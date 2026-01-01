CREATE TABLE instances
(
    id           UUID                     NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    assistant_id UUID,
    showcase     bool                     not null,
    status       VARCHAR(50)              NOT NULL,
    origin       VARCHAR(50)              NOT NULL,
    account      text                     not null,
    CONSTRAINT pk_instances PRIMARY KEY (id),
    CONSTRAINT fk_instances_assistant FOREIGN KEY (assistant_id) REFERENCES assistants (id),
    CONSTRAINT unq_instance_assistant_origin UNIQUE (assistant_id, origin)
);

CREATE INDEX idx_instance_showcase
    ON instances (origin)
    WHERE showcase = true;