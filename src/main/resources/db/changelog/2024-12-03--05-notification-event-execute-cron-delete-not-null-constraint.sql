--liquibase formatted sql

--changeset melkinda:notification-event-execute-cron-delete-not-null-constraint
ALTER TABLE notifications."event" ALTER COLUMN execute_cron DROP NOT NULL;
--rollback ALTER TABLE notifications."event" ALTER COLUMN execute_cron SET NOT NULL;