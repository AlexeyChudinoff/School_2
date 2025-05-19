-- liquibase formatted sql
-- changeset alexchudinov:1
CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
  )