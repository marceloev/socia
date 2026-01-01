package br.com.iolab.socia.infrastructure.assistant.instance.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.infrastructure.jooq.generated.tables.records.InstancesRecord;
import br.com.iolab.socia.domain.assistant.instance.Instance;
import br.com.iolab.socia.domain.assistant.instance.InstanceGateway;
import br.com.iolab.socia.domain.assistant.instance.InstanceID;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static br.com.iolab.infrastructure.jooq.generated.tables.Instances.INSTANCES;

@Repository
public class InstanceGatewayImpl extends BasicModelGateway<Instance, InstanceID, InstancesRecord> implements InstanceGateway {
    protected InstanceGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final InstanceMapperImpl instanceMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                instanceMapper,
                INSTANCES,
                INSTANCES.ID,
                INSTANCES.UPDATED_AT
        );
    }
}
