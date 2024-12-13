--liquibase formatted sql

--changeset melkinda:-fill-event-type-dictionary-table
INSERT INTO notifications.event_type (alias, description)
VALUES
('COMMERCIAL', 'Commercial Event'),
('INFORMATIONAL', 'Informational Event');
--rollback DELETE FROM notifications.event_type c WHERE c.alias IN ('COMMERCIAL', 'INFORMATIONAL');