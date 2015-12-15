from rest_framework import serializers
from users.models import User, SensorValues

class UserSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = User
        fields = ('user_id', 'name', 'surnames', 'dni', 'birthdate', 'email', 'height', 'weight', 'password')


class SensorValuesSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = SensorValues
        fields = ('latitude', 'longitude', 'altitude', 'time', 'magnetometer', 'mgt_accuracy', 'accelerometer', 'acc_accuracy', 'light', 'light_accuracy', 'battery')