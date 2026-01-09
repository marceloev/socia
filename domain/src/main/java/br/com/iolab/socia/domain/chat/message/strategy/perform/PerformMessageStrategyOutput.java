package br.com.iolab.socia.domain.chat.message.strategy.perform;

import br.com.iolab.socia.domain.assistant.knowledge.Knowledge;
import br.com.iolab.socia.domain.chat.message.Message;
import br.com.iolab.socia.domain.chat.message.resource.MessageResource;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class PerformMessageStrategyOutput {
    private final List<Message> messages;
    private final List<MessageResource> resources;
    private final List<Knowledge> knowledges;

    private PerformMessageStrategyOutput (@NonNull final Builder builder) {
        this.messages = builder.messages;
        this.resources = builder.resources;
        this.knowledges = builder.knowledges;
    }

    public static @NonNull MessageStep builder () {
        return new Builder();
    }

    public interface MessageStep {
        ResourcesStep message (@NonNull List<Message> messages);
    }

    public interface ResourcesStep {
        KnowledgeStep resources (@NonNull List<MessageResource> resources);
    }

    public interface KnowledgeStep {
        FinishedStep knowledge (@NonNull List<Knowledge> knowledges);
    }

    public interface FinishedStep {
        PerformMessageStrategyOutput build ();
    }

    public static class Builder implements MessageStep, ResourcesStep, KnowledgeStep, FinishedStep {
        private List<Message> messages;
        private List<MessageResource> resources;
        private List<Knowledge> knowledges;

        @Override
        public ResourcesStep message (@NonNull final List<Message> messages) {
            this.messages = messages;
            return this;
        }

        @Override
        public KnowledgeStep resources (@NonNull final List<MessageResource> resources) {
            this.resources = resources;
            return this;
        }

        @Override
        public FinishedStep knowledge (@NonNull final List<Knowledge> knowledges) {
            this.knowledges = knowledges;
            return this;
        }

        @Override
        public PerformMessageStrategyOutput build () {
            return new PerformMessageStrategyOutput(this);
        }
    }
}
