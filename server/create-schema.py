from credentials import USER, PASSWORD, DATABASE, HOST
import psycopg2

conn = psycopg2.connect(dbname=DATABASE, user=USER, password=PASSWORD, host=HOST)
with open("schema.sql") as f:
    print("Opened SQL script...")
    with conn.cursor() as cur:
        print("Opened database connection...")
        cur.execute(f.read())
        conn.commit()