--liquibase formatted
--changeset melkinda:create-notification-table
CREATE TABLE notifications.notification (
	id SERIAL PRIMARY KEY,
	alias VARCHAR NOT NULL UNIQUE,
	"name" VARCHAR NOT NULL,
	notification_type_id INTEGER NOT NULL,
	description VARCHAR NULL,
	sending_type_id INTEGER NOT NULL,
	is_active BOOL NOT NULL,
	FOREIGN KEY(sending_type_id) REFERENCES notifications.sending_type(id)
);
--rollback DROP TABLE notifications.notification;