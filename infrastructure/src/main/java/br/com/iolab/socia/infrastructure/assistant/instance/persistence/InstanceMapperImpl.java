package br.com.iolab.socia.infrastructure.assistant.instance.persistence;

import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.infrastructure.jooq.generated.tables.records.InstancesRecord;
import br.com.iolab.socia.domain.assistant.AssistantID;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import br.com.iolab.socia.domain.assistant.instance.types.InstanceOriginType;
import br.com.iolab.socia.domain.assistant.instance.types.InstanceStatusType;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class InstanceMapperImpl extends ModelMapper<Instance, InstancesRecord> {
    @Override
    public @NonNull InstancesRecord fromModel (@NonNull final Instance instance) {
        return new InstancesRecord(
                instance.getId().value(),
                instance.getCreatedAt(),
                instance.getUpdatedAt(),
                mapNullable(instance.getAssistantID(), AssistantID::value),
                instance.isShowcase(),
                mapNullable(instance.getOrigin(), Enum::name),
                instance.getAccount(),
                mapNullable(instance.getStatus(), Enum::name)
        );
    }

    @Override
    public @NonNull Instance toModel (@NonNull final InstancesRecord instancesRecord) {
        return Instance.with(
                mapNullable(instancesRecord.getId(), InstanceID::from),
                instancesRecord.getCreatedAt(),
                instancesRecord.getUpdatedAt(),
                mapNullable(instancesRecord.getAssistantId(), AssistantID::from),
                instancesRecord.getShowcase(),
                mapNullable(instancesRecord.getOrigin(), InstanceOriginType::valueOf),
                instancesRecord.getAccount(),
                mapNullable(instancesRecord.getStatus(), InstanceStatusType::valueOf)
        );
    }
}
