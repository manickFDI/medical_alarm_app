import json
from flask import Flask, request, Response, jsonify
from flask.ext.restful import Resource, Api
# Own Imports
from database import mongo_connector
from database.mysql_connector import MysqlDatabase
from utils import RegexConverter

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
app.config['MONGO_DBNAME'] = 'malarm'
mongo_connector.init_app(app)

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
        return Response(status=201,
                        headers={"Location": api.url_for(User, id=_id)}
                        )


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

        user_db = mysqldb.get_user(dni)

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

        user_db = mysqldb.get_user(dni)

        # PERFORM OPERATIONS
        if not user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % dni,
                                         "User")
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

    def put(self, dni):
        # PARSE THE REQUEST:
        input = request.get_json(force=True)
        if not input:
            return create_error_response(415, "Unsupported Media Type",
                                         "Use a JSON compatible format",
                                         "User")
            # Get the password sent through post body
        input_data = input['status']
        _currentStatus = input_data['current_status']
        _newStatus = input_data['new_status']
        _idContagion = input_data['id_contagion']

        user_db = mysqldb.get_user(dni)

        # PERFORM OPERATIONS
        if not user_db:
            return create_error_response(404, "Unknown user",
                                         "There is no a user with dni %s"
                                         % dni,
                                         "User")

        if not mysqldb.update_user_satus(user_db, _currentStatus, _newStatus, _idContagion):
            return create_error_response(500, "User update error",
                                         "Error when updating the user %s status"
                                         % dni,
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
        disease_db = mysqldb.get_disease(name);

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

        return mongo_connector.getNearByLocations(_distance, _latitude, _longitude)

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

        _userId = input_data['user_id']
        _timestamp = input_data['timestamp']
        _latitude = input_data['latitude']
        _longitude = input_data['longitude']
        _magnetometer = input_data['magnetometer']
        _accelerometer = input_data['accelerometer']
        _light = input_data['light']
        _battery = input_data['battery']

        if not _userId or not _timestamp or not _latitude or not _longitude \
                or not _magnetometer or not _accelerometer or not _light or not _battery:
            return create_error_response(400, "Wrong request format",
                                         "Be sure you include all mandatory properties",
                                         "Sensors")

        _id = mongo_connector.insert_sensor_value(_userId, _timestamp, _latitude, _longitude, _magnetometer,
                                                  _accelerometer, _light, _battery)

        # CREATE RESPONSE AND RENDER
        return Response(status=201,
                        headers={"Location": api.url_for(Sensors, id=_userId)}
                        )


class Focuses(Resource):
    def get(self):
        return mysqldb.get_focuses()


class UsersContagions(Resource):

    def get(self):
        disease = request.args['disease']

        if disease is not None:
            return mysqldb.get_users_contagions(disease)
        else:
            return []


class Contagions(Resource):

    def get(self):
        return mysqldb.get_active_contagions()


class Contagion(Resource):

    def delete(self, id):

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


class Focus(Resource):
    def delete(self, id):

        # PEROFRM OPERATIONS
        # Try to delete the focus. If it could not be deleted, the database
        # returns False.
        if mysqldb.delete_focus(id):
            # RENDER RESPONSE
            return '', 204
        else:
            # GENERATE ERROR RESPONSE
            return create_error_response(404, "Unknown focus",
                                         "There is no a focus with id %s"
                                         % id,
                                         "Focus")
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
# Start the application
# DATABASE SHOULD HAVE BEEN POPULATED PREVIOUSLY
if __name__ == '__main__':
    # Debug True activates automatic code reloading and improved error messages
    app.run(debug=True)
