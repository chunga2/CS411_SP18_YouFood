#!/usr/bin/env python3
import pandas as pd
import numpy as np

la = np.linalg
import psycopg2
from scipy.sparse import lil_matrix
from keras.models import Model
from keras.layers import Input, Dense, Softmax
from keras.utils import to_categorical

from tqdm import tqdm

print("Opening database connection...")
connection = psycopg2.connect(dbname="youfood", user="youfood", password="wizard11", host="localhost")


def sql(query):
    with connection as conn:
        with conn.cursor() as cur:
            cur.execute(query)
            return pd.DataFrame(cur.fetchall())


print("Forming co-occurrence matrix...")
with connection as conn:
    with conn.cursor() as cur:
        cur.execute('SELECT MAX(rID) AS rcount, MAX(uID) AS ucount FROM "Restaurant", "User";')
        rcount, ucount = cur.fetchone()
        cur.execute(""" SELECT R1.rID AS r1ID,  R2.rID AS r2ID
                        FROM "Restaurant" AS R1, "Restaurant" AS R2, "Transaction" AS T1, "Transaction" AS T2
                        WHERE T1.useremail = T2.useremail
                              AND R1.name = T1.restaurant_name
                              AND R1.address = T1.restaurant_address
                              AND R2.name = T2.restaurant_name
                              AND R2.address = T2.restaurant_address;""")
        tuples = cur.fetchall()
    matrix = lil_matrix((rcount + 1, rcount + 1))
    for r1, r2 in tqdm(tuples):
        matrix[r1, r2] += 1
    del tuples
    for row in tqdm(range(rcount + 1)):
        if matrix[row, :].sum():
            matrix[row, :] /= matrix[row, :].sum()
    matrix = matrix.todense()

print("Compiling skip-gram model...")
loss = 'kullback_leibler_divergence'
epochs = 1000
print(f"loss: {loss}")
print(f"epochs: {epochs}")
x = Input(shape=(rcount + 1,))
layer1 = Dense(5, use_bias=False)(x)
layer2 = Dense(rcount + 1, use_bias=False)(layer1)
y = Softmax()(layer2)
model = Model(inputs=x, outputs=y)
model.compile(optimizer='adagrad', loss=loss)
seqs = list(range(rcount + 1))
print("Training model...")
model.fit(to_categorical(seqs), matrix, epochs=epochs, verbose=False)
print("Done!")
print("Preparing embeddings for upload...")
vecs = model.layers[1].get_weights()[0]


def tuples():
    for rID, c1, c2, c3, c4, c5 in pd.DataFrame(vecs).itertuples():
        if rID:
            yield f"({rID}, {c1}, {c2}, {c3}, {c4}, {c5})"


values = ", ".join(tuples())
print("Uploading...")
sql(f"""DELETE FROM "RestaurantEmbeddings";
        INSERT INTO "RestaurantEmbeddings"(rID, c1, c2, c3, c4, c5) VALUES {values};
        SELECT * FROM "RestaurantEmbeddings";""")
print("Done!")
