import psycopg2
from flask import Flask, jsonify, request, Response
from flask.views import MethodView
from psycopg2._psycopg import DataError

app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")


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
        /users?email=<emali>
        Gets user info for a given specified email address that is a URL query parameter
        """

        cur = conn.cursor()
        email = str(request.args.get("email"))
        print(email)
        cur.execute("SELECT * FROM \"User\" WHERE email='%s';", (email,))

        row = cur.fetchone()
        # Executes if no user with that email address was found
        if row == None:
            response = Response(status=400)
            return response

        data = {
            'Email': row[0],
            'Name': row[1]
        }

        cur.close()
        return jsonify(data), 200


class RestaurantAPI(MethodView):
    def format_restaurant(restaurant_tuple):
        address, pricerange, cuisine, name, phone, image_url = restaurant_tuple
        return {
            "address": address,
            "pricerange": pricerange,
            "cuisine": cuisine,
            "name": name,
            "phone": phone,
            "image_url": image_url
        }

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
                    params += [v]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", ""

        where_clause, where_params = build_where(request.args)

        with conn.cursor() as cur:
            try:
                cur.execute('SELECT * FROM "Restaurant" {where_clause}'.format(where_clause=where_clause), where_params)
                rv = cur.fetchall()
                rv = [RestaurantAPI.format_restaurant(r) for r in rv]
                return jsonify(rv)
            except DataError as e:
                conn.rollback()
                return "Invalid query data!", 500


user_view = UserAPI.as_view('user_api')
restaurant_view = RestaurantAPI.as_view('restaurant_api')
app.add_url_rule('/user', view_func=user_view, methods=['GET'])
app.add_url_rule('/restaurants', view_func=restaurant_view, methods=['GET'])

if __name__ == "__main__":
    app.run(host='0.0.0.0')
