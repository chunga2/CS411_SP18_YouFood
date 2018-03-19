import psycopg2
from flask import Flask
app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")

@app.route("/")
def hello():
        cur = conn.cursor()
        cur.execute("SELECT * FROM \"User\"") 
        rows = cur.fetchall()
        email = rows[0][0]
        cur.close()
        return email

if __name__ == "__main__":
    app.run(host='0.0.0.0')
