# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0007_auto_20151214_2110'),
    ]

    operations = [
        migrations.AlterField(
            model_name='sensorvalues',
            name='acc_accuracy',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='accelerometer',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='altitude',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='latitude',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='light',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='light_accuracy',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='longitude',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='magnetometer',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='mgt_accuracy',
            field=models.CharField(max_length=25),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='time',
            field=models.CharField(max_length=25),
        ),
    ]
