CREATE TABLE rl_users_addresses (
  user_id         INTEGER NOT NULL,
  address_id      INTEGER NOT NULL,
  PRIMARY KEY(user_id, address_id),
  CONSTRAINT fk_user
    FOREIGN KEY(user_id)
        REFERENCES users(id),
  CONSTRAINT fk_address
    FOREIGN KEY(address_id)
        REFERENCES addresses(id)
);