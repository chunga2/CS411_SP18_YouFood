from credentials import USER, PASSWORD, DATABASE, HOST
import psycopg2
import json
from pprint import pprint

#Choose the file (Must be in same file)
FILENAME = 'san_francisco.json'

#Load the json
data = json.load(open(FILENAME))
businesses = data['businesses']

#Connect with psycopg2 to server
conn = psycopg2.connect(dbname=DATABASE, user=USER, password=PASSWORD, host=HOST)
cur = conn.cursor()

#loop through and add all json to database
counter = 0
for business in businesses:
    # Format special things for display
    print("Adding #" + str(counter) + " " + business['name'])
    
    in_address = ", ".join(business['location']['display_address'])
    # Update restaurant
    cur.execute('''
    UPDATE \"Restaurant\"
    SET lat=%s
    SET lon=%s
    WHERE name=%s AND address=%s
    ''',
    (business['coordinates']['latitude'], business['coordinates']['longitude'], business['name'], in_address))
   
    conn.commit()
    counter += 1

print("Finished! Check Database. Did this many: ", counter)
conn.commit()

"""
SAMPLE ENTRY
{ 'categories': [ {'alias': 'vietnamese', 'title': 'Vietnamese'},
                    {'alias': 'chinese', 'title': 'Chinese'},
                    {'alias': 'noodles', 'title': 'Noodles'}],
    'coordinates': {'latitude': 37.76368, 'longitude': -122.46879},
    'display_phone': '(415) 566-4722',
    'distance': 2863.684176093556,
    'id': 'yummy-yummy-san-francisco',
    'image_url': 'https://s3-media2.fl.yelpcdn.com/bphoto/iNtuo93WsV9t8dU3Njiwbw/o.jpg',
    'is_closed': False,
    'location': { 'address1': '1015 Irving St',
                  'address2': '',
                  'address3': '',
                  'city': 'San Francisco',
                  'country': 'US',
                  'display_address': [ '1015 Irving St',
                                       'San Francisco, CA 94122'],
                  'state': 'CA',
                  'zip_code': '94122'},
    'name': 'Yummy Yummy',
    'phone': '+14155664722',
    'price': '$$',
    'rating': 4.0,
    'review_count': 1263,
    'transactions': ['pickup', 'delivery'],
    'url': 'https://www.yelp.com/biz/yummy-yummy-san-francisco?adjust_creative=09CWf82V7c_xrYOTHH6wKw&utm_campaign=yelp_api_v3&utm_medium=api_v3_business_search&utm_source=09CWf82V7c_xrYOTHH6wKw'}

"""
