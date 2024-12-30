--liquibase formatted
--changeset melkinda:create-sending-error-table
CREATE TABLE notifications.sending_error (
	id BIGSERIAL PRIMARY KEY,
	sending_queue_id INTEGER NOT NULL,
	message VARCHAR NOT NULL,
	exception_name VARCHAR NOT NULL,
	details VARCHAR NOT NULL,
	FOREIGN KEY (sending_queue_id) REFERENCES notifications.sending_queue(id);
);
--rollback DROP TABLE notifications.sending_error CASCADE;