from django.shortcuts import render

from rest_framework import generics
from rest_framework import viewsets

from users.serializers import UserSerializer, SensorValuesSerializer
from users.models import User, SensorValues

# Create your views here.
class UserViewSet(viewsets.ModelViewSet):
	queryset = User.objects.all() # Nos traemos todos los campos de la tabla User
	serializer_class = UserSerializer # Necesitamos un serializer que sea igual que el del User


class SensorValuesViewSet(viewsets.ModelViewSet):
	queryset = SensorValues.objects.all() # Nos traemos todos los campos de la tabla UpdateInfo
	serializer_class = SensorValuesSerializer # Necesitamos un serializer que sea igual que el del UpdateInfo