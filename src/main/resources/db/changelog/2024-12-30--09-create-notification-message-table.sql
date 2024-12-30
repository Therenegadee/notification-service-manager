--liquibase formatted
--changeset melkinda:create-notification-message-table
CREATE TABLE notifications.notification_message (
	id SERIAL PRIMARY KEY,
	notification_id INTEGER NOT NULL,
	distribution_channel_id INTEGER NOT NULL,
	message VARCHAR NOT NULL,
	UNIQUE (notification_id, distribution_channel_id),
	FOREIGN KEY(notification_id) REFERENCES notifications.notification(id),
	FOREIGN KEY(distribution_channel_id) REFERENCES notifications.distribution_channel(id)
);
--rollback DROP TABLE notifications.notification_message CASCADE;