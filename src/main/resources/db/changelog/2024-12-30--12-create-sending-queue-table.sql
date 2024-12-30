--liquibase formatted
--changeset melkinda:create-sending-queue-table
CREATE TABLE notifications.sending_queue (
	id BIGSERIAL PRIMARY KEY,
	notification_id INTEGER NOT NULL,
	stage VARCHAR NOT NULL,
	finish_time TIMESTAMP NULL,
	start_time TIMESTAMP NULL,
	FOREIGN KEY (notification_id) REFERENCES notifications.notification(id)
);
--rollback DROP TABLE notifications.sending_queue CASCADE;