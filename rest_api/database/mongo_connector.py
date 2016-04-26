# from flask.ext.mongoalchemy import MongoAlchemy

from flask.ext.pymongo import PyMongo


def init_app(app):
    global mongo
    mongo = PyMongo(app)
    return 0


def insert_sensor_value(_userId, _timestamp, _latitude, _longitude, _magnetometer, _accelerometer, _light, _battery):
    data = {}
    location = {}
    coordinates = [_latitude, _longitude]

    location['type'] = "Point"
    location['coordinates'] = coordinates
    data['user_id'] = _userId
    data['location'] = location
    data['timestamp'] = _timestamp
    data['magnetometer'] = _magnetometer
    data['accelerometer'] = _accelerometer
    data['light'] = _light
    data['battery'] = _battery

    cur = mongo.db.sensors.insert(data)

    return _userId


def getNearByLocations(_maxDistance, _latitude, _longitude):
    cur = mongo.db.sensors.find({"location": {"$nearSphere": {"$geometry": {"type": "Point", "coordinates": [
        float(_latitude), float(_longitude)]}, "$maxDistance": int(_maxDistance)}}})

    nearLocations = []
    for item in cur:
        loc = {}
        loc['timestamp'] = item['timestamp']
        loc['location'] = item['location']
        loc['user_id'] = item['user_id']
        nearLocations.append(loc)

    return nearLocations


def getPointsGroupedByUser(user_id):
    #cur = mongo.db.sensors.aggregate([{"$group": {"_id": {"user_id": "$user_id"}, "locations": {
    #   "$push": {"type": "$location.type", "coordinates": "$location.coordinates"}}}}])
    aux_id = int(user_id)
    cur = mongo.db.sensors.find({"user_id": aux_id})
    points = []

    for item in cur:
        user = item
        points.append(user)

    return points
