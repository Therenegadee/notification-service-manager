--liquibase formatted sql

--changeset melkinda:init-notification-event-table
CREATE TABLE IF NOT EXISTS notifications.event (
    id serial primary key,
    alias varchar(25) not null unique,
    name varchar(300) not null,
    event_type_id integer not null,
    description varchar,
    execution_type varchar NOT NULL,
    execute_cron varchar(100) not null,
    execute_timestamp timestamp not null,
    is_active boolean not null,
    FOREIGN KEY (event_type_id) REFERENCES notifications.event_type (id)
);
--rollback DROP TABLE notifications.event CASCADE;

--changeset melkinda:create-execution-type-index
CREATE INDEX idx_execution_type ON notifications.event(execution_type) WHERE is_active = true;
--rollback DROP INDEX idx_execution_type CASCADE;