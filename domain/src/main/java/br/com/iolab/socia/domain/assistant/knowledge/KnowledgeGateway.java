package br.com.iolab.socia.domain.assistant.knowledge;

import br.com.iolab.commons.domain.model.ModelGateway;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.knowledge.fields.KnowledgeKey;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface KnowledgeGateway extends ModelGateway<Knowledge, KnowledgeID> {
    @NonNull
    List<Knowledge> findByAssistant (@NonNull AssistantID assistantID);

    @NonNull
    Optional<Knowledge> findByKey (@NonNull AssistantID assistantID, @NonNull KnowledgeKey key);

    void upsert(@NonNull Knowledge knowledge);

    void deleteExpired ();
}
