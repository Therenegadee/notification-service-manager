--liquibase formatted sql

--changeset melkinda:-fill-event-type-dictionary-table
INSERT INTO notifications.placeholder (alias, placeholder_value, description)
VALUES
('RECIPIENT_NAME', 'recipient_name', 'Notification recipient''s name.'),
('RECIPIENT_FULL_NAME', 'recipient_full_name', 'Notification recipient''s full name.'),
('RECIPIENT_BIRTHDAY', 'recipient_birthday', 'Notification recipient''s birthday date.');
--rollback DELETE FROM notifications.placeholder p WHERE p.alias IN ('RECIPIENT_NAME', 'RECIPIENT_FULL_NAME', 'RECIPIENT_BIRTHDAY');