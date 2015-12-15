# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0005_auto_20151214_1556'),
    ]

    operations = [
        migrations.AlterField(
            model_name='sensorvalues',
            name='time',
            field=models.CharField(max_length=30),
        ),
    ]
