# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='User',
            fields=[
                ('id', models.AutoField(primary_key=True, verbose_name='ID', serialize=False, auto_created=True)),
                ('user_id', models.CharField(max_length=15)),
                ('name', models.CharField(max_length=15)),
                ('surnames', models.CharField(max_length=25)),
                ('dni', models.CharField(max_length=9)),
                ('birthdate', models.DateField()),
                ('email', models.EmailField(max_length=254)),
                ('height', models.PositiveSmallIntegerField()),
                ('weight', models.PositiveSmallIntegerField()),
                ('password', models.CharField(max_length=100)),
            ],
        ),
    ]
