from credentials import USER, PASSWORD, DATABASE, HOST
import psycopg2
import json
from pprint import pprint
from datetime import datetime

#Connect with psycopg2 to server
conn = psycopg2.connect(dbname=DATABASE, user=USER, password=PASSWORD, host=HOST)
cur = conn.cursor()

#Get the current time, replace this with whatever datetime format you need
#in the API, probbaly parse from a JSON the user inputs.
#How are we integrating the graphing?
#Like how will the user access it and use it
dt = datetime.now();

#execute the thing
cur.execute('''
SELECT *
FROM "Transaction"
WHERE date < (%s::date - (concat((MOD(extract(isodow FROM %s)::int, 7)),' days'))::interval+'7 days'::interval) AND
date > (%s::date - (concat((MOD(extract(isodow FROM %s)::int, 7)),' days'))::interval);
''',
(dt,))

#All Goes well, you should see something here.
#IDK what though, probably just a bunch of characters and stuff. It's unformated
print(cur.fetchall())
