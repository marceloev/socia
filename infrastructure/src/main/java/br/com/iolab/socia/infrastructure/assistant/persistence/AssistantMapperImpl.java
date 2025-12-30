package br.com.iolab.socia.infrastructure.assistant.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.assistant.Assistant;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.types.AssistantProviderType;
import br.com.iolab.socia.domain.assistant.types.AssistantStatusType;
import br.com.iolab.socia.domain.organization.OrganizationID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.AssistantsRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class AssistantMapperImpl extends ModelMapper<Assistant, AssistantsRecord> {
    @Override
    public @NonNull AssistantsRecord fromModel (@NonNull final Assistant assistant) {
        return new AssistantsRecord(
                mapNullable(assistant.getId(), ModelID::value),
                assistant.getCreatedAt(),
                assistant.getUpdatedAt(),
                mapNullable(assistant.getOrganizationID(), ModelID::value),
                mapNullable(assistant.getStatus(), Enum::name),
                mapNullable(assistant.getPhone(), Phone::value),
                mapNullable(assistant.getProvider(), Enum::name),
                assistant.getVersion(),
                assistant.getPrompt()
        );
    }

    @Override
    public @NonNull Assistant toModel (@NonNull final AssistantsRecord record) {
        return Assistant.with(
                mapNullable(record.getId(), AssistantID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                mapNullable(record.getOrganizationId(), OrganizationID::from),
                mapNullable(record.getStatus(), AssistantStatusType::valueOf),
                mapNullable(record.getPhone(), Phone::of),
                mapNullable(record.getProvider(), AssistantProviderType::valueOf),
                record.getVersion(),
                record.getPrompt()
        );
    }
}
