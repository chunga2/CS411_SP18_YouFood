from datetime import datetime
from typing import Dict, Tuple, Union, List

import psycopg2
from flask import Flask, jsonify, request, Response
from flask.views import MethodView
from psycopg2 import IntegrityError, ProgrammingError
from psycopg2._psycopg import DataError
from datetime import datetime

app = Flask(__name__)

conn = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")


@app.route("/")
def home():
    return "<h1 style='color:blue'>Hello There!</h1>"


class InvalidUsageException(Exception):
    def __init__(self, error_message, status_code=None):
        """
        Create an InvalidUsageException.
        If this is raised within an app context, Flask will return an error dict to the user with code 400.
        :param error_message:
        :param status_code:
        """
        Exception.__init__(self)
        self.status_code = 400
        self.message = error_message
        if status_code is not None:
            self.status_code = status_code

    def to_dict(self):
        return {
            'error': self.message
        }


@app.errorhandler(InvalidUsageException)
def handle_invalid_usage(error):
    response = jsonify(error.to_dict())
    response.status_code = error.status_code
    return response


# TODO: Migrate all functions to use this to validate their arguments, because this will ensure consistency
def make_arg_list(args: List[str], data: Dict[str, str]) -> Union[List[str], Tuple[str, int]]:
    """
    Accept a list of arguments to match, as well as a dictionary to unpack into them.
    :param args: The arguments to match.
    :return: A list of parameters matching the arguments (in order) or a string holding the first missing argument.
    """
    vals = []
    for k in args:
        if k in data:
            vals += autocast(data[k])
        else:
            raise InvalidUsageException(f"Missing argument {k}")
    return vals


def autocast(string: str) -> Union[str, datetime]:
    try:
        return datetime.strptime(string, "%d-%m-%Y %H:%M:%S")
    except:
        return string


def flatten(iterable):
    it = iter(iterable)
    for e in it:
        if isinstance(e, (list, tuple)):
            for f in flatten(e):
                yield f
        else:
            yield e


def verify_login():
    """
    POST: /verify_login
    Request Body:
    {
        email: <email>,
        password: <password
    }

    Returns 400 if a request param was missing, 401 if the login was invalid, or 204 if it was valid
    """
    json_data = request.get_json()
    email = json_data.get("email")
    password = json_data.get("password")

    if email == None or password == None:
        return "Missing necessary parameter in request body", 400

    with conn as c:
        with c.cursor() as cur:
            cur.execute("""
                SELECT email, name, is_owner 
                FROM "User" 
                WHERE email = %s AND hashedpass = %s""", (email, password))

            row = cur.fetchone()
            if row == None:
                return "Invalid Login", 401
            else:
                user_data = {
                    "email": row[0],
                    "name": row[1],
                    "is_owner": row[2]
                }
                return jsonify(user_data), 200


def parse_date(date: str) -> datetime:
    return datetime.strptime(date, "%d-%m-%Y %H:%M:%S")


def format_restaurants(restaurant_tuples):
    json_objects = []
    i = 0
    while i < len(restaurant_tuples):
        rest_tuple = restaurant_tuples[i]
        address, name, pricerange, phone, image_url, category, lat, lon = rest_tuple
        json_obj = {
            "address": address,
            "name": name,
            "pricerange": pricerange,
            "phone": phone,
            "image_url": image_url,
            "categories": [category],
            "latitude": lat,
            "longitude": lon
        }

        while (i + 1) < len(restaurant_tuples) and address == restaurant_tuples[i + 1][0] and name == \
                restaurant_tuples[i + 1][1]:
            json_obj["categories"].append(restaurant_tuples[i + 1][5])
            i = i + 1

        json_objects.append(json_obj)
        i = i + 1

    return json_objects


