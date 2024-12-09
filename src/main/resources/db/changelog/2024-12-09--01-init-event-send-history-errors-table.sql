--liquibase formatted sql

--changeset melkinda:init-notification-event-send-history-errors-table
CREATE TABLE IF NOT EXISTS notifications.event_send_history_error (
    id bigserial PRIMARY KEY,
    event_send_history_id integer not null,
    message varchar not null,
    exception_name varchar not null,
    details varchar not null,
    FOREIGN KEY (event_send_history_id) REFERENCES notifications.event_send_history(id)
);
--rollback DROP TABLE notifications.event_send_history_error CASCADE;