from datetime import datetime
from flask import Flask, request, Response, jsonify
from flask.ext.restful import Resource, Api
# Own Imports
import algorithm
from database.mongo_connector import MongoDatabase
from database.mysql_connector import MysqlDatabase
from utils import RegexConverter
import requests
import time

# Define the application and the api
app = Flask(__name__)
app.debug = True
# Set the API's databases. Change the DATABASE_DBTYPE value  from app.config to modify the
# database to be used (for instance for testing)
# MySQL
MYSQL_DB_PATH = "../database/schema.sql"
app.config.update({'MYSQL_DATABASE': MysqlDatabase()})
mysqldb = app.config['MYSQL_DATABASE']
mysqldb.init(MYSQL_DB_PATH)
# MongoDB
app.config.update({'MONGO_DATABASE': MongoDatabase()})
app.config['MONGO_DBNAME'] = 'malarm'
mongodb = app.config['MONGO_DATABASE']
mongodb.init_app()

# Start the RESTful API.
api = Api(app)


def create_error_response(status_code, title, message, resource_type=None):
    response = jsonify(title=title, message=message, resource_type=resource_type)
    response.status_code = status_code
    return response


@app.errorhandler(404)
def resource_not_found(error):
    return create_error_response(404, "Resource not found", "This resource url does not exit")


@app.errorhandler(500)
def unknown_error(error):
    return create_error_response(500, "Error", "The system has failed. Please, contact the administrator")


def geocode(place):
    base = "https://maps.googleapis.com/maps/api/geocode/json?"
    params = "address={addr}&key={api_key}".format(
            addr=place,
            api_key='AIzaSyDUyflHx7Y6ytJSMRZK6mdO_T4wXb7GQ_U'
    )
    url = "{base}{params}".format(base=base, params=params)
    response = requests.get(url)
    data = response.json()
    return data['results'][0]['geometry']['location']


def reverse_geocode(coordinates, zone):
    # grab some lat/long coords from wherever. For this example,
    # I just opened a javascript console in the browser and ran:
    #
    # navigator.geolocation.getCurrentPosition(function(p) {
    #   console.log(p);
    # })
    #
    latitude = coordinates[0]
    longitude = coordinates[1]

    # Did the geocoding request comes from a device with a
    # location sensor? Must be either true or false.
    sensor = 'true'

    base = "http://maps.googleapis.com/maps/api/geocode/json?"
    params = "latlng={lat},{lon}&sensor={sen}&key{api_key}".format(
            lat=latitude,
            lon=longitude,
            sen=sensor,
            api_key='AIzaSyDUyflHx7Y6ytJSMRZK6mdO_T4wXb7GQ_U'
    )
    url = "{base}{params}".format(base=base, params=params)
    response = requests.get(url)
    data = response.json()
    if not zone:
        return data['results'][0]['formatted_address']
    else:
        return data['results'][0]['address_components'][2]['short_name']


class Users(Resource):
    def get(self):
        _type = request.args['type']
        if _type == "status":
            return mysqldb.get_total_user_status()
        else:
            return {}

    def post(self):
        """
        Adds a new user in the database.
        ENTITY BODY INPUT FORMAT:
        * Media type: Collection+JSON:
             http://amundsen.com/media-types/collection/
            * Profile:
                http://schema.org/Person
        The body should be a Collection+JSON template.
        Semantic descriptors used in template:
            nombre (mandatory) - name
            apellido (mandatory) - last name
            email (mandatory)
            fechaNacimiento (mandatory) - birthday
            sexo () - gender
            peso () - weight
            DNI (mandatory) - ID number
            secret (mandatory) - password

            id, state and salt are not given because they are calculated by the API or the database.

        OUTPUT:
        Returns 201 + the url of the new resource in the Location header
        Return 409 Conflict if there is another user with the same email or ID number
        Return 400 if the body is not well formed
        Return 415 if it receives a media type different from application/json
        """
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
        # Get the request body and serialize it to object
        # We should check that the format of the request body is correct. Check
        # That mandatory attributes are there.

        input_data = input['user']

        _name = input_data['name']
        _lastName = input_data['lastname']
        _email = input_data['email']
        _birthday = input_data['birthday']
        _gender = input_data['gender']
        _weight = input_data['weight']
        _idNumber = input_data['idnumber']
        _secret = input_data['secret']

        # TODO: use REGEX to verify birthday format

        if not _name or not _lastName or \
                not _email or not _birthday or not _idNumber or not _secret:
            return create_error_response(400, "Wrong request format",
                                         "Be sure you include all mandatory properties",
                                         "User")
        if not _gender:
            _gender = 'NULL'

        if not _weight:
            _weight = 'NULL'

            # Conflict if user already exist
        if mysqldb.contains_user_email(_email):
            return create_error_response(400, "Wrong nickname",
                                         "There is already a user with same email %s.\
                                          Try another one " % _email,
                                         "User")
        elif mysqldb.contains_user_id_number(_idNumber):
            return create_error_response(400, "Wrong nickname",
                                         "There is already a user with same id number %s.\
                                          Try another one " % _idNumber,
                                         "User")

        # TODO: calculate salt and encrypt password (secret)
        _salt = "salta con migo salta"

        _id = mysqldb.create_user(_name, _lastName, _email, _birthday, _gender, _weight, _idNumber, _secret, _salt, 0)

        # CREATE RESPONSE AND RENDER
        return '', 204


