CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    CONSTRAINT uk_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX idx_user_roles_role_id
    ON user_roles (role_id);

INSERT INTO roles (id, name) VALUES
    ('00000000-0000-0000-0000-000000000001', 'CUSTOMER'),
    ('00000000-0000-0000-0000-000000000002', 'RESTAURANT_OWNER'),
    ('00000000-0000-0000-0000-000000000003', 'DELIVERY_DRIVER'),
    ('00000000-0000-0000-0000-000000000004', 'ADMIN');
