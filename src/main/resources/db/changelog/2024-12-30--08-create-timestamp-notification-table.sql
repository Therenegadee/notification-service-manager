--liquibase formatted
--changeset melkinda:create-timestamp-notification-table
CREATE TABLE notifications.timestamp_notification (
	notification_id INTEGER NOT NULL UNIQUE,
	timestamp TIMESTAMP NOT NULL,
	FOREIGN KEY(notification_id) REFERENCES notifications.notification(id)
);
--rollback DROP TABLE notifications.timestamp_notification CASCADE;