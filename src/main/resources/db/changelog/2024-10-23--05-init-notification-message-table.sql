--liquibase formatted sql

--changeset melkinda:init-notification-message-table
CREATE TABLE IF NOT EXISTS notification.message (
    notification_event_id integer not null,
    notification_channel_id integer not null,
    message varchar not null,
    FOREIGN KEY notification_event_id REFERENCES notifications.event(id),
    FOREIGN KEY notification_channel_id REFERENCES notifications.channel(id)
)
--rollback DROP TABLE notification.message CASCADE;