class User(Resource):
    def post(self, dni):

        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['user']
        _secret = input_data['secret']

        user_db = mysqldb.get_user_by_dni(dni)

        # PERFORM OPERATIONS
        if not user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % dni,
                                         "User")

        # TODO: Check password and salt first: change to POST
        # FILTER AND GENERATE RESPONSE
        # Create the envelope:
        envelope = {}

        envelope['idusuario'] = user_db['user_id']
        envelope['name'] = user_db['name']
        envelope['lastname'] = user_db['lastname']
        envelope['email'] = user_db['email']
        envelope['birthday'] = user_db['birthday']
        envelope['gender'] = user_db['gender']
        envelope['weight'] = user_db['weight']
        envelope['idnumber'] = user_db['idnumber']
        envelope['state'] = user_db['state']

        return envelope

    def get(self, dni):

        user_db = mysqldb.get_user_by_dni(dni)

        # PERFORM OPERATIONS
        if not user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % dni,
                                         "User")
        # FILTER AND GENERATE RESPONSE
        # Create the envelope:
        return user_db

    def put(self, dni):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['user']
        _newStatus = input_data['new_status']

        user_db = mysqldb.get_user_by_dni(dni)

        # PERFORM OPERATIONS
        if not user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % dni,
                                         "User")

        if _newStatus != "dead" or not mysqldb.update_user_satus(user_db):
            return create_error_response(500, "User update error",
                                         "Error updating user to status: %s"
                                         % _newStatus,
                                         "User")

        # RENDER RESPONSE
        return '', 204


class Diseases(Resource):
    def get(self):
        _type = request.args['type']
        _top = request.args['top'].replace("\"", "")

        if _type == "d":  # For deaths
            return mysqldb.get_top_deaths(_top)
        elif _type == "c":  # For contagions
            return mysqldb.get_top_contagions(_top)
        else:
            return {}


class Disease(Resource):
    def get(self, name):
        disease_db = mysqldb.get_disease_by_name(name);

        # PERFORM OPERATIONS
        if not disease_db:
            return create_error_response(404, "Unknown disease",
                                         "There is no a disease with name %s"
                                         % name,
                                         "Disease")
        # FILTER AND GENERATE RESPONSE
        # Create the envelope:
        envelope = {}

        envelope['iddisease'] = disease_db['disease_id']
        envelope['name'] = disease_db['name']
        envelope['eradicated'] = disease_db['eradicated']
        envelope['numdeaths'] = disease_db['num_deaths']
        envelope['numcontagions'] = disease_db['num_contagions']
        envelope['numchildren'] = disease_db['num_children']
        envelope['numteenagers'] = disease_db['num_teenagers']
        envelope['numadults'] = disease_db['num_adults']
        envelope['numelders'] = disease_db['num_elders']
        envelope['numwomen'] = disease_db['num_women']
        envelope['nummen'] = disease_db['num_men']
        envelope['weight'] = disease_db['weight']

        return envelope


