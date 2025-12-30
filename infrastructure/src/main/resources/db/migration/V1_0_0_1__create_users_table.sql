CREATE TABLE users (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT unq_users_email UNIQUE (email),
    CONSTRAINT unq_users_phone UNIQUE (phone)
);
