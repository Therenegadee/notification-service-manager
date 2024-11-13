--liquibase formatted sql

--changeset melkinda:init-notification-subscription-table
CREATE TABLE IF NOT EXISTS notifications.subscription (
    id serial PRIMARY KEY,
    user_id integer not null,
    notification_event_id integer not null,
    notification_channel_id integer not null,
    FOREIGN KEY notification_event_id REFERENCES notifications.event(id),
    FOREIGN KEY notification_channel_id REFERENCES notifications.channel(id),
    UNIQUE (user_id, notification_event_id, notification_channel_id)
)
--rollback DROP TABLE notifications.subscription CASCADE;