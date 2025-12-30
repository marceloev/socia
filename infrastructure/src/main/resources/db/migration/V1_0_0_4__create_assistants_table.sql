CREATE TABLE assistants (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    organization_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    provider VARCHAR(20) NOT NULL,
    version VARCHAR(50) NOT NULL,
    prompt TEXT NOT NULL,
    CONSTRAINT pk_assistants PRIMARY KEY (id),
    CONSTRAINT fk_assistants_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT unq_assistants_phone UNIQUE (phone)
);

CREATE INDEX idx_assistants_organization_id ON assistants(organization_id);
