import os
import sys

# Path for spark source folder
from itertools import combinations

os.environ['SPARK_HOME'] = "/usr/local/Cellar/apache-spark/1.5.2"
# Append pyspark  to Python Path
sys.path.append("/usr/local/Cellar/apache-spark/1.5.2/libexec/python/")

from flask import json
from pyspark import SparkContext, SparkConf
from shapely.geometry import LineString, mapping


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
