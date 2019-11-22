from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.core import serializers
from django.apps import apps
from django.urls import reverse
#API STUFF
#API Specific imports
from rest_framework.parsers import JSONParser
from .serializers import BhutiaSerializer, EnglishSerializer
from rest_framework import generics
import dictionaries 
from django.views.decorators.csrf import csrf_exempt
import hashlib

#API TEST STUFF
# def home(request):
#     # return HttpResponse("Hello, world. You're at the home index.")
#     home = True
#     return render(request, 'home.html', {'home': home})

def home(request):
    return render(request, "main_home.html")


def download(request, pack):
    data = serializers.serialize(
        "json", apps.get_model("mainapp", pack).objects.all())
    return JsonResponse(data, safe=False)

class AllBhutia(generics.ListAPIView):
    """
    FIXME implement dynamically called language pack
    """
    # def get(self, request, *args, **kwargs):
    # lang = self.kwargs['slug']
    # target = apps.get_model('dictionaries', lang)
    target = apps.get_model('dictionaries', "Bhutia")
    queryset = target.objects.all()
    serializer_class = BhutiaSerializer

@csrf_exempt
def test(request, lang, word=None):
    #  if request.method == 'GET':
     if request.method == 'GET' and 'Authorization' in request.headers:
         if request.headers['Authorization'] != encrypt_string("Az39dB0n!23"):
             return HttpResponse("Auth Key is Wrong.")
        ##FIXME NEED TO DESIGN INDIVIDUAL WORD SEARCH
         if (word == None):
             dictionary = apps.get_model("dictionaries", lang)
             print(dictionary)
             dic = dictionary.objects.all()
             serializer = None
             if lang=="Bhutia":
                serializer = BhutiaSerializer(dic, many=True)
             if lang == "English":
                serializer = EnglishSerializer(dic, many=True)
             return JsonResponse(serializer.data, safe=False)

        #  dictionary = apps.get_model("dictionaries", lang)
        #  print(dictionary)
        #  dic = dictionary.objects.all()
        #  serializer = BhutiaSerializer(dic, many=True)
        #  return JsonResponse(serializer.data, safe=False)
         return HttpResponse("Site works, but no lang matched")
     else:
        return HttpResponse("Site works, but you have no auth key.")

def encrypt_string(hash_string):
    sha_signature = \
        hashlib.sha256(hash_string.encode()).hexdigest()
    return sha_signature