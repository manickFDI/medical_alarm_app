from flask import json

import algorithm
from shapely.geometry import LineString, Point

ring1 = LineString([Point(0, 0), Point(2, 2)])
ring2 = LineString([Point(0, 2), Point(2, 0)])
ring3 = LineString([Point(0, 1), Point(1, 2), Point(2, 1)])
rings = []
rings.append(ring1)
rings.append(ring2)
rings.append(ring3)

result = algorithm.calculateFocus(rings)
for item in result:
    print item
