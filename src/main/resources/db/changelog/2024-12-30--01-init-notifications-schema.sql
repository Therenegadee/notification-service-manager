--liquibase formatted sql

--changeset melkinda:init-notifications-schema
CREATE SCHEMA IF NOT EXISTS notifications;
--rollback DROP SCHEMA notifications CASCADE;