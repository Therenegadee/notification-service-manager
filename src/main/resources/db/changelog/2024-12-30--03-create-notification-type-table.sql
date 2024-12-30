--liquibase formatted
--changeset melkinda:create-notification-type-table
CREATE TABLE notifications.notification_type (
	id          SERIAL  PRIMARY KEY,
	alias       VARCHAR NOT NULL    UNIQUE,
	description VARCHAR NULL
);
--rollback DROP TABLE notifications.notification_type CASCADE;