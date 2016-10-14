CREATE TABLE user
(
  id       BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
  version  BIGINT DEFAULT 1                  NOT NULL,
  created  TIMESTAMP DEFAULT now()           NOT NULL,
  modified TIMESTAMP,
  username VARCHAR(32)                       NOT NULL,
  password VARCHAR(32)                       NOT NULL,
  status   INT DEFAULT 0                     NOT NULL
);
CREATE UNIQUE INDEX "user_username_uindex"
  ON user (username);