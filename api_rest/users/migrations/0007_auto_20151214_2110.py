# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0006_auto_20151214_2000'),
    ]

    operations = [
        migrations.AlterField(
            model_name='sensorvalues',
            name='battery',
            field=models.CharField(max_length=30),
        ),
        migrations.AlterField(
            model_name='sensorvalues',
            name='time',
            field=models.CharField(max_length=15),
        ),
    ]
