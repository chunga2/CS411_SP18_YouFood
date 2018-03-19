import psycopg2
from flask import Flask, jsonify, request, Response
from flask.views import MethodView


app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")

class UserAPI(MethodView):
    
    def get(self):
        """
        /users?email=<emali>
        Gets user info for a given specified email address that is a URL query parameter
        """

        cur = conn.cursor()
        email = str(request.args.get("email"))
        print email
        cur.execute("SELECT * FROM \"User\" WHERE email=%s;", (email,))

        row = cur.fetchone()
        # Executes if no user with that email address was found
        if row == None:
            response = Response(status=400)
            return response

        data = {
            'Email' : row[0],
            'Name' : row[1]
        }

        cur.close()
        return jsonify(data), 200

user_view = UserAPI.as_view('user_api')
app.add_url_rule('/user', view_func=user_view, methods=['GET'])


if __name__ == "__main__":
    app.run(host='0.0.0.0')
