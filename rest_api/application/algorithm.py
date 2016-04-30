import os
import sys
from functools import partial

# Path for spark source folder
from itertools import combinations

os.environ['SPARK_HOME'] = "/usr/local/Cellar/apache-spark/1.5.2"
# Append pyspark  to Python Path
sys.path.append("/usr/local/Cellar/apache-spark/1.5.2/libexec/python/")

from flask import json
from pyspark import SparkContext, SparkConf
from shapely.geometry import LineString, mapping, Point


def initializeSpark(appName, sparkMaster):
    conf = SparkConf().setAppName(appName).setMaster(sparkMaster).set("spark.driver.allowMultipleContexts", "true")
    global sc
    sc = SparkContext(conf=conf)


def stopContext():
    sc.stop()


def getFocusForLines(lines):
    shapes = sc.parallelize(lines).reduce(lambda line1, line2: line1.intersection(line2))
    return shapes


def calculateFocusForNElems(lines, i):
    j = 0
    result = []
    while j < len(lines):
        aux = lines.pop(0)
        result.append(getFocusForLines(lines))
        lines.append(aux)
        j += 1

    return result


def combine(items):
    return sum([map(list, combinations(items, i)) for i in range(len(items) + 1)], [])


def calculateFocus(lines):
    initializeSpark("Malarm", "local[2]")
    lineCombinations = combine(lines)
    result = []
    for lineCombination in lineCombinations:
        if len(lineCombination) > 1:
            focusForLines = getFocusForLines(lineCombination)
            if not focusForLines.is_empty:
                combination = {}
                combination['num_users'] = len(lineCombination)
                combination['points'] = mapping(focusForLines)
                result.append(combination)

    stopContext()
    return result


def get_points_in_time(point, U, M):
    points = M.value.get_points_in_time_window(U.value['user_id'], point['timestamp'])
    print(points)
    ret = {}
    ret['point'] = point
    ret['points'] = points
    return ret


def compare_points_location(point_pair, D):
    points = []
    print(point_pair)
    user_point = Point(point_pair['point']['location']['coordinates'][0], point_pair['point']['location']['coordinates'][1])
    for possible_point in point_pair['points']:
        aux = Point(possible_point['location']['coordinates'][0], point_pair['point']['location']['coordinates'][1])
        if user_point.distance(aux) <= D.value:
            points.append(possible_point)
    return points


def calculateContagion(user, time_window, distance, mongodb):
    initializeSpark("Malarm", "local[2]")
    D = sc.broadcast(distance)
    U = sc.broadcast(user)
    M = sc.broadcast(mongodb)
    points = sc.parallelize(mongodb.get_points_by_user_and_time(user['user_id'], time_window))
    users = points.flatMap(partial(get_points_in_time, U=U, M=M)).cache()\
        .map(partial(compare_points_location, D=D)).collect()
    stopContext()
    return users
