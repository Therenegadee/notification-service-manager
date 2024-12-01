--liquibase formatted sql

--changeset melkinda:init-message-placeholder-table
CREATE TABLE IF NOT EXISTS notifications.notification_message_placeholder (
    notification_message_id integer not null,
    placeholder_id integer not null,
    FOREIGN KEY (notification_message_id) REFERENCES notifications.message (id),
    FOREIGN KEY (placeholder_id) REFERENCES notifications.placeholder (id),
)
--rollback DROP TABLE notifications.notification_message_placeholder CASCADE;