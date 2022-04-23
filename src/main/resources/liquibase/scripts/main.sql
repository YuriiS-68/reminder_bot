-- liquibase formatted sql

-- changeset yuvis:1
CREATE TABLE notification_task (
    id SERIAL PRIMARY KEY,
    idChat INTEGER NOT NULL,
    notice VARCHAR(300) NOT NULL,
    time TIMESTAMP NOT NULL
);