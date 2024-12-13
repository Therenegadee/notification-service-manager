--liquibase formatted sql

--changeset melkinda:init-notification-event-send-history-table
CREATE TABLE IF NOT EXISTS notifications.event_send_history (
    id bigserial PRIMARY KEY,
    notification_event_id integer not null,
    stage varchar not null,
    sent_time timestamp null,
    FOREIGN KEY notification_event_id REFERENCES notifications.event(id)
)
--rollback DROP TABLE notifications.event_send_history CASCADE;

--changeset melkinda:create-notification-event-not-executed-index
CREATE INDEX idx_notification_event_not_executed ON notifications.event_send_history (notification_event_id)
WHERE stage != 'NOT_STARTED';
--rollback DROP INDEX idx_notification_event_not_executed CASCADE;
