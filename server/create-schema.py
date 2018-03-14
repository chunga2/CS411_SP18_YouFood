from credentials import USER, PASSWORD, DATABASE, HOST
import psycopg2

conn = psycopg2.connect(dbname=DATABASE, user=USER, password=PASSWORD, host=HOST)
with open("schema.sql") as f:
    with conn.cursor() as cur:
        cur.execute(f.read())