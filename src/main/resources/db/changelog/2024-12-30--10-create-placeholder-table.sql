--liquibase formatted
--changeset melkinda:create-placeholder-table
CREATE TABLE notifications.placeholder (
	id SERIAL PRIMARY KEY,
	alias VARCHAR NOT NULL UNIQUE,
	placeholder_name VARCHAR NOT NULL,
	placeholder_wrapped_value VARCHAR GENERATED ALWAYS AS ('${' || column_1 || '}') STORED,
	description VARCHAR NULL,
);
--rollback DROP TABLE notifications.placeholder CASCADE;