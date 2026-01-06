CREATE TABLE knowledge (
    id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    assistant_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    user_id UUID,
    chat_id UUID,
    key VARCHAR(255) NOT NULL,
    value TEXT NOT NULL,
    knowledgeSensitivity VARCHAR(50) NOT NULL,
    confidence DECIMAL(3,2) NOT NULL,
    ttl_days INTEGER,
    rationale TEXT NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_knowledge PRIMARY KEY (id)
);

CREATE INDEX idx_knowledge_assistant ON knowledge(assistant_id);

CREATE UNIQUE INDEX idx_knowledge_assistant_key_unique ON knowledge(assistant_id, key) 
WHERE expires_at IS NULL OR expires_at > NOW();

CREATE INDEX idx_knowledge_expired ON knowledge(expires_at) 
WHERE expires_at IS NOT NULL AND expires_at <= NOW();

COMMENT ON TABLE knowledge IS 'Knowledge learned by assistants';
COMMENT ON COLUMN knowledge.assistant_id IS 'Assistant that owns the knowledge';
COMMENT ON COLUMN knowledge.organization_id IS 'Organization context';
COMMENT ON COLUMN knowledge.user_id IS 'User who requested the knowledge creation (audit)';
COMMENT ON COLUMN knowledge.chat_id IS 'Chat where the knowledge was learned (audit)';
COMMENT ON COLUMN knowledge.key IS 'Knowledge key (e.g., business_hours, return_policy)';
COMMENT ON COLUMN knowledge.value IS 'Knowledge value';
COMMENT ON COLUMN knowledge.knowledgeSensitivity IS 'Sensitivity: PUBLIC, PRIVATE, CONFIDENTIAL';
COMMENT ON COLUMN knowledge.confidence IS 'Confidence level (0.0 to 1.0)';
COMMENT ON COLUMN knowledge.ttl_days IS 'Time to live in days';
COMMENT ON COLUMN knowledge.rationale IS 'Rationale for why it was created';
COMMENT ON COLUMN knowledge.expires_at IS 'Calculated expiration date';
