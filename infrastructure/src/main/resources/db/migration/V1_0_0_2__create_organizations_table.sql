CREATE TABLE organizations (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(14),
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_organizations PRIMARY KEY (id)
);
