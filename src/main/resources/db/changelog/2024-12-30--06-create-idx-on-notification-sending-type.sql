--liquibase formatted
--changeset melkinda:create-idx-on-notification-sending-type
CREATE INDEX idx_execution_type ON notifications.notification(execution_type)
WHERE (is_active = true);
-- rollback DROP INDEX idx_execution_type;