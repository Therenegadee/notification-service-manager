--liquibase formatted sql

--changeset melkinda:init-notification-message-table
CREATE TABLE IF NOT EXISTS notifications.message (
    id serial PRIMARY KEY,
    notification_event_id integer not null,
    notification_channel_id integer not null,
    message varchar not null,
    UNIQUE(notification_event_id, notification_channel_id),
    FOREIGN KEY notification_event_id REFERENCES notifications.event(id),
    FOREIGN KEY notification_channel_id REFERENCES notifications.channel(id)
)
--rollback DROP TABLE notifications.message CASCADE;