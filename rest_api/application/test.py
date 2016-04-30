from datetime import date, timedelta, datetime
import calendar

from database.mongo_connector import MongoDatabase

mongo = MongoDatabase()
mongo.get_points_in_time_window(4, 1462007127000)
