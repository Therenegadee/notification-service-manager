--liquibase formatted sql

--changeset melkinda:-fill-channels-dictionary-table
INSERT INTO notifications.channel ("name", alias) VALUES ('Telegram', 'TELEGRAM');
--rollback DELETE FROM notifications.channel c WHERE c.alias = 'TELEGRAM';