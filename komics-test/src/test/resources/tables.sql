CREATE TABLE user
(
  id       VARCHAR(32)  PRIMARY KEY NOT NULL,
--   version  BIGINT DEFAULT 1                  NOT NULL,
  username VARCHAR(32)                       NOT NULL,
  passwd VARCHAR(32)                       NOT NULL,
  mobile   VARCHAR(32)                       NOT NULL,
  email    VARCHAR(32)                       NOT NULL,
  status   INT DEFAULT 0                     NOT NULL
);
CREATE UNIQUE INDEX "user_username_uindex"
  ON user (username);

CREATE TABLE user_profile
(
  id       VARCHAR(32)  PRIMARY KEY NOT NULL,
  user_id  VARCHAR(32)  NOT NULL,
  version  BIGINT DEFAULT 1                  NOT NULL,
  name VARCHAR(32)                       NOT NULL,
  gender CHAR(1)                       NOT NULL,
  age   INTEGER (4)                       NOT NULL
);