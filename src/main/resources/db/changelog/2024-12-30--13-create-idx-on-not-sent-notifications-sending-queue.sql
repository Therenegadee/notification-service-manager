--liquibase formatted
--changeset melkinda:create-idx-on-not-sent-notifications-sending-queue
CREATE INDEX idx_notification_not_sent ON notifications.sending_queue(notification_id)
WHERE ((stage)::text <> 'NOT_STARTED'::text);
-- rollback DROP INDEX idx_notification_not_sent;