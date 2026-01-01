CREATE TABLE chats (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    organization_id UUID NOT NULL,
    assistant_id UUID NOT NULL,
    instance_id uuid not null,
    user_id UUID NOT NULL,
    phone_to VARCHAR(20) NOT NULL,
    phone_from VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    count BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_chats PRIMARY KEY (id),
    CONSTRAINT fk_chats_organization FOREIGN KEY (organization_id) REFERENCES organizations(id),
    CONSTRAINT fk_chats_assistant FOREIGN KEY (assistant_id) REFERENCES assistants(id),
    CONSTRAINT fk_chats_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT unq_chats_to_from UNIQUE (phone_to, phone_from)
);
