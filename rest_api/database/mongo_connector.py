# from flask.ext.mongoalchemy import MongoAlchemy

from flask.ext.pymongo import PyMongo
from datetime import date, timedelta, datetime
import calendar


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


def get_points_in_time_window(user_id, timestamp):
    aux_id = int(user_id)
    aux_time = int(timestamp)
    date_ts = datetime.datetime.fromtimestamp(aux_time)
    lower_ts = calendar.timegm((date_ts - timedelta(minutes=5)).utctimetuple())
    upper_ts = calendar.timegm((date_ts + timedelta(minutes=5)).utctimetuple())

    cur = mongo.db.sensors.find({"user_id": {"$ne": aux_id}, "timestamp": {"$gte": lower_ts, "$lte": upper_ts}})

    points = []
    for item in cur:
        point = {}
        point['user_id'] = item['user_id']
        point['location'] = item['location']
        point['timestamp'] = item['timestamp']
        point['magnetometer'] = item['magnetometer']
        point['accelerometer'] = item['accelerometer']
        point['light'] = item['light']
        point['battery'] = item['battery']
        points.append(point)

    return points


def get_points_by_user_and_time(user_id, time_window):
    aux_id = int(user_id)
    aux_time = int(time_window)
    limit_date = date.today() - timedelta(days=aux_time)
    limit_ts = calendar.timegm(limit_date.utctimetuple())
    cur = mongo.db.sensors.find({"user_id": {"$eq": aux_id}, "timestamp": {"$gte": limit_ts}})

    points = []
    for item in cur:
        point = {}
        point['user_id'] = item['user_id']
        point['location'] = item['location']
        point['timestamp'] = item['timestamp']
        point['magnetometer'] = item['magnetometer']
        point['accelerometer'] = item['accelerometer']
        point['light'] = item['light']
        point['battery'] = item['battery']
        points.append(point)

    return points