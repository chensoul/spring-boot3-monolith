CREATE TABLE public.users(
    id bigserial PRIMARY KEY,
    name varchar(255) NOT NULL,
    key varchar(255) NOT NULL UNIQUE,
    is_active boolean NOT NULL DEFAULT FALSE,
    created_by varchar(255),
    updated_by varchar(255),
    created_at timestamp NOT NULL DEFAULT current_timestamp,
    updated_at timestamp NOT NULL DEFAULT current_timestamp
);
CREATE INDEX user_key_is_active_idx ON public.users (key, is_active);
INSERT INTO users ( name, key, is_active) VALUES ( 'test','123', true);