class Sensors(Resource):
    def get(self):
        _distance = request.args['distance']
        _longitude = request.args['longitude'].replace("\"", "")
        _latitude = request.args['latitude'].replace("\"", "")

        return mongodb.getNearByLocations(_distance, _latitude, _longitude)

    def post(self):
        """
        Adds a new user in the database.
        ENTITY BODY INPUT FORMAT:
        * Media type: Collection+JSON:
             http://amundsen.com/media-types/collection/
            * Profile: Sensor
                {
                   _id: ObjectId(),
                   user_id: <>,
                   timestamp: <>,
                   latitude: <>,
                   longitude: <>,
                   altitude: <>,
                   magnetometer: <>,
                   accelerometer: <>,
                   light: <>,
                   battery: <>
                }
        The body should be a Collection+JSON template.
        Semantic descriptors used in template: All fields are mandatory, but they can have the value -1 meaning no-value

        OUTPUT:
        Returns 201 + the url of the new resource in the Location header
        Return 409 Conflict if there is another user with the same email or ID number
        Return 400 if the body is not well formed
        Return 415 if it receives a media type different from application/json
        """
        """
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
        # Get the request body and serialize it to object
        # We should check that the format of the request body is correct. Check
        # That mandatory attributes are there.
        """
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
        input_data = input['sensor']

        _userDni = input_data['user_dni']
        _timestamp = input_data['timestamp']
        _latitude = input_data['latitude']
        _longitude = input_data['longitude']
        _magnetometer = input_data['magnetometer']
        _accelerometer = input_data['accelerometer']
        _light = input_data['light']
        _battery = input_data['battery']

        _userId = mysqldb.get_user_by_dni(_userDni)['user_id']

        if not _userId or not _timestamp or not _latitude or not _longitude \
                or not _magnetometer or not _accelerometer or not _light or not _battery:
            return create_error_response(400, "Wrong request format",
                                         "Be sure you include all mandatory properties",
                                         "Sensors")

        _id = mongodb.insert_sensor_value(_userId, _timestamp, _latitude, _longitude, _magnetometer,
                                          _accelerometer, _light, _battery)

        # CREATE RESPONSE AND RENDER
        return Response(status=201,
                        headers={"Location": api.url_for(Sensors, id=_userId)}
                        )


class UsersContagions(Resource):
    def get(self):
        disease = request.args['disease']

        if disease is not None:
            return mysqldb.get_users_contagions(disease)
        else:
            return []

    def post(self):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['user_contagion']
        _user_id = input_data['user_id']
        _contagion_id = input_data['contagion_id']

        if mysqldb.insert_user_contagion(_user_id, _contagion_id):
            if mysqldb.update_disease_with_new_infected(_user_id, _contagion_id):
                return '', 204

            return create_error_response(403, "Error updating disease",
                                         "There is no a disease or contagion with the provided ids",
                                         "UserContagions")

        return create_error_response(403, "Error updating infected user",
                                     "There is no a user contagion with the provided ids",
                                     "UserContagions")

    def delete(self):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['user_contagion']
        _user_id = input_data['user_id']
        _contagion_id = input_data['contagion_id']

        if mysqldb.delete_user_contagion(_user_id, _contagion_id):
            return '', 204

        return create_error_response(403, "Error updating disease",
                                     "There is no a disease or contagion with the provided ids",
                                     "UserContagions")


class Contagions(Resource):
    def get(self):

        _type = request.args['type']

        if _type is not None and _type == 'coordinates':
            contagions = mysqldb.get_active_contagions()
            envelope = []
            for contagion in contagions:
                coordinates = geocode(contagion['place'])
                envelope.append(coordinates)
            return envelope

        elif _type == 'contagions':
            return mysqldb.get_active_contagions()

        else:
            return {}

    def post(self):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['user_contagion']
        _user_dni = input_data['user_dni']
        _distance = input_data['distance']
        _time_window = input_data['time_window']
        _exposure = input_data['exposure']
        _disease_name = input_data['disease']
        _id_doctor = input_data['doctor_id']
        _ts_date = input_data['date']
        aux_time = int(_ts_date) / 1000
        _date = (datetime.fromtimestamp(aux_time)).strftime('%d/%m/%Y')
        _s_level = input_data['level']
        _level = 0
        if _s_level == "bajo":
            _level = 1
        elif _s_level == "medio":
            _level = 2
        elif _s_level == "alto":
            _level = 3
        _description = input_data['description']

        user = mysqldb.get_user_by_dni(_user_dni)

        if user is None:
            return create_error_response(403, "Error user not found",
                                         "There is no user with the provided dni: %s", _user_dni,
                                         "User")

        infected_users = algorithm.calculateContagion(user, _time_window, _distance, _exposure, mongodb)

        if infected_users is not None and len(infected_users) > 0:
            contagion = {}
            contagion['disease_id'] = mysqldb.get_disease_by_name(_disease_name)['disease_id']
            contagion['doctor_id'] = _id_doctor
            contagion['time_window'] = _time_window
            contagion['distance'] = _distance
            contagion['date'] = _date
            contagion['level'] = _level
            contagion['description'] = _description
            contagion['users'] = []

            contagions = []
            for list in infected_users:
                for points in list:
                    zone = reverse_geocode(points['contagion_point'][0]['location']['coordinates'], True)
                    user1 = points['contagion_point'][0]['user_id']
                    user2 = points['contagion_point'][1]['user_id']
                    contagion['users'].append(user1)
                    contagion['users'].append(user2)
                    contagion['zone'] = zone
                    if not zone in contagions:
                        mysqldb.insert_contagion(contagion)
                        contagions.append(contagion)

        return contagions


