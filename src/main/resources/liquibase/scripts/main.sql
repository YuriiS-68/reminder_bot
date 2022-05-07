-- liquibase formatted sql

-- changeset yuvis:1
CREATE TABLE notification_task (
    id SERIAL NOT NULL PRIMARY KEY,
    id_chat INTEGER NOT NULL,
    id_message INTEGER NOT NULL,
    notice TEXT NOT NULL,
    time TIMESTAMP NOT NULL,
    sent_date TIMESTAMP,
    status varchar(200) NOT NULL DEFAULT 'PENDING'
);

-- changeset yuvis:2
CREATE INDEX notification_task_date_idx ON notification_task(time);

-- changeset yuvis:3
--ALTER TABLE notification_task ALTER COLUMN notice TYPE TEXT;

-- changeset yuvis:4
-- ALTER TABLE notification_task DROP COLUMN messageId;