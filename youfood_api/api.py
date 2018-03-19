import psycopg2
from psycopg2 import IntegrityError
from flask import Flask, jsonify, request, Response
from flask.views import MethodView


app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")

class UserAPI(MethodView):
    
    def get(self):
        """
        GET: /users?email=<emali>
        Gets user info for a given specified email address that is a URL query parameter
        """
        
        email = request.args.get("email")
        if email == None:
            return jsonify({'error': 'No email address specified'}), 400

        cur = conn.cursor()
        cur.execute("SELECT * FROM \"User\" WHERE email=%s;", (email,))

        row = cur.fetchone()
        # Executes if no user with that email address was found
        if row == None:
            conn.rollback()
            cur.close()
            return jsonify({'error': 'No user with that email exists'}), 400

        data = {
            'email' : row[0],
            'name' : row[1],
            'password' : row[2]
        }

        cur.close()
        return jsonify(data), 200

    def post(self):
        """
        POST: /users

        JSON request payload:
        {
            "email" : <email>
            "name" : <name>
            "password" : <password>
        }
        """
        json_data = request.get_json()
        email = json_data.get("email")
        name = json_data.get("name")
        password = json_data.get("password")
        
        if email == None or name == None or password == None:
            return Response(status=400)

        cur = conn.cursor()
        try:
            cur.execute("INSERT INTO \"User\" (email, name, hashedpass) VALUES (%s, %s, %s);", (email, name, password))
            conn.commit()
        except IntegrityError:
            conn.rollback()
            cur.close()
            return jsonify({'error': 'User with email already exists'}), 400

        cur.close()
        return Response(status=201)

    def put(self):
        """
        PUT: /users

        JSON request payload (email is required, at least 1 of name or password is also required)
        {
            "email" : <email>
            "name" : <name>
            "password" : <password>
        }
        """
        json_data = request.get_json()
        email = json_data.get("email")
        name = json_data.get("name")
        password = json_data.get("password")
        
        if email == None or (name == None and password == None):
            return jsonify({'error': 'Missing email or both name and password'}), 400

        cur = conn.cursor()
        if(name != None):
            cur.execute("UPDATE \"User\" SET name = %s WHERE email = %s;", (name, email))
            if cur.rowcount == 0:
                conn.rollback()
                cur.close()
                return jsonify({'error': 'No user with that email exists'}), 400

        if(password != None):
            cur.execute("UPDATE \"User\" SET hashedpass = %s WHERE email = %s;", (password, email))
            if cur.rowcount == 0:
                conn.rollback()
                cur.close()
                return jsonify({'error': 'No user with that email exists'}), 400

        conn.commit()
        cur.close()
        return Response(status=204)

    def delete(self):
        """
        DELETE: /users?email=<email>
        Deletes a user with a given email address. It is only 1 user because email is a primary key, so the email
        entered can correspond to at most 1 user
        """
        
        email = request.args.get("email")
        if email == None:
            return jsonify({'error': 'No email address specified'}), 400

        cur = conn.cursor()
        cur.execute("DELETE FROM \"User\" WHERE email=%s;", (email,))
        if cur.rowcount == 0:
            conn.rollback()
            cur.close()
            return jsonify({'error': 'No user with that email exists'}), 400

        conn.commit()
        cur.close()
        return Response(status=204)


user_view = UserAPI.as_view('user_api')
app.add_url_rule('/users', view_func=user_view, methods=['GET', 'POST', 'PUT', 'DELETE'])


if __name__ == "__main__":
    app.run(host='0.0.0.0')