class UserAPI(MethodView):

    def get(self):
        """
        GET: /users?email=<emali>
        Gets user info for a given specified email address that is a URL query parameter
        """

        args = ['email']
        params = make_arg_list(args, request.args)
        with conn as c:
            with c.cursor() as cur:
                cur.execute("SELECT email, name, is_owner FROM \"User\" WHERE email=%s;", params)
                row = cur.fetchone()
                # Executes if no user with that email address was found
                if row == None:
                    return jsonify({'error': 'No user with that email exists'}), 400

                data = {
                    'email': row[0],
                    'name': row[1],
                    'is_owner': row[2]
                }

                return jsonify(data), 200

    def post(self):
        """
        POST: /users

        JSON request payload:
        {
            "email" : <email>
            "name" : <name>
            "password" : <password>,
        }
        """
        json_data = request.get_json()
        email = json_data.get("email")
        name = json_data.get("name")
        password = json_data.get("password")

        if email == None or (name == None and password == None):
            return jsonify({'error': 'Missing email or both name and password'}), 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("INSERT INTO \"User\" (email, name, hashedpass) VALUES (%s, %s, %s);", [email, name, password])
                return Response(status=201)

    def put(self): #TODO: Rewrite so that this can use make_arg_list
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

        with conn as c:
            with c.cursor() as cur:
                if (name != None):
                    cur.execute("UPDATE \"User\" SET name = %s WHERE email = %s;", (name, email))
                    if cur.rowcount == 0:
                        return jsonify({'error': 'No user with that email exists'}), 400

                if (password != None):
                    cur.execute("UPDATE \"User\" SET hashedpass = %s WHERE email = %s;", (password, email))
                    if cur.rowcount == 0:
                        return jsonify({'error': 'No user with that email exists'}), 400

                return Response(status=204)

    def delete(self):
        """
        DELETE: /users?email=<email>
        Deletes a user with a given email address. It is only 1 user because email is a primary key, so the email
        entered can correspond to at most 1 user
        """

        json_data = request.get_json()
        email = json_data.get("email")
        if not email:
            raise InvalidUsageException("Missing email!")

        with conn as c:
            with c.cursor() as cur:
                cur.execute("DELETE FROM \"User\" WHERE email=%s;", [email])
                if cur.rowcount == 0:
                    return jsonify({'error': 'No user with that email exists'}), 400

                return Response(status=204)


