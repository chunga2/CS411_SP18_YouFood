CREATE TABLE "Owner"(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL
);
CREATE TABLE "User"(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL
);
CREATE TABLE "Budget"(
    date timetz NOT NULL,
    useremail citext REFERENCES "User"(email),
    total money NOT NULL,
    PRIMARY KEY (useremail, startdate)
);
CREATE TABLE "Transaction"(
    date timetz NOT NULL,
    useremail citext REFERENCES "User"(email),
    amount money NOT NULL,
    PRIMARY KEY (useremail, date)
);

CREATE TABLE "Recommendation"(
    date timetz NOT NULL,
    useremail citext REFERENCES "User"(email),
    restaurant text REFERENCES "Restaurant"(address),
    PRIMARY KEY 
);
CREATE TABLE "Review"(
    useremail citext REFERENCES "User"(email),
    restaurant text REFERENCES "Restaurant"(address),
    description text,
    rating numeric 
        CONSTRAINT onetoten CHECK (rating <= 10 AND rating >= 0) NOT NULL,
    date timetz NOT NULL
)
CREATE TABLE "Promotion"(
    restaurant text REFERENCES "Restaurant"(address),
)
CREATE TABLE "Restaurant"(
    address text NOT NULL PRIMARY KEY,
    pricerange int
        CONSTRAINT numdollarsigns CHECK(pricerange <=4 AND pricerange >=1) NOT NULL,
    cuisine text NOT NULL,
    name text NOT NULL,
    owner citext REFERENCES "Owner"(email)
);