class Contagion(Resource):
    def put(self, id):

        # PEROFRM OPERATIONS
        # Try to delete the contagion. If it could not be deleted, the database
        # returns False.
        if mysqldb.delete_contagion(id):
            # RENDER RESPONSE
            return '', 204
        else:
            # GENERATE ERROR RESPONSE
            return create_error_response(404, "Unknown contagion",
                                         "There is no a contagion with id %s"
                                         % id,
                                         "Contagion")


class Focuses(Resource):
    def get(self):
        return mysqldb.get_focuses()

    def post(self):
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['focus_users']
        _description = input['description']
        _doctorId = input['doctor_id']

        lines = []
        _numUsers = 0
        _users = []
        for dni in input_data:
            user = mysqldb.get_user_by_dni(dni['dni'])
            if user is not None:
                _numUsers += 1
                _users.append(user)
                locations = mongodb.getPointsGroupedByUser(user['user_id'])
                lineParts = []
                for location in locations:
                    lineParts.append(
                        Point(location['location']['coordinates'][0], location['location']['coordinates'][1]))
                if len(lineParts) > 1:
                    lines.append(LineString(lineParts))
        result = {}
        ret = []
        if len(lines) > 1:
            result = algorithm.calculateFocus(lines)
            if len(result) > 0:
                focus = {}
                focus['description'] = _description
                focus['doctor_id'] = _doctorId
                focus['num_users'] = _numUsers
                focus['focus_id'] = mysqldb.insert_focus(focus)
                for user in _users:
                    mysqldb.insert_user_focus(focus, user)

                date = time.strftime('%d/%m/%Y')
                for placeResult in result:
                    points = placeResult['points']
                    newPoints = []
                    if len(points) > 2:
                        for point in points:
                            if len(point['coordinates']) > 1:
                                for coordinate in point['coordinates']:
                                    addr = reverse_geocode(coordinate, False)
                                    aux = {}
                                    aux['address'] = addr
                                    aux['coordinates'] = coordinate
                                    mysqldb.insert_focus_place(focus, aux, date)
                                    newPoints.append(aux)
                            else:
                                addr = reverse_geocode(point['coordinates'], False)
                                point['address'] = addr
                                mysqldb.insert_focus_place(focus, point, date)
                                newPoints.append(point)
                    else:
                        if len(points['coordinates']) > 1:
                            for coordinate in points['coordinates']:
                                addr = reverse_geocode(coordinate, False)
                                aux = {}
                                aux['address'] = addr
                                aux['coordinates'] = coordinate
                                mysqldb.insert_focus_place(focus, aux, date)
                                newPoints.append(aux)
                        else:
                            addr = reverse_geocode(points['coordinates'], False)
                            points['address'] = addr
                            mysqldb.insert_focus_place(focus, points, date)
                            newPoints.append(points)

                    newPlaceResult = {}
                    newPlaceResult['num_users'] = placeResult['num_users']
                    newPlaceResult['points'] = newPoints
                    ret.append(newPlaceResult)

        return ret


