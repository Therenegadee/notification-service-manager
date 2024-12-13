--liquibase formatted sql

--changeset melkinda:notification-event-table-create-execute-timestamp-index
CREATE INDEX idx_event_execute_timestamp ON notifications.event (execute_timestamp);
--rollback DROP INDEX idx_event_execute_timestamp;