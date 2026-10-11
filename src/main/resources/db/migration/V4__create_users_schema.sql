CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(255),
    uid VARCHAR(255) NOT NULL UNIQUE,
    photo_profile VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_users_phone_number
    ON users (phone_number);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_user_roles_user_role UNIQUE (user_id, role)
);
