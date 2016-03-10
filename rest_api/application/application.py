import json
from flask import Flask, request, Response, g, jsonify
from flask.ext.restful import Resource, Api

#Own Imports
from database.mysql_connector import MysqlDatabase
from utils import RegexConverter


# Constants for hypermedia formats and profiles
COLLECTIONJSON = "application/vnd.collection+json"
HAL = "application/hal+json"
ACCOUNTING_USER_PROFILE = "http://schema.org/Person"

# Define the application and the api
app = Flask(__name__)
app.debug = True
# Set the API's databases. Change the DATABASE_DBTYPE value from app.config to modify the
# database to be used (for instance for testing)
app.config.update({'MYSQL_DATABASE': MysqlDatabase()})
# Start the RESTful API.
mysqldb = app.config['MYSQL_DATABASE']
api = Api(app)


class Users(Resource):
    def post(self):
        return "POST Users"


class User(Resource):
    def get(self, nickname):
        return mysqldb.showUsers(nickname)

# Add the Regex Converter so we can use regex expressions when we define the
# routes
app.url_map.converters['regex'] = RegexConverter


# Define the routes
api.add_resource(Users, '/malarm/api/users/', endpoint='users')
api.add_resource(User, '/malarm/api/users/<nickname>/', endpoint='user')


# Start the application
# DATABASE SHOULD HAVE BEEN POPULATED PREVIOUSLY
if __name__ == '__main__':
    # Debug True activates automatic code reloading and improved error messages
    app.run(debug=True)