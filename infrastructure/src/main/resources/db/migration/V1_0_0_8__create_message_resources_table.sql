CREATE TABLE message_resources
(
    id          UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    message_id  UUID                     NOT NULL,
    type        VARCHAR(50)              NOT NULL,
    contentType VARCHAR(255)             NOT NULL,
    file        BYTEA                    NOT NULL,
    CONSTRAINT pk_message_resources PRIMARY KEY (id),
    CONSTRAINT fk_message_resources_message FOREIGN KEY (message_id) REFERENCES messages (id)
);

CREATE INDEX idx_message_resources_message_id ON message_resources (message_id);
