--liquibase formatted
--changeset melkinda:create-subscription-table
CREATE TABLE notifications."subscription" (
	id SERIAL PRIMARY KEY,
	user_id INTEGER NOT NULL,
	notification_type_id INTEGER NOT NULL,
	distribution_channel_id INTEGER NOT NULL,
	contact_value VARCHAR NOT NULL UNIQUE,
	UNIQUE (user_id, notification_type_id, distribution_channel_id),
	FOREIGN KEY (distribution_channel_id) REFERENCES notifications.distribution_channel(id),
	FOREIGN KEY (notification_type_id) REFERENCES notifications.notification_type(id)
);
--rollback DROP TABLE notifications."subscription" CASCADE;