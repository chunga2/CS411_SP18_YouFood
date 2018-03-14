CREATE TABLE Owner(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL
);
CREATE TABLE User(
    email citext
        CONSTRAINT proper_email CHECK (email ~* '^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$') PRIMARY KEY,
    name text NOT NULL,
    hashedpass char(20) NOT NULL
);
CREATE TABLE Budget(
    startdate timetz NOT NULL,
    useremail REFERENCES User(email),
    term timetz NOT NULL,
    total money NOT NULL,
    used money NOT NULL
    PRIMARY KEY (useremail, startdate)
);
CREATE TABLE Recommendation(
    date timetz NOT NULL,
    useremail REFERENCES User(email),
    restaurant REFERENCES Restaurant(address),
    PRIMARY KEY 
);
CREATE TABLE Review(
    useremail REFERENCES User(email),
    restaurant REFERENCES Restaurant(address),
    description text,
    rating numeric 
        CONSTRAINT onetoten CHECK (rating <= 10 AND rating >= 0) NOT NULL,
    date timetz NOT NULL
)
CREATE TABLE Promotion(
    restaurant REFERENCES Restaurant(address),
)
CREATE TABLE Restaurant(
    address text NOT NULL PRIMARY KEY,
    pricerange int
        CONSTRAINT numdollarsigns CHECK(pricerange <=4 AND pricerange >=1) NOT NULL,
    cuisine text NOT NULL,
    name text NOT NULL,
    owner REFERENCES Owner(email)
);