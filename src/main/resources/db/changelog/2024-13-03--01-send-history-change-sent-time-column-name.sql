--liquibase formatted sql

--changeset melkinda:send-history-table-change-sent-time-column-name
ALTER TABLE notifications.event_send_history RENAME COLUMN sent_time TO finish_time;
--rollback ALTER TABLE notifications.event_send_history RENAME COLUMN sent_time TO finish_time;