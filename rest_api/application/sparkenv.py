import os
import sys

# Path for spark source folder
os.environ['SPARK_HOME']="/usr/local/Cellar/apache-spark/1.5.2"


# Append pyspark  to Python Path
sys.path.append("/usr/local/Cellar/apache-spark/1.5.2/libexec/python/")

try:
    from pyspark import SparkContext
    from pyspark import SparkConf

    print ("Successfully imported Spark Modules")

except ImportError as e:
    print("Can not import Spark Modules", e)
    sys.exit(1)