--liquibase formatted sql

--changeset melkinda:init-notification-channel-table
CREATE TABLE IF NOT EXISTS notifications.channel (
    id serial primary key,
    name varchar(100) not null,
    alias varchar(25) not null unique
)
--rollback DROP TABLE notifications.channel CASCADE;