class PromotionAPI(MethodView):

    def get(self):
        """
        GET: /promotions?restaurant_name=<name>&restaurant_address=<address>
        Gets user info for a given specified email address that is a URL query parameter
        """
        restaurant_name = request.args.get("restaurant_name")
        restaurant_address = request.args.get("restaurant_address")

        if restaurant_name is None:
            return jsonify({'error': 'No restaurant_name specified'}), 400
        if restaurant_address is None:
            return jsonify({'error': 'No restaurant_address specified'}), 400

        with conn as c:
            with c.cursor() as cur:
                cur = conn.cursor()
                cur.execute("""SELECT restaurant_name, restaurant_address, date, description
                            FROM "Promotion"
                            WHERE restaurant_name=%s AND
                            restaurant_address=%s;""",
                            (restaurant_name, restaurant_address))

                rows = cur.fetchall()

                json_objects = []
                for row in rows:
                    name, address, date, description = row
                    json_obj = {
                        'restaurant_name': name,
                        'restaurant_address': address,
                        'date': date.strftime("%d-%m-%Y %H:%M:%S"),
                        'description': description
                    }
                    json_objects.append(json_obj)

                return jsonify(json_objects), 200

    def post(self):
        """
        POST: /promotions

        JSON request payload:
        {
            "restaurant_name": <restaurant_name>,
            "restaurant_address": <restaurant_address>,
            "date": <date>,
            "description": <description>
        }
        """
        json_data = request.get_json()
        restaurant_name = json_data.get("restaurant_name")
        restaurant_address = json_data.get("restaurant_address")
        date = json_data.get("date")
        description = json_data.get("description")

        if restaurant_name is None or restaurant_address is None or date is None or description is None:
            return Response(status=400)

        with conn as c:
            with c.cursor() as cur:
                cur.execute("INSERT INTO \"Promotion\" "
                            "(restaurant_name, restaurant_address, date, description) "
                            "VALUES (%s, %s, %s, %s);",
                            (restaurant_name, restaurant_address,
                             datetime.strptime(date, "%d-%m-%Y %H:%M:%S"), description))
                return Response(status=201)

    def delete(self):
        """
        DELETE: /promotions?restaurant_name=<name>&restaurant_address=<address>&date=<date>&description=<description>
        Deletes a user with a given email address. It is only 1 Promotion because 4 attributes are a primary key.
        """

        restaurant_name = request.args.get("restaurant_name")
        restaurant_address = request.args.get("restaurant_address")
        date = request.args.get("date")
        description = request.args.get("description")

        if restaurant_name is None:
            return jsonify({'error': 'No restaurant_name specified'}), 400
        if restaurant_address is None:
            return jsonify({'error': 'No restaurant_address specified'}), 400
        if date is None:
            return jsonify({'error': 'No date specified'}), 400
        if description is None:
            return jsonify({'error': 'No description specified'}), 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""DELETE
                            FROM "Promotion"
                            WHERE restaurant_name=%s AND
                            restaurant_address=%s AND
                            date=%s AND
                            description=%s;""",
                            (restaurant_name, restaurant_address,
                             datetime.strptime(date, "%d-%m-%Y %H:%M:%S"), description))

                if cur.rowcount == 0:
                    return jsonify({'error': 'No promotion with that key exists'}), 400

                return Response(status=204)


class RestaurantAPI(MethodView):

    def get(self):
        """
        GET /restaurants?params with a list of all restaurants, encoded as JSON.
        Accepts params as GET arguments, which are documented in build_where.
        :return: JSON response, formatted by format_restaurant.
        """
        def build_where(query_params):
            subqueries = {
                "name": "name ILIKE %s",
                "city": "address LIKE %s",
                "address": "address = %s",
                "pricegte": "pricerange >= %s",
                "pricelte": "pricerange <= %s",
                "priceeq": "pricerange = %s"
            }
            selections = []
            params = []
            for k, v in query_params.items():
                if k in subqueries:
                    if k == "city":
                        value = "%%%s,%%" % str(v)
                        selections += [subqueries[k]]
                        params += [value]
                    elif k == 'name':
                        value = f'%{v}%'
                        selections += [subqueries[k]]
                        params += [value]
                    else:
                        selections += [subqueries[k]]
                        params += [v]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", []

        where_clause, where_params = build_where(request.args)
        with conn as c:
            with c.cursor() as cur:
                if len(where_params) > 0:
                    cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                        "Restaurant".phone, "Restaurant".image_url, "RestaurantCategories".category, 
                        "Restaurant".lat, "Restaurant".lon  
                        FROM "Restaurant", "RestaurantCategories"
                        {where_clause} AND "Restaurant".name = "RestaurantCategories".restaurant_name 
                        AND "Restaurant".address = "RestaurantCategories".restaurant_address 
                        ORDER BY "Restaurant".name ASC, "Restaurant".address ASC""".format(where_clause=where_clause), where_params)
                else:
                    cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                        "Restaurant".phone, "Restaurant".image_url, "RestaurantCategories".category, 
                        "Restaurant".lat, "Restaurant".lon  
                        FROM "Restaurant", "RestaurantCategories" 
                        WHERE "Restaurant".name = "RestaurantCategories".restaurant_name 
                        AND "Restaurant".address = "RestaurantCategories".restaurant_address 
                        ORDER BY "Restaurant".name ASC, "Restaurant".address ASC""")

                rv = cur.fetchall()
                jsonobjects = format_restaurants(rv)
                return jsonify(jsonobjects), 200

    def put(self):
        """
        PUT /restaurants
        {
            'restaurant_address': <address>,
            'restaurant_name': <name>,
            'owner_email': <email>
        }
        """
        args = ['owner_email', 'restaurant_name', 'restaurant_address']
        params = make_arg_list(args, request.get_json())

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    SELECT is_owner
                    FROM "User"
                    WHERE email=%s;""", (params[0],))

                row = cur.fetchone()
                if row is None or row[0] == False:
                    return "Not an owner", 400

                cur.execute("""
                    UPDATE "Restaurant"
                    SET owner_email = %s
                    WHERE name = %s 
                    AND address = %s;""", params)
                return Response(status=204)


