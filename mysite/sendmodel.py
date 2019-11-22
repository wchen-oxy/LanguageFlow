# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey has `on_delete` set to the desired behavior.
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models

class English(models.Model):
    word = models.CharField(primary_key=True, max_length=50)
    ipa =  models.CharField(max_length=100, blank=True, null=True)
    definition = models.CharField(max_length=200, blank=False, null=False)
    example = models.CharField(max_length=400, blank = True, null= True)

    class Meta:
        managed = False
        db_table = 'English'
        verbose_name = 'English Entry'
        verbose_name_plural = 'English'

    def __str__(self):
        template = '{0.word}'
        return template.format(self)


