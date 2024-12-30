--liquibase formatted
--changeset melkinda:create-channel-distribution-table
CREATE TABLE notifications.distribution_channel (
	id      SERIAL  PRIMARY KEY,
	"name"  VARCHAR NOT NULL,
	alias   VARCHAR NOT NULL    UNIQUE
);
--rollback DROP TABLE notifications.distribution_channel CASCADE;