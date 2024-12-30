--liquibase formatted
--changeset melkinda:create-cron-notification-table
CREATE TABLE notifications.cron_notification (
	notification_id INTEGER NOT NULL UNIQUE,
	cron_value VARCHAR NOT NULL,
	FOREIGN KEY(notification_id) REFERENCES notifications.notification(id)
);
--rollback DROP TABLE notifications.cron_notification CASCADE;