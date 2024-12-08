--liquibase formatted sql

--changeset melkinda:init-notification-subscription-table
CREATE TABLE IF NOT EXISTS notifications.subscription (
    id serial PRIMARY KEY,
    user_id integer not null,
    notification_event_type_id integer not null,
    notification_channel_id integer not null,
    contact_value varchar not null unique,
    FOREIGN KEY (notification_event_type_id) REFERENCES notifications.event_type(id),
    FOREIGN KEY (notification_channel_id) REFERENCES notifications.channel(id),
    UNIQUE (user_id, notification_event_type_id, notification_channel_id)
)
--rollback DROP TABLE notifications.subscription CASCADE;

--changeset melkinda:create-subscription-notification-event-type-index
CREATE INDEX idx_subscription_notification_event_type ON notifications.subscription (notification_event_type_id);
--rollback DROP INDEX idx_subscription_notification_event_type CASCADE;