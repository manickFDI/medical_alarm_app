# from flask.ext.mongoalchemy import MongoAlchemy

from flask.ext.pymongo import PyMongo


def init_app(app):
    global mongo
    mongo = PyMongo(app)
    return 0


def showUsers():
    cur = mongo.db.user.find()
    users = ""
    for item in cur:
        users = users + " " + item['name']
    return users
