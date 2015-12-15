# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0003_updateinfo_altitude'),
    ]

    operations = [
        migrations.AlterField(
            model_name='updateinfo',
            name='acc_accuracy',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='accelerometer',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='altitude',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='latitude',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='light',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='light_accuracy',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='longitude',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='magnetometer',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='mgt_accuracy',
            field=models.CharField(max_length=15),
        ),
        migrations.AlterField(
            model_name='updateinfo',
            name='time',
            field=models.CharField(max_length=15),
        ),
    ]
