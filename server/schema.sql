CREATE TABLE "Restaurant"(
    address text NOT NULL,
    pricerange int,
    name text NOT NULL,
    phone text NOT NULL,
    image_url text NOT NULL,
    PRIMARY KEY (address, name)
);
CREATE TABLE "Owner"(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$'),
    name text NOT NULL,
    hashedpass char(20) NOT NULL,
    PRIMARY KEY (email)
);
CREATE TABLE "User"(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL
);
CREATE TABLE "Budget"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email),
    total money NOT NULL,
    PRIMARY KEY (useremail, date)
);
CREATE TABLE "Transaction"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email),
    amount money NOT NULL,
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    PRIMARY KEY (useremail, date)
);

CREATE TABLE "Recommendation"(
    date timestamp NOT NULL,
    useremail citext REFERENCES "User"(email),
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    PRIMARY KEY (useremail, restaurant_name, restaurant_address, date)
);

CREATE TABLE "Review"(
    useremail citext REFERENCES "User"(email),
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    description text,
    rating numeric
        CONSTRAINT onetoten CHECK (rating <= 10 AND rating >= 0) NOT NULL,
    date timestamp NOT NULL,
    PRIMARY KEY(useremail, restaurant_name, restaurant_address, date)
);

CREATE TABLE "Promotion"(
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    date timestamp NOT NULL,
    description text NOT NULL,
    PRIMARY KEY (date, description, restaurant_name, restaurant_address)
);

CREATE TABLE "RestaurantCategories" (
    restaurant_name text,
    restaurant_address text,
    FOREIGN KEY (restaurant_name, restaurant_address) REFERENCES "Restaurant"(name, address),
    category text NOT NULL,
    PRIMARY KEY (category, restaurant_name, restaurant_address)
);
