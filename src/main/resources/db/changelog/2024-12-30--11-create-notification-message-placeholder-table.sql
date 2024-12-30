--liquibase formatted
--changeset melkinda:create-notification-message-placeholder-table
CREATE TABLE notifications.notification_message_placeholder (
	notification_message_id INTEGER NOT NULL,
	placeholder_id INTEGER NOT NULL
	FOREIGN KEY (notification_message_id) REFERENCES notifications.notification_message(id),
	FOREIGN KEY (placeholder_id) REFERENCES notifications.placeholder(id)
);
--rollback DROP TABLE notifications.notification_message_placeholder CASCADE;