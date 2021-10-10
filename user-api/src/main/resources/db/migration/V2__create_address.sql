CREATE TABLE addresses (
  id              SERIAL PRIMARY KEY,
  address_1       VARCHAR(100) NOT NULL,
  address_2       VARCHAR(100) NOT NULL,
  city            VARCHAR(100) NOT NULL,
  state           VARCHAR(2) NOT NULL,
  zip             VARCHAR(7) NOT NULL,
  country         VARCHAR(30) NOT NULL
);