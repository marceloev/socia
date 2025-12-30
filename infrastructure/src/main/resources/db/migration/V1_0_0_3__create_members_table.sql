CREATE TABLE members (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    organization_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT pk_members PRIMARY KEY (id),
    CONSTRAINT fk_members_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_members_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT unq_members_organization_user UNIQUE (organization_id, user_id)
);

CREATE INDEX idx_members_organization_id ON members(organization_id);
CREATE INDEX idx_members_user_id ON members(user_id);
