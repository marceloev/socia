package br.com.iolab.socia.domain.chat.message.strategy.perform;

import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.knowledge.Knowledge;
import br.com.iolab.socia.domain.chat.Chat;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.MessageID;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class PerformMessageStrategyInput {
    private final Chat chat;
    private final Assistant assistant;
    private final List<Message> history;
    private final Map<MessageID, List<MessageResource>> resources;
    private final List<Knowledge> knowledge;

    private PerformMessageStrategyInput (@NonNull Builder builder) {
        this.chat = builder.chat;
        this.assistant = builder.assistant;
        this.history = builder.history;
        this.resources = builder.resources;
        this.knowledge = builder.knowledge;
    }

    public static @NonNull ChatStep builder () {
        return new Builder();
    }

    public interface ChatStep {
        AssistantStep chat (@NonNull Chat chat);
    }

    public interface AssistantStep {
        HistoryStep assistant (@NonNull Assistant assistant);
    }

    public interface HistoryStep {
        ResourceStep history (@NonNull List<Message> history);
    }

    public interface ResourceStep {
        KnowledgeStep resource (@NonNull Map<MessageID, List<MessageResource>> resources);
    }

    public interface KnowledgeStep {
        FinishedStep knowledge (@NonNull List<Knowledge> knowledge);
    }

    public interface FinishedStep {
        PerformMessageStrategyInput build ();
    }

    public static class Builder implements ChatStep, AssistantStep, HistoryStep, ResourceStep, KnowledgeStep, FinishedStep {
        private Chat chat;
        private Assistant assistant;
        private List<Message> history;
        private Map<MessageID, List<MessageResource>> resources;
        private List<Knowledge> knowledge;

        @Override
        public AssistantStep chat (@NonNull final Chat chat) {
            this.chat = chat;
            return this;
        }

        @Override
        public HistoryStep assistant (@NonNull final Assistant assistant) {
            this.assistant = assistant;
            return this;
        }

        @Override
        public ResourceStep history (@NonNull final List<Message> history) {
            this.history = history;
            return this;
        }

        @Override
        public FinishedStep knowledge (@NonNull final List<Knowledge> knowledge) {
            this.knowledge = knowledge;
            return this;
        }

        @Override
        public KnowledgeStep resource (@NonNull final Map<MessageID, List<MessageResource>> resources) {
            this.resources = resources;
            return this;
        }

        @Override
        public PerformMessageStrategyInput build () {
            return new PerformMessageStrategyInput(this);
        }
    }
}
