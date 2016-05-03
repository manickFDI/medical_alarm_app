# from flask.ext.mongoalchemy import MongoAlchemy

from datetime import date, timedelta, datetime
import time

from pymongo import Connection

db = None


class MongoDatabase(object):
    def init_app(self):
        global db
        if db is None:
            connection = Connection()
            db = connection['malarm']
        return 0

    def insert_sensor_value(self, _userId, _timestamp, _latitude, _longitude, _magnetometer, _accelerometer, _light,
                            _battery):
        self.init_app()
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

        cur = db.sensors.insert(data)

        return _userId

    def getNearByLocations(self, _maxDistance, _latitude, _longitude):
        self.init_app()
        cur = db.sensors.find({"location": {"$nearSphere": {"$geometry": {"type": "Point", "coordinates": [
            float(_latitude), float(_longitude)]}, "$maxDistance": int(_maxDistance)}}})

        nearLocations = []
        for item in cur:
            loc = {}
            loc['timestamp'] = item['timestamp']
            loc['location'] = item['location']
            loc['user_id'] = item['user_id']
            nearLocations.append(loc)

        return nearLocations

    def getPointsGroupedByUser(self, user_id):
        self.init_app()
        # cur = mongo.db.sensors.aggregate([{"$group": {"_id": {"user_id": "$user_id"}, "locations": {
        #   "$push": {"type": "$location.type", "coordinates": "$location.coordinates"}}}}])
        aux_id = int(user_id)
        cur = db.sensors.find({"user_id": aux_id})
        points = []

        for item in cur:
            user = item
            points.append(user)

        return points

    def get_points_in_time_window(self, user_id, timestamp):
        self.init_app()
        aux_id = int(user_id)
        aux_time = int(timestamp) / 1000
        date_ts = datetime.fromtimestamp(aux_time)
        lower_ts = long(time.mktime((date_ts - timedelta(minutes=5)).timetuple()))*1000
        upper_ts = long(time.mktime((date_ts + timedelta(minutes=5)).timetuple()))*1000

        cur = db.sensors.find({"user_id": {"$ne": aux_id}, "timestamp": {"$gte": lower_ts, "$lte": upper_ts}})

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

    def get_points_by_user_and_time(self, user_id, time_window):
        self.init_app()
        aux_id = int(user_id)
        aux_time = int(time_window)
        limit_date = date.today() - timedelta(days=aux_time)
        limit_ts = time.mktime(limit_date.timetuple())
        cur = db.sensors.find({"user_id": {"$eq": aux_id}, "timestamp": {"$gte": limit_ts}})

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

    def get_points_by_user_and_forward_time(self, user_id, time_window, timestamp):
        self.init_app()
        aux_id = int(user_id)
        aux_time_window = int(time_window)
        aux_time = int(timestamp) / 1000
        date_ts = datetime.fromtimestamp(aux_time)
        limit_date = date_ts + timedelta(minutes=aux_time_window)
        limit_ts = time.mktime(limit_date.timetuple())*1000
        cur = db.sensors.find({"user_id": {"$eq": aux_id}, "timestamp": {"$gte": timestamp, "$lte": limit_ts}})

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


"""
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
    date_ts = datetime.fromtimestamp(aux_time)
    lower_ts = calendar.timegm((date_ts - timedelta(minutes=5)).timetuple())
    upper_ts = calendar.timegm((date_ts + timedelta(minutes=5)).timetuple())
    lower_ts = int(lower_ts)/1000
    upper_ts = int(upper_ts)/1000
    print(aux_id)
    print(lower_ts)
    print(upper_ts)
    print(mongo)

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
    limit_ts = calendar.timegm(limit_date.timetuple())
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
"""