class Focus(Resource):
    def delete(self, id):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['focus_place']
        _place = input_data['place']

        # PEROFRM OPERATIONS
        # Try to delete the focus. If it could not be deleted, the database
        # returns False.
        if mysqldb.delete_place_focus(id, _place):
            # RENDER RESPONSE
            return '', 204
        else:
            # GENERATE ERROR RESPONSE
            return create_error_response(404, "Unknown focus",
                                         "There is no a focus with id %s"
                                         % id,
                                         "Focus")


class Notifications(Resource):
    def get(self):
        _dni = request.args['dni']

        _user_db = mysqldb.get_user_by_dni(_dni)

        # PERFORM OPERATIONS
        if not _user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % _dni,
                                         "Notifications")
        envelope = {}
        envelope['user'] = _user_db
        envelope['notifications'] = mysqldb.get_notifications(_user_db)

        return envelope


class Notification(Resource):
    def put(self):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['notification']
        _user_id = input_data['user_id']
        _contagion_id = input_data['contagion_id']

        if mysqldb.update_notification(_user_id, _contagion_id):
            return '', 204

        return create_error_response(403, "Error updating notification",
                                     "There is no a notification with the provided ids",
                                     "Notification")

    def delete(self):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['notification']
        _user_id = input_data['user_id']
        _contagion_id = input_data['contagion_id']

        if mysqldb.delete_notification(_user_id, _contagion_id):
            return '', 204

        return create_error_response(403, "Error updating notification",
                                     "There is no a notification with the provided ids",
                                     "Notification")


class Dispersion(Resource):
    def get(self, name):
        disease_db = mysqldb.get_disease_by_name(name)

        # PERFORM OPERATIONS
        if not disease_db:
            return create_error_response(404, "Unknown disease",
                                         "There is no a disease with name %s"
                                         % name,
                                         "Dispersion")
        dispersion = {}
        dispersion['data'] = mysqldb.get_dispersion(disease_db['disease_id'])

        return dispersion


class News(Resource):
    def get(self):
        _dni = request.args['user_dni']
        user = mysqldb.get_user_by_dni(_dni)
        return mysqldb.get_user_news(user['user_id'])

    def delete(self):
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
        input_data = input['user_news']

        _userId = input_data['user_dni']
        _newsId = input_data['news_id']

        user = mysqldb.get_user_by_dni(_userId)
        mysqldb.delete_user_news(user['user_id'], _newsId)

        return '', 204


class Status(Resource):
    def get(self):
        _user = request.args['user_dni']
        _lat = request.args['lat']
        _lng = request.args['lng']

        status = {}
        status['user'] = mysqldb.get_user_by_dni(_user)
        status['zone'] = mysqldb.get_contagions_by_zone(reverse_geocode([_lat, _lng], True))

        return status


# Add the Regex Converter so we can use regex expressions when we define the
# routes
app.url_map.converters['regex'] = RegexConverter

# Define the routes
api.add_resource(Users, '/malarm/api/users/', endpoint='users')
api.add_resource(User, '/malarm/api/user/<dni>/', endpoint='user')
api.add_resource(Sensors, '/malarm/api/sensors/', endpoint='sensors')
api.add_resource(Diseases, '/malarm/api/diseases/', endpoint='diseases')
api.add_resource(Disease, '/malarm/api/disease/<name>/', endpoint='disease')
api.add_resource(Focus, '/malarm/api/focus/<id>/', endpoint='focus')
api.add_resource(Focuses, '/malarm/api/focuses/', endpoint='focuses')
api.add_resource(Contagion, '/malarm/api/contagion/<id>/', endpoint='contagion')
api.add_resource(Contagions, '/malarm/api/contagions/', endpoint='contagions')
api.add_resource(UsersContagions, '/malarm/api/users/contagions/', endpoint='users_contagions')
api.add_resource(Notifications, '/malarm/api/user/notifications/', endpoint='user_notifications')
api.add_resource(Notification, '/malarm/api/notification/', endpoint='notification')
api.add_resource(Dispersion, '/malarm/api/dispersion/<name>/', endpoint='dispersion')
api.add_resource(News, '/malarm/api/news/', endpoint='news')
api.add_resource(Status, '/malarm/api/user/status/', endpoint='status')
# Start the application
# DATABASE SHOULD HAVE BEEN POPULATED PREVIOUSLY
if __name__ == '__main__':
    # Debug True activates automatic code reloading and improved error messages
    app.run(debug=True)