class RestaurantCategoriesAPI(MethodView):
    def get(self):
        """
        GET /restaurant_categories?category=<category>
        :return: JSON response, formatted by format_restaurant.
        """
        def build_where(query_params):
            subqueries = {
                "name": "name = %s",
                "city": "address LIKE %s",
                "address": "address = %s",
                "pricegte": "pricerange >= %s",
                "pricelte": "pricerange <= %s",
                "priceeq": "pricerange = %s"
            }
            selections = []
            params = []
            for k, v in query_params.items():
                if k in subqueries:
                    if k == "city":
                        value = "%%%s,%%" % str(v)
                        selections += [subqueries[k]]
                        params += [value]
                    else:
                        selections += [subqueries[k]]
                        params += [v]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", []
        # This endpoint should never be called without category passed in as an arg
        category = request.args.get("category")
        if category == None:
            return jsonify({'error': 'No category specified'}), 400

        where_params = [category]
        where_clause, params = build_where(request.args)
        where_params += params
        print(where_clause)
        print(where_params)
        with conn as c:
            with c.cursor() as cur:
                if len(params) == 0:
                    cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                        "Restaurant".phone, "Restaurant".image_url, "SpecificRestaurants".category, 
                        "Restaurant".lat, "Restaurant".lon 
                        FROM "Restaurant",
                            (
                                (SELECT * FROM "RestaurantCategories")
                                EXCEPT
                                (SELECT * 
                                 FROM "RestaurantCategories" AS "OuterTable"
                                 WHERE NOT EXISTS
                                    (
                                    SELECT * FROM "RestaurantCategories"
                                    WHERE "RestaurantCategories".category = %s AND 
                                    "RestaurantCategories".restaurant_name = "OuterTable".restaurant_name AND 
                                    "RestaurantCategories".restaurant_address = "OuterTable".restaurant_address
                                    )
                                ) 
                            ) AS "SpecificRestaurants"
                        WHERE "Restaurant".name = "SpecificRestaurants".restaurant_name 
                        AND "Restaurant".address = "SpecificRestaurants".restaurant_address 
                        ORDER BY "Restaurant".name ASC, "Restaurant".address ASC;""", where_params)
                else:
                    cur.execute("""SELECT "Restaurant".address, "Restaurant".name, "Restaurant".pricerange, 
                        "Restaurant".phone, "Restaurant".image_url, "SpecificRestaurants".category, 
                        "Restaurant".lat, "Restaurant".lon 
                        FROM "Restaurant",
                            (
                                (SELECT * FROM "RestaurantCategories")
                                EXCEPT
                                (SELECT * 
                                 FROM "RestaurantCategories" AS "OuterTable"
                                 WHERE NOT EXISTS
                                    (
                                    SELECT * FROM "RestaurantCategories"
                                    WHERE "RestaurantCategories".category = %s AND 
                                    "RestaurantCategories".restaurant_name = "OuterTable".restaurant_name AND 
                                    "RestaurantCategories".restaurant_address = "OuterTable".restaurant_address
                                    )
                                ) 
                            ) AS "SpecificRestaurants"
                        {where_clause} AND "Restaurant".name = "SpecificRestaurants".restaurant_name 
                        AND "Restaurant".address = "SpecificRestaurants".restaurant_address 
                        ORDER BY "Restaurant".name ASC, "Restaurant".address ASC;""".format(where_clause=where_clause), where_params)

                rv = cur.fetchall()
                jsonobjects = format_restaurants(rv)
                return jsonify(jsonobjects), 200


def format_transaction(transaction_tuple: Tuple) -> Dict[str, str]:
    date, user, amount, restaurant_name, restaurant_address = transaction_tuple
    return {
        "date": date.strftime("%d-%m-%Y %H:%M:%S"),
        "useremail": user,
        "amount": amount,
        "restaurant_name": restaurant_name,
        "restaurant_address": restaurant_address,
    }


class TransactionAPI(MethodView):
    def get(self):
        """
        GET /transactions?params with a list of all transactions matching the params.
        :return: JSON response, formatted by format_restaurant.
        """

        def build_where(query_params):
            subqueries = {
                "start": "date > %s",
                "end": "date < %s",
                "user": "useremail = %s",
                "amount": "amount = %s",
            }
            selections = []
            params = []
            for k, v in query_params.items():
                if k == "start" or k == "end":
                    try:
                        timestamp = parse_date(v)
                        params += [timestamp]
                    except ValueError as e:
                        return "Invalid time format", 500
                else:
                    params += [str(v)]
                selections += [subqueries[k]]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", []

        where_clause, where_params = build_where(request.args)
        with conn as c:
            with c.cursor() as cur:
                try:
                    cur.execute(f"""SELECT date, useremail, amount, restaurant_name, restaurant_address FROM "Transaction"
                            {where_clause} ORDER BY date ASC""", where_params)
                    rv = cur.fetchall()
                    jsonobjects = [format_transaction(x) for x in rv]
                    return jsonify(jsonobjects), 200
                except DataError as e:
                    print(e)
                    return "Invalid query data!", 500

    def post(self):
        """
        POST: /transactions

        JSON request payload
        {
            "date": <date>,
            "useremail": <user>,
            "amount": <amount>,
            "restaurant_name": <restaurant_name>,
            "restaurant_address": <restaurant_address>,
        }
        """
        json_data = request.get_json()
        insert_cols = ["date", "useremail", "amount", "restaurant_name", "restaurant_address"]
        insert_params = []

        for v in insert_cols:
            if v not in json_data:
                return f"Missing field {v}", 500
            if v == "date":
                try:
                    timestamp = datetime.strptime(json_data[v], "%d-%m-%Y %H:%M:%S")
                    insert_params += [timestamp]
                except ValueError as e:
                    return "Invalid time format", 500
            else:
                insert_params += [json_data[v]]

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""INSERT INTO "Transaction"(date, useremail, amount, restaurant_name, restaurant_address)
                                    VALUES (%s, %s, %s, %s, %s)""", insert_params)
                return Response(status=201)

    def put(self):
        """
        PUT: /transactions

        JSON request payload
        {
            "date": <date>,
            "useremail": <user>,
            "amount": <amount>
        }
        """

        json_data = request.get_json()
        date = json_data.get("date")
        useremail = json_data.get("useremail")
        amount = json_data.get("amount")

        if date is None or useremail is None or amount is None:
            return "Missing request", 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    UPDATE "Transaction"
                    SET amount=%s
                    WHERE useremail=%s
                    AND date = %s""",
                            (amount, useremail, datetime.strptime(date, "%d-%m-%Y %H:%M:%S")))
                return Response(status=204)

    def delete(self):
        """
        DELETE: /transactions?useremail=<email>&date=<date>
        """
        useremail = request.args.get("useremail")
        date = request.args.get("date")
        if useremail is None or date is None:
            return Response(status=400)

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    DELETE FROM "Transaction"
                    WHERE useremail=%s 
                    AND date=%s""", (useremail, datetime.strptime(date, "%d-%m-%Y %H:%M:%S")))
                return Response(status=204)


