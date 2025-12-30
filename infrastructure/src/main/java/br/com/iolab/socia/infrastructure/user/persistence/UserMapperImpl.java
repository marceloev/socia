package br.com.iolab.socia.infrastructure.user.persistence;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.model.ModelMapper;
import br.com.iolab.commons.types.fields.Email;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.user.User;
import br.com.iolab.socia.domain.user.UserID;
import br.com.iolab.socia.domain.user.types.UserStatusType;
import br.com.iolab.infrastructure.jooq.generated.tables.records.UsersRecord;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import static br.com.iolab.commons.types.Optionals.mapNullable;

@Service
public class UserMapperImpl extends ModelMapper<User, UsersRecord> {
    @Override
    public @NonNull UsersRecord fromModel (@NonNull final User user) {
        return new UsersRecord(
                mapNullable(user.getId(), ModelID::value),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getName(),
                mapNullable(user.getEmail(), Email::value),
                user.getPassword(),
                mapNullable(user.getPhone(), Phone::value),
                mapNullable(user.getStatus(), Enum::name)
        );
    }

    @Override
    public @NonNull User toModel (@NonNull final UsersRecord record) {
        return User.with(
                mapNullable(record.getId(), UserID::from),
                record.getCreatedAt(),
                record.getUpdatedAt(),
                record.getName(),
                mapNullable(record.getEmail(), Email::of),
                record.getPassword(),
                mapNullable(record.getPhone(), Phone::of),
                mapNullable(record.getStatus(), UserStatusType::valueOf)
        );
    }
}
