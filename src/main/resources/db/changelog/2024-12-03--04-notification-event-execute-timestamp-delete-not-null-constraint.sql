--liquibase formatted sql

--changeset melkinda:notification-event-execute-timestamp-delete-not-null-constraint
ALTER TABLE notifications."event" ALTER COLUMN execute_timestamp DROP NOT NULL;
--rollback ALTER TABLE notifications."event" ALTER COLUMN execute_timestamp SET NOT NULL;