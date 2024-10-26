--liquibase formatted sql

--changeset melkinda:init-notification-event-table
CREATE TABLE IF NOT EXISTS notifications.event (
    id serial primary key,
    alias varchar(25) not null unique,
    name varchar(300) not null,
    description varchar,
    default_cron varchar(100) not null
)
--rollback DROP TABLE notifications.event CASCADE;