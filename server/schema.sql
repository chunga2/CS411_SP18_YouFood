CREATE TABLE "User"(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL,
    uID SERIAL UNIQUE
);
CREATE TABLE "Restaurant"(
    address text NOT NULL,
    name text NOT NULL,
    pricerange int,
    phone text NOT NULL,
    image_url text NOT NULL,
    lat float,
    lon float,
    rID SERIAL UNIQUE,
    PRIMARY KEY (address, name)
);
CREATE TABLE "Budget"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email) ON DELETE CASCADE,
    total money NOT NULL,
    PRIMARY KEY (useremail, date)
);
CREATE TABLE "Transaction"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email) ON DELETE CASCADE,
    amount money NOT NULL,
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    PRIMARY KEY (useremail, date)
);

CREATE TABLE "Recommendation"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email) ON DELETE CASCADE,
    restaurant_name text,
    restaurant_address text,
    distance real,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    PRIMARY KEY (useremail, restaurant_name, restaurant_address)
);

CREATE TABLE "Review"(
    useremail citext REFERENCES "User"(email) ON DELETE CASCADE,
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    description text,
    rating numeric
        CONSTRAINT onetoten CHECK (rating <= 10 AND rating >= 0) NOT NULL,
    date timestamp NOT NULL,
    PRIMARY KEY(useremail, restaurant_name, restaurant_address, date)
);

CREATE TABLE "RestaurantCategories" (
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    category text NOT NULL,
    PRIMARY KEY (category, restaurant_name, restaurant_address)
);

CREATE TABLE "RestaurantEmbeddings"(
  rID INTEGER REFERENCES "Restaurant"(rID) PRIMARY KEY,
  c1 REAL,
  c2 REAL,
  c3 REAL,
  c4 REAL,
  c5 REAL
);

CREATE VIEW "BudgetStatistics" AS (
  SELECT "Budget".useremail,
    AVG(amount::DECIMAL)::MONEY AS avg_spend,
    SUM(amount) AS total_spend,
    total AS budget,
    "Budget".date AS date 
  FROM "Transaction", "Budget"
  WHERE "Budget".date <= "Transaction".date
        AND "Transaction".date < "Budget".date + '7 days'::interval
        AND "Transaction".useremail = "Budget".useremail
  GROUP BY "Budget".date, "Budget".useremail);
