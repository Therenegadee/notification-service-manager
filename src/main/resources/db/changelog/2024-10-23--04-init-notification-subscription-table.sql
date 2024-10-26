--liquibase formatted sql

--changeset melkinda:init-notification-subscription-table
CREATE TABLE IF NOT EXISTS notifications.subscription (
    user_id integer not null,
    notification_event_id integer not null,
    notification_channel_id integer not null,
    preferred_cron varchar not null,
    FOREIGN KEY notification_event_id REFERENCES notifications.event(id),
    FOREIGN KEY notification_channel_id REFERENCES notifications.channel(id)
)
--rollback DROP TABLE notifications.subscription CASCADE;