--liquibase formatted sql

--changeset melkinda:init-placeholder-table
CREATE TABLE IF NOT EXISTS notifications.placeholder (
    id serial primary key,
    alias varchar not null unique,
    placeholder_value varchar(300) not null,
    description varchar
)
--rollback DROP TABLE notifications.placeholder CASCADE;