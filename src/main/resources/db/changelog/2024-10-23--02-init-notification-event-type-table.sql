--liquibase formatted sql

--changeset melkinda:init-notification-event-type-table
CREATE TABLE IF NOT EXISTS notifications.event_type (
    id serial primary key,
    alias varchar(25) not null unique,
    description varchar
)
--rollback DROP TABLE notifications.event_type CASCADE;