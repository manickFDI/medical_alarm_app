from django.db import models

# Create your models here.
class User(models.Model):
	user_id = models.CharField(max_length=15)
	name = models.CharField(max_length=15)
	surnames = models.CharField(max_length=25)
	dni = models.CharField(max_length=9)
	birthdate = models.CharField(max_length=25) #problemas de fechas
	email = models.EmailField()
	height = models.PositiveSmallIntegerField() # 0 to 32767
	weight = models.PositiveSmallIntegerField() # 0 to 32767
	password = models.CharField(max_length=100)

	class meta:
		ordering = ('user_id')

	def __unicode__(self):
		return self.name


class SensorValues(models.Model):
	latitude = models.CharField(max_length=25)
	longitude = models.CharField(max_length=25)
	altitude = models.CharField(max_length=25)
	time = models.CharField(max_length=25)
	magnetometer = models.CharField(max_length=25)
	mgt_accuracy = models.CharField(max_length=25)
	accelerometer = models.CharField(max_length=25)
	acc_accuracy = models.CharField(max_length=25)
	light = models.CharField(max_length=25)
	light_accuracy = models.CharField(max_length=25)
	battery = models.CharField(max_length=30)