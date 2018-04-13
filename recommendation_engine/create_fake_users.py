from datetime import datetime, timedelta

from tqdm import tqdm

categories = [
    "Southern",
    "Soul Food",
    "Mexican",
    "Fish & Chips",
    "Bakeries",
    "Wine Bars",
    "Bagels",
    "Juice Bars & Smoothies",
    "Wraps",
    "Breweries",
    "Hot Pot",
    "Seafood Markets",
    "Shanghainese",
    "Cafes",
    "Restaurants",
    "Hawaiian",
    "Korean",
    "Pubs",
    "Street Vendors",
    "Brewpubs",
    "Barbeque",
    "Szechuan",
    "Pan Asian",
    "Grocery",
    "Ice Cream & Frozen Yogurt",
    "Diners",
    "Middle Eastern",
    "Mediterranean",
    "Vietnamese",
    "Gelato",
    "Beer, Wine & Spirits",
    "Gluten-Free",
    "Modern European",
    "Food Trucks",
    "Shaved Ice",
    "Desserts",
    "Steakhouses",
    "Creperies",
    "Organic Stores",
    "Vegan",
    "Irish",
    "Chicken Wings",
    "Caterers",
    "Poke",
    "Japanese",
    "Sandwiches",
    "Fast Food",
    "Indian",
    "Noodles",
    "Burgers",
    "Vegetarian",
    "Karaoke",
    "Spanish",
    "Buffets",
    "Seafood",
    "Hookah Bars",
    "American (Traditional)",
    "Chocolatiers & Shops",
    "Cheesesteaks",
    "Lounges",
    "Ramen",
    "Italian",
    "Taiwanese",
    "Pizza",
    "Internet Cafes",
    "Pool Halls",
    "Salad",
    "Pasta Shops",
    "German",
    "Cafeteria",
    "Chinese",
    "Hot Dogs",
    "Kosher",
    "Arcades",
    "Cajun/Creole",
    "Empanadas",
    "Coffee & Tea",
    "Beer Bar",
    "Asian Fusion",
    "Greek",
    "Thai",
    "Venues & Event Spaces",
    "Caribbean",
    "American (New)",
    "Dive Bars",
    "Delis",
    "Mongolian",
    "Soup",
    "Breakfast & Brunch",
    "Tex-Mex",
    "Meat Shops",
    "Comfort Food",
    "Food Court",
    "Bars",
    "Sports Bars",
    "Sushi Bars",
]

import psycopg2
from random import choices, random
N_USERS = 500

connection = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")

users_list = []
names_list = []
tastes_list = []
for i in range(N_USERS):
    users_list += [f"test{i}@illinois.net"]
    names_list += [f"Test {i}"]
    tastes_list += [choices(categories, k=3)]

with connection as conn:
    with conn.cursor() as cur:
        for email, username, tastes in tqdm(zip(users_list, names_list, tastes_list), total=N_USERS):
            cur.execute("""SELECT restaurant_name, restaurant_address FROM "RestaurantCategories"
                            WHERE category = %s
                            OR category = %s
                            OR category = %s
                        ORDER BY random() ASC;""", tastes)
            tuples = cur.fetchall()[:5]
            cur.execute("""INSERT INTO "User"(email, name, hashedpass) VALUES (%s, %s, %s);""",
                        [email, username, "123"])
            subinserts = []
            for name, address in tuples:
                subinserts += [
                    f"""(%s, {repr(email)}, {20}, '{name.replace("'", "''")}', '{address.replace("'", "''")}')"""]
            start = datetime.now()
            end = start + timedelta(hours=1)
            nows = [start + (end - start) * random() for _ in range(len(tuples))]
            g_query = f"""INSERT INTO "Transaction" VALUES {", ".join(subinserts)};"""
            cur.execute(g_query, nows)
        conn.commit()
