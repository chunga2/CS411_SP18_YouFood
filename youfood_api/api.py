import psycopg2
from psycopg2 import IntegrityError, ProgrammingError
from flask import Flask, jsonify, request, Response
from flask.views import MethodView
from psycopg2._psycopg import DataError

app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")

def home():
    return "<h1 style='color:blue'>Hello There!</h1>"

def flatten(iterable):
    it = iter(iterable)
    for e in it:
        if isinstance(e, (list, tuple)):
            for f in flatten(e):
                yield f
        else:
            yield e

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
            'name' : row[1]
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



def format_restaurants(restaurant_tuples):
    json_objects = []
    i = 0
    while i < len(restaurant_tuples):
        rest_tuple = restaurant_tuples[i]
        address, name, pricerange, phone, image_url, category = rest_tuple
        json_obj = {
            "address": address,
            "name": name,
            "pricerange": pricerange,
            "phone": phone,
            "image_url": image_url,
            "categories": [category]
        }

        while (i+1) < len(restaurant_tuples) and address == restaurant_tuples[i+1][0] and name == restaurant_tuples[i+1][1]:
            json_obj["categories"].append(restaurant_tuples[i+1][5])
            i = i + 1

        json_objects.append(json_obj)
        i = i + 1

    return json_objects


class RestaurantAPI(MethodView):

    def get(self):
        """
        Respond to API call /restaurants?params with a list of all restaurants, encoded as JSON.
        Accepts params as GET arguments, which are documented in build_where.
        :return: JSON response, formatted by format_restaurant.
        """

        def build_where(query_params):
            subqueries = {
                "name": "name = %s",
                "address": "address = %s",
                "pricelt": "pricerange < %s",
                "priceeq": "pricerange = %s",
            }
            selections = []
            params = []
            for k, v in query_params.items():
                if k in subqueries:
                    selections += [subqueries[k]]
                    params += [str(v)]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", ""

        where_clause, where_params = build_where(request.args)
        with conn.cursor() as cur:
            try:
                cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                    "Restaurant".phone, "Restaurant".image_url, "RestaurantCategories".category
                    FROM "Restaurant", "RestaurantCategories"
                    {where_clause} AND "Restaurant".name = "RestaurantCategories".restaurant_name 
                    AND "Restaurant".address = "RestaurantCategories".restaurant_address 
                    ORDER BY "Restaurant".name ASC, "Restaurant".address ASC""".format(where_clause=where_clause), where_params)
                rv = cur.fetchall()
                jsonobjects = format_restaurants(rv)
                cur.close()
                return jsonify(jsonobjects)
            except DataError as e:
                conn.rollback()
                cur.close()
                return "Invalid query data!", 500
            except ProgrammingError as e:
                conn.rollback()
                cur.close()
                return "Invalid query data!", 500



class RestaurantCategoriesAPI(MethodView):
    def get(self):
        """
        Respond to API call /restaurant_categories?category=<category>
        :return: JSON response, formatted by format_restaurant.
        """
        category = request.args.get("category")
        if category == None:
            return jsonify({'error': 'No category specified'}), 400

        with conn.cursor() as cur:
            try:
                cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                    "Restaurant".phone, "Restaurant".image_url, "RestaurantCategories".category
                    FROM "Restaurant", "RestaurantCategories"
                    WHERE "RestaurantCategories".category = %s 
                    AND "Restaurant".name = "RestaurantCategories".restaurant_name 
                    AND "Restaurant".address = "RestaurantCategories".restaurant_address 
                    ORDER BY "Restaurant".name ASC, "Restaurant".address ASC""", (category,))
                rv = cur.fetchall()
                jsonobjects = format_restaurants(rv)
                jsonobjects.append({'length': len(jsonobjects)})
                cur.close()
                return jsonify(jsonobjects)
            except DataError as e:
                conn.rollback()
                cur.close()
                return "Invalid query data!", 500
            except ProgrammingError as e:
                conn.rollback()
                cur.close()
                return "Invalid query data!", 500


app.add_url_rule('/', 'home', home, methods=['GET'])

user_view = UserAPI.as_view('user_api')
app.add_url_rule('/users', view_func=user_view, methods=['GET', 'POST', 'PUT', 'DELETE'])


restaurant_view = RestaurantAPI.as_view('restaurant_api')
app.add_url_rule('/restaurants', view_func=restaurant_view, methods=['GET'])

restaurant_categories_view = RestaurantCategoriesAPI.as_view('restaurant_categories_api')
app.add_url_rule('/restaurant_categories', view_func=restaurant_categories_view, methods=['GET'])


if __name__ == "__main__":
    app.run(host='0.0.0.0')
