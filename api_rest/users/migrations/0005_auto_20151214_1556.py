# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0004_auto_20151214_1227'),
    ]

    operations = [
        migrations.CreateModel(
            name='SensorValues',
            fields=[
                ('id', models.AutoField(serialize=False, auto_created=True, verbose_name='ID', primary_key=True)),
                ('latitude', models.CharField(max_length=15)),
                ('longitude', models.CharField(max_length=15)),
                ('altitude', models.CharField(max_length=15)),
                ('time', models.CharField(max_length=15)),
                ('magnetometer', models.CharField(max_length=15)),
                ('mgt_accuracy', models.CharField(max_length=15)),
                ('accelerometer', models.CharField(max_length=15)),
                ('acc_accuracy', models.CharField(max_length=15)),
                ('light', models.CharField(max_length=15)),
                ('light_accuracy', models.CharField(max_length=15)),
                ('battery', models.CharField(max_length=20)),
            ],
        ),
        migrations.DeleteModel(
            name='UpdateInfo',
        ),
    ]
