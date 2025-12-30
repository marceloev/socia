package br.com.iolab.socia.infrastructure.user.persistence;

import br.com.iolab.commons.infrastructure.persistence.BasicModelGateway;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.user.User;
import br.com.iolab.socia.domain.user.UserGateway;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.infrastructure.jooq.generated.tables.records.UsersRecord;
import lombok.NonNull;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static br.com.iolab.infrastructure.jooq.generated.tables.Users.USERS;

@Repository
public class UserGatewayImpl extends BasicModelGateway<User, UserID, UsersRecord> implements UserGateway {
    protected UserGatewayImpl (
            final DSLContext readOnlyDSLContext,
            final DSLContext writeOnlyDSLContext,
            final UserMapperImpl userMapper
    ) {
        super(
                readOnlyDSLContext,
                writeOnlyDSLContext,
                userMapper,
                USERS,
                USERS.ID,
                USERS.UPDATED_AT
        );
    }

    @Override
    public Optional<User> findByPhone (@NonNull final Phone phone) {
        return this.readOnlyDSLContext
                .selectFrom(USERS)
                .where(USERS.PHONE.eq(phone.value()))
                .fetchOptional()
                .map(this.mapper::toModel);
    }
}
