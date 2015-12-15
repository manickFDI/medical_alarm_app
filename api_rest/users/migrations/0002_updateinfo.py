# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='UpdateInfo',
            fields=[
                ('id', models.AutoField(auto_created=True, verbose_name='ID', serialize=False, primary_key=True)),
                ('latitude', models.FloatField()),
                ('longitude', models.FloatField()),
                ('time', models.TimeField()),
                ('magnetometer', models.FloatField()),
                ('mgt_accuracy', models.PositiveSmallIntegerField()),
                ('accelerometer', models.FloatField()),
                ('acc_accuracy', models.PositiveSmallIntegerField()),
                ('light', models.FloatField()),
                ('light_accuracy', models.PositiveSmallIntegerField()),
                ('battery', models.CharField(max_length=20)),
            ],
        ),
    ]
