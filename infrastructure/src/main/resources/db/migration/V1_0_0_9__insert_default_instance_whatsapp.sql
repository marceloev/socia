-- Insere instância padrão do WhatsApp (idempotente)
INSERT INTO instances (id, created_at, updated_at, assistant_id, showcase, status, origin, account)
SELECT '3428448c-8982-4dff-996b-921266dcce27'::uuid,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP,
       NULL,
       true,
       'ACTIVE',
       'WHATSAPP',
       '+553491325041'
WHERE NOT EXISTS (
    SELECT 1 FROM instances WHERE id = '3428448c-8982-4dff-996b-921266dcce27'::uuid
);
