--liquibase formatted sql

--changeset melkinda:notification-event-add-timezone-to-timestamp-execute-events
ALTER TABLE notifications."event" ALTER COLUMN execute_timestamp TYPE timestamptz USING execute_timestamp::timestamptz;
--rollback ALTER TABLE notifications."event" ALTER COLUMN execute_timestamp TYPE timestamp USING execute_timestamp::timestamp;;