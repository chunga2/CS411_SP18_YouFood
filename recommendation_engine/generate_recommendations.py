import pandas as pd
import numpy as np
la = np.linalg

import psycopg2



connection = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")
def sql(query, opt=[]):
    with connection as conn:
        with conn.cursor() as cur:
            cur.execute(query, opt)
            return pd.DataFrame(cur.fetchall())
def get_mean_vector(email):
    rids = sql(f"""
                SELECT "RelevantRID".rID, power, c1, c2 ,c3, c4, c5
                FROM (
                       SELECT
                         rID AS rID,
                         COUNT(rID) :: float / (
                           SELECT COUNT(*) :: float
                           FROM "Transaction"
                           WHERE useremail = '{email}'
                         )   as power
                       FROM "Transaction", "Restaurant"
                       WHERE useremail = '{email}'
                             AND "Restaurant".name = "Transaction".restaurant_name
                             AND "Restaurant".address = "Transaction".restaurant_address
                       GROUP BY rid) AS "RelevantRID" JOIN "RestaurantEmbeddings" ON "RelevantRID".rID = "RestaurantEmbeddings".rID;
                """)
    mean_vec = np.zeros((5,))
    for _, rid, power, *vec in rids.itertuples():
        vec = np.array(vec).reshape((5,))
        mean_vec += vec*power
    return mean_vec
def make_recommendations(useremail):
    mean_vec = get_mean_vector(useremail).tolist()
    return sql("""INSERT INTO "Recommendation"(restaurant_name, restaurant_address, date, useremail)
    SELECT name AS restaurant_name, address AS restaurant_address, current_timestamp AS date, %s AS useremail FROM
      ((SELECT rID,
              ((%s-c1)^2 + (%s-c2)^2 + (%s-c3)^2 + (%s-c4)^2 + (%s-c5)^2) AS distance
        FROM "RestaurantEmbeddings") AS "EngagedRID" JOIN "Restaurant" ON "Restaurant".rID = "EngagedRID".rID)
      WHERE name NOT IN (SELECT restaurant_name AS name FROM "Transaction" WHERE useremail = %s)
    ORDER BY distance ASC;  SELECT * FROM "Recommendation";
    """, [useremail] + mean_vec + [useremail])
for _, user in sql("SELECT email FROM \"User\";").itertuples():
    make_recommendations(user)[:10]