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


def get_points_in_time(user_point, U, M):
    points = M.value.get_points_in_time_window(U.value['user_id'], user_point['timestamp'])
    return ((user_point, point) for point in points)


def calculate_exposure(point_pair, D, E, M):
    initial_user_points = M.value.get_points_by_user_and_forward_time(point_pair[0]['user_id'],E.value,point_pair[0]['timestamp'])
    possible_contagion_user_points = M.value.get_points_by_user_and_forward_time(point_pair[1]['user_id'],E.value,point_pair[1]['timestamp'])

    already_failed = False
    finished = False
    i = 0
    j = 0
    while not finished and i < len(initial_user_points):
        while not already_failed and j < len(possible_contagion_user_points):
            user_point = Point(initial_user_points[i]['location']['coordinates'][0],
                               initial_user_points[i]['location']['coordinates'][1])
            possible_point = Point(possible_contagion_user_points[j]['location']['coordinates'][0],
                                   possible_contagion_user_points[j]['location']['coordinates'][1])
            if user_point.distance(possible_point) <= D.value:
                already_failed = False
            else:
                if already_failed:
                    return False
                else:
                    already_failed = True
            j += 1
        i += 1

    return True


def compare_points_location(point_pair, D, E, M):
    points = []
    user_point = Point(point_pair[0]['location']['coordinates'][0], point_pair[0]['location']['coordinates'][1])
    possible_point = Point(point_pair[1]['location']['coordinates'][0], point_pair[1]['location']['coordinates'][1])
    if user_point.distance(possible_point) <= D.value and calculate_exposure(point_pair, D, E, M):
        cross = {}
        cross['contagion_point']=point_pair
        points.append(cross)
    return points


def calculateContagion(user, time_window, distance, exposure_time, mongodb):
    initializeSpark("Malarm", "local[2]")
    D = sc.broadcast(distance)
    U = sc.broadcast(user)
    M = sc.broadcast(mongodb)
    E = sc.broadcast(exposure_time)
    points = sc.parallelize(mongodb.get_points_by_user_and_time(user['user_id'], time_window))
    possible_points = points.flatMap(partial(get_points_in_time, U=U, M=M))
    users = possible_points.map(partial(compare_points_location, D=D, E=E, M=M)).collect()
    stopContext()
    return users
