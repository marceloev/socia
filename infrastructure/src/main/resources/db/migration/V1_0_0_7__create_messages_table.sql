CREATE TABLE messages
(
    id              UUID                     NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    chat_id         UUID                     NOT NULL,
    status          VARCHAR(20)              NOT NULL,
    role            VARCHAR(20)              NOT NULL,
    content         TEXT,
    metadata        jsonb                    not null,
    next_check_time timestamp with time zone not null,
    CONSTRAINT pk_messages PRIMARY KEY (id),
    CONSTRAINT fk_messages_chat FOREIGN KEY (chat_id) REFERENCES chats (id)
);

CREATE INDEX idx_message_chat_id ON messages (chat_id, created_at desc);

CREATE INDEX idx_messages_reserve_polling
    ON messages (next_check_time ASC, chat_id)
    WHERE status IN ('RECEIVED', 'COMPLETED', 'FAILED');