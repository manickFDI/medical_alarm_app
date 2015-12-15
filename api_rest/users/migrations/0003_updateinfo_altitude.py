# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('users', '0002_updateinfo'),
    ]

    operations = [
        migrations.AddField(
            model_name='updateinfo',
            name='altitude',
            field=models.FloatField(default=0),
            preserve_default=False,
        ),
    ]