def format_budget(transaction_tuple: Tuple) -> Dict[str, str]:
    date, user, total = transaction_tuple
    return {
        "date": date.strftime("%d-%m-%Y %H:%M:%S"),
        "useremail": user,
        "total": total,
    }


class BudgetAPI(MethodView):
    def get(self):
        """
        Respond to API call /budget?params with a list of all budgets matching the params.
        :return: JSON response, formatted by format_restaurant.
        """

        def build_where(query_params):
            subqueries = {
                "start": "date >= %s",
                "end": "date <= %s",
                "user": "useremail = %s",
                "total": "total = %s",
            }
            selections = []
            params = []
            for k, v in query_params.items():
                if k in subqueries:
                    if k == "start" or k == "end":
                        try:
                            timestamp = parse_date(v)
                            params += [timestamp]
                        except ValueError as e:
                            return "Invalid time format", 500
                    else:
                        params += [str(v)]
                    selections += [subqueries[k]]
            if selections:
                where_clause = "WHERE " + " AND ".join(selections)
                return where_clause, params
            return "", []

        where_clause, where_params = build_where(request.args)
        with conn as c:
            with c.cursor() as cur:
                try:
                    cur.execute(f"""SELECT date, useremail, total FROM "Budget"
                            {where_clause} ORDER BY date ASC""", where_params)
                    rv = cur.fetchall()
                    jsonobjects = [format_budget(x) for x in rv]
                    return jsonify(jsonobjects)
                except DataError as e:
                    print(e)
                    return "Invalid query data!", 500

    def post(self):
        """
        POST: /budget

        JSON request payload
        {
            "date": <date>,
            "useremail": <user>,
            "total": <amount>,
        }
        """
        json_data = request.get_json()
        insert_cols = ["date", "useremail", "total"]
        insert_params = []

        for k in insert_cols:
            if k not in json_data:
                return f"Missing field {k}", 500
            if k == "date":
                try:
                    timestamp = parse_date(json_data[k])
                    insert_params += [timestamp]
                except ValueError as e:
                    return "Invalid time format", 500
            else:
                insert_params += [json_data[k]]

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""INSERT INTO "Budget"(date, useremail, total)
                                    VALUES (%s, %s, %s)""", insert_params)
                return Response(status=201)

    def put(self):
        """
        PUT: /budget

        JSON request payload
        {
            "date": <date>,
            "useremail": <user>,
            "amount": <amount>
        }
        """
        json_data = request.get_json()
        date = json_data.get("date")
        useremail = json_data.get("useremail")
        total = json_data.get("amount")

        if date is None or useremail is None or total is None:
            return "Missing request", 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    UPDATE "Budget"
                    SET total=%s
                    WHERE useremail=%s 
                    AND date = %s;""",
                            (total, useremail, datetime.strptime(date, "%d-%m-%Y %H:%M:%S")))
                return Response(status=204)

    def delete(self):
        """
        DELETE: /budget?useremail=<email>&date=<date>
        """
        useremail = request.args.get("useremail")
        date = request.args.get("date")
        if useremail is None or date is None:
            return Response(status=400)

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    DELETE FROM "Budget"
                    WHERE useremail=%s 
                    AND date=%s""", (useremail, datetime.strptime(date, "%d-%m-%Y %H:%M:%S")))
                return Response(status=204)


class RecommendationAPI(MethodView):
    def convert_to_json(self, rows):
        json_objects = []
        for row in rows:
            date, useremail, restaurant_name, restaurant_address = row
            json_obj = {
                'date': date.strftime("%d-%m-%Y %H:%M:%S"),
                'useremail': useremail,
                'restaurant_name': restaurant_name,
                'restaurant_address': restaurant_address
            }
            json_objects.append(json_obj)

        return json_objects

    def get(self):
        """
        GET /recommendations?useremail=<useremail>
        Response: JSON array of all recommendations for that user
        """
        useremail = request.args.get("useremail")
        if useremail == None:
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    SELECT date, useremail, restaurant_name, restaurant_address
                    FROM "Recommendation"
                    WHERE useremail = %s
                    ORDER BY date desc;
                    """, (useremail,))
                rv = cur.fetchall()
                json_objects = self.convert_to_json(rv)
                return jsonify(json_objects), 200

    def post(self):
        """
        POST /recommendations
        Request:
        {
            "date": <"%d-%m-%Y %H:%M:%S">
            "useremail": <useremail>,
            "restaurant_name": <name>,
            "restaurant_address": <addres>
        }
        """
        json_data = request.get_json()
        date = json_data.get("date")
        useremail = json_data.get("useremail")
        restaurant_name = json_data.get("restaurant_name")
        restaurant_address = json_data.get("restaurant_address")

        if date == None or useremail == None or restaurant_name == None or restaurant_address == None:
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                formatted_date_obj = datetime.strptime(date, "%d-%m-%Y %H:%M:%S")
                cur.execute("""
                    INSERT INTO
                    "Recommendation" (date, useremail, restaurant_name, restaurant_address)
                    VALUES (%s, %s, %s, %s);""", (formatted_date_obj, useremail, restaurant_name, restaurant_address))
                return Response(status=201)

    def delete(self):
        """
        DELETE /recommendations?usermail=<usermail>&restaurant_name=<name>&restaurant_address=Maddress>&date=<date>
        Response is 400 if missing arg, 500 if error, 204 NO CONTENT
        """
        useremail = request.args.get("useremail")
        restaurant_name = request.args.get("restaurant_name")
        restaurant_address = request.args.get("restaurant_address")

        if useremail == None or restaurant_name == None or restaurant_address == None:
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                    DELETE FROM "Recommendation"
                    WHERE useremail=%s 
                    AND restaurant_name=%s 
                    AND restaurant_address=%s;
                    """, (useremail, restaurant_name, restaurant_address))
                if cur.rowcount == 0:
                    return jsonify({'error': 'No record to delete'}), 400
                return Response(status=204)


class ReviewAPI(MethodView):
    def convert_to_json(self, rows):
        json_objects = []
        for row in rows:
            name, useremail, restaurant_name, restaurant_address, description, rating, date = row
            json_obj = {
                'name': name,
                'useremail': useremail,
                'restaurant_name': restaurant_name,
                'restaurant_address': restaurant_address,
                'description': description,
                'rating': int(rating),
                'date': date.strftime("%d-%m-%Y %H:%M:%S")
            }
            json_objects.append(json_obj)

        return json_objects

    def get(self):
        """
        GET /reviews?search_user=<bool>...
        if search_user='True': &useremail=<email>
        if search_user='False': &restaurant_name=<name> &restaurant_address=<address>
        """
        search_user = request.args.get("search_user")
        if search_user == None:
            return "Missing Request Arg", 400

        if search_user == 'True':
            useremail = request.args.get("useremail")
            if useremail == None:
                return "Missing Request Arg", 400

            with conn as c:
                with c.cursor() as cur:
                    cur.execute("""
                        SELECT "User".name, useremail, restaurant_name, restaurant_address, description, rating, date
                        FROM "Review", "User"
                        WHERE useremail=%s
                        AND "Review".useremail = "User".email;""", (useremail,))
                    rows = cur.fetchall()
                    json_objects = self.convert_to_json(rows)
                    return jsonify(json_objects), 200
        else:
            restaurant_name = request.args.get("restaurant_name")
            restaurant_address = request.args.get("restaurant_address")
            if restaurant_name == None or restaurant_address == None:
                return "Missing Request Arg", 400

            with conn as c:
                with c.cursor() as cur:
                    cur.execute("""
                        SELECT "User".name, useremail, restaurant_name, restaurant_address, description, rating, date
                        FROM "Review", "User"
                        WHERE restaurant_name=%s
                        AND restaurant_address=%s
                        AND "Review".useremail = "User".email;""", (restaurant_name, restaurant_address))
                    rows = cur.fetchall()
                    json_objects = self.convert_to_json(rows)
                    return jsonify(json_objects), 200

    def post(self):
        """
        POST /recommendations
        Request:
        {
            "date": <"%d-%m-%Y %H:%M:%S">
            "useremail": <useremail>,
            "restaurant_name": <name>,
            "restaurant_address": <address>,
            "description": <description>,
            "rating": <rating>
        }
        """
        json_data = request.get_json()
        date = json_data.get("date")
        useremail = json_data.get("useremail")
        restaurant_name = json_data.get("restaurant_name")
        restaurant_address = json_data.get("restaurant_address")
        description = json_data.get("description")
        rating = json_data.get("rating")

        if date == None or useremail == None or restaurant_name == None or restaurant_address == None or description == None or rating == None:
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                formatted_date_obj = datetime.strptime(date, "%d-%m-%Y %H:%M:%S")
                cur.execute("""
                    INSERT INTO
                    "Review" (date, useremail, restaurant_name, restaurant_address, description, rating)
                    VALUES (%s, %s, %s, %s, %s, %s);""",
                            (formatted_date_obj, useremail, restaurant_name, restaurant_address, description, rating))
                return Response(status=201)

    def put(self):
        """
        PUT /recommendations
        Request (first 4 params used to identify what to update, last 2 are attributes that can be updated)
        {
            "date": <"%d-%m-%Y %H:%M:%S">
            "useremail": <useremail>,
            "restaurant_name": <name>,
            "restaurant_address": <address>,
            "description": <description>,
            "rating": <rating>
        }
        """
        json_data = request.get_json()
        date = json_data.get("date")
        useremail = json_data.get("useremail")
        restaurant_name = json_data.get("restaurant_name")
        restaurant_address = json_data.get("restaurant_address")
        description = json_data.get("description")
        rating = json_data.get("rating")

        if date == None or useremail == None or restaurant_name == None or restaurant_address == None or (
                description == None and rating == None):
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                formatted_date_obj = datetime.strptime(date, "%d-%m-%Y %H:%M:%S")

                if description != None:
                    cur.execute("""
                        UPDATE "Review"
                        SET description = %s
                        WHERE date = %s AND useremail = %s AND restaurant_name = %s
                        AND restaurant_address = %s;""",
                                (description, formatted_date_obj, useremail, restaurant_name, restaurant_address))
                    if cur.rowcount == 0:
                        return "Record Not Found", 400

                if rating != None:
                    cur.execute("""
                        UPDATE "Review"
                        SET rating = %s
                        WHERE date = %s AND useremail = %s AND restaurant_name = %s
                        AND restaurant_address = %s;""",
                                (rating, formatted_date_obj, useremail, restaurant_name, restaurant_address))
                    if cur.rowcount == 0:
                        return "Record Not Found", 400

                return Response(status=204)

    def delete(self):
        """
        DELETE /recommendations?usermail=<usermail>&restaurant_name=<name>&restaurant_address=Maddress>&date=<date>
        Response is 400 if missing arg, 500 if error, 204 NO CONTENT
        """
        date = request.args.get("date")
        useremail = request.args.get("useremail")
        restaurant_name = request.args.get("restaurant_name")
        restaurant_address = request.args.get("restaurant_address")

        if useremail == None or restaurant_name == None or restaurant_address == None or date == None:
            return "Missing request arg", 400

        with conn as c:
            with c.cursor() as cur:
                formatted_date_obj = datetime.strptime(date, "%d-%m-%Y %H:%M:%S")
                cur.execute("""
                    DELETE FROM "Review"
                    WHERE date = %s
                    AND useremail=%s
                    AND restaurant_name=%s
                    AND restaurant_address=%s
                    """, (formatted_date_obj, useremail, restaurant_name, restaurant_address))
                if cur.rowcount == 0:
                    return jsonify({'error': 'No record to delete'}), 400
                return Response(status=204)

def get_restaurant_statistics():
    """
    GET /get_restaurant_statistics?name=<name>&address=<address>

    Response: 
    {
        'review_average': <float>,
        'review_count': <int>,
        'transaction_count': <int>
    }
    """
    name = request.args.get("name")
    address = request.args.get("address")
    if name is None or address is None:
        return jsonify({"error": "Missing Query Arg"}), 400

    with conn as c:
            with c.cursor() as cur:
                cur.execute(""" 
                    SELECT "ReviewGroup".averagerating, "ReviewGroup".numratings, "TransactionGroup".numtransactions
                    FROM 
                    (
                        SELECT AVG(rating) AS averagerating, COUNT(*) AS numratings 
                        FROM "Review" 
                        WHERE restaurant_name = %s AND restaurant_address = %s 
                    ) AS "ReviewGroup",
                    (
                        SELECT COUNT(*) AS numtransactions
                        FROM "Transaction"
                        WHERE restaurant_name = %s AND restaurant_address = %s
                    ) AS "TransactionGroup";""", (name, address, name, address))

                row = cur.fetchone()
                statistics = {
                    'review_average': float(row[0]),
                    'review_count': row[1],
                    'transaction_count': row[2]
                }

                return jsonify(statistics), 200

def get_weekly_transactions_for_user():
    useremail = request.args.get("useremail")
    dateSearch = request.args.get("date")
    if useremail is None or dateSearch is None:
        return jsonify({"error": "Missing Query Arg"}), 400

    dateObj = datetime.strptime(dateSearch, "%d-%m-%Y %H:%M:%S")
    with conn as c:
            with c.cursor() as cur:
                cur.execute("""
                SELECT date, useremail, amount, restaurant_name, restaurant_address
                FROM "Transaction"
                WHERE useremail=%s AND date < (%s::date - (concat((MOD(extract(isodow FROM %s)::int, 7)),' days'))::interval+'7 days'::interval) AND
                date > (%s::date - (concat((MOD(extract(isodow FROM %s)::int, 7)),' days'))::interval);
                """,
                (useremail, dateObj,dateObj,dateObj,dateObj))

                rows = cur.fetchall()
                json_objects = []
                for row in rows:
                    date, email, amount, name, address = row
                    jsonObj = {
                        "date": date.strftime("%d-%m-%Y %H:%M:%S"),
                        "useremail": email,
                        "amount": amount,
                        "restaurant_name": name,
                        "restaurant_address": address
                    }

                    json_objects.append(jsonObj)
                return jsonify(json_objects), 200




app.add_url_rule('/', 'home', home, methods=['GET'])
app.add_url_rule('/verify_login', 'verify_login', verify_login, methods=['POST'])
app.add_url_rule('/restaurant_statistics', 'restaurant_statistics', get_restaurant_statistics, methods=['GET'])
app.add_url_rule('/transactions_weekly', 'transactions_weekly', get_weekly_transactions_for_user, methods=['GET'])

user_view = UserAPI.as_view('user_api')
app.add_url_rule('/users', view_func=user_view, methods=['GET', 'POST', 'PUT', 'DELETE'])

promotion_view = PromotionAPI.as_view('owner_api')
app.add_url_rule('/promotions', view_func=promotion_view, methods=['GET', 'POST', 'DELETE'])

restaurant_view = RestaurantAPI.as_view('restaurant_api')
app.add_url_rule('/restaurants', view_func=restaurant_view, methods=['GET', 'PUT'])

restaurant_categories_view = RestaurantCategoriesAPI.as_view('restaurant_categories_api')
app.add_url_rule('/restaurant_categories', view_func=restaurant_categories_view, methods=['GET'])

recommendation_view = RecommendationAPI.as_view('recommendation_api')
app.add_url_rule('/recommendations', view_func=recommendation_view, methods=['GET', 'POST', 'DELETE'])

transaction_view = TransactionAPI.as_view('transaction_api')
app.add_url_rule('/transactions', view_func=transaction_view, methods=['GET', 'POST', 'PUT', 'DELETE'])

review_view = ReviewAPI.as_view('review_api')
app.add_url_rule('/reviews', view_func=review_view, methods=['GET', 'POST', 'PUT', 'DELETE'])

budget_view = BudgetAPI.as_view('budget_api')
app.add_url_rule('/budgets', view_func=budget_view, methods=['GET', 'POST', 'PUT', 'DELETE'])

if __name__ == "__main__":
    app.run(host='0.0.0.0')
