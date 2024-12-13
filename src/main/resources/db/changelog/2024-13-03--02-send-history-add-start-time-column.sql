--liquibase formatted sql

--changeset melkinda:send-history-table-add-start-time-column
ALTER TABLE notifications.event_send_history ADD COLUMN start_time timestamp;
--rollback ALTER TABLE notifications.event_send_historyDROP COLUMN start_time;