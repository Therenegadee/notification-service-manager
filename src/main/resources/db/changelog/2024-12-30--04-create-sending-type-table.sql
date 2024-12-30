--liquibase formatted
--changeset melkinda:create-sending-type-table
CREATE TABLE notifications.sending_type (
	id          SERIAL  PRIMARY KEY,
	alias       VARCHAR NOT NULL    UNIQUE,
	description VARCHAR NULL
);
--rollback DROP TABLE notifications.sending_type CASCADE;