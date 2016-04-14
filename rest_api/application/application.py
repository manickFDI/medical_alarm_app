import json
from flask import Flask, request, Response, jsonify
from flask.ext.restful import Resource, Api
# Own Imports
from database import mongo_connector
from database.mysql_connector import MysqlDatabase
from utils import RegexConverter

# Constants for hypermedia formats and profiles
COLLECTIONJSON = "application/vnd.collection+json"
HAL = "application/hal+json"
ACCOUNTING_USER_PROFILE = "http://schema.org/Person"

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


"""
An user corresponds to:

+-----------------+-------------+------+-----+---------+-------+
| Field           | Type        | Null | Key | Default | Extra |
+-----------------+-------------+------+-----+---------+-------+
| idUsuario       | int(11)     | NO   | PRI | NULL    |       |
| nombre          | varchar(45) | NO   |     | NULL    |       |
| apellidos       | varchar(45) | NO   |     | NULL    |       |
| email           | varchar(45) | NO   | UNI | NULL    |       |
| fechaNacimiento | varchar(10) | NO   |     | NULL    |       |
| sexo            | int(11)     | YES  |     | NULL    |       |
| peso            | double      | YES  |     | NULL    |       |
| DNI             | varchar(9)  | NO   | UNI | NULL    |       |
| secret          | varchar(45) | NO   |     | NULL    |       |
| salt            | varchar(45) | NO   |     | NULL    |       |
| estado          | int(11)     | NO   |     | NULL    |       |
+-----------------+-------------+------+-----+---------+-------+
"""


class Users(Resource):
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


# Add the Regex Converter so we can use regex expressions when we define the
# routes
app.url_map.converters['regex'] = RegexConverter

# Define the routes
api.add_resource(Users, '/malarm/api/users/', endpoint='users')
api.add_resource(User, '/malarm/api/user/<dni>/', endpoint='user')
api.add_resource(Sensors, '/malarm/api/sensors/', endpoint='sensors')

# Start the application
# DATABASE SHOULD HAVE BEEN POPULATED PREVIOUSLY
if __name__ == '__main__':
    # Debug True activates automatic code reloading and improved error messages
    app.run(debug=True)
