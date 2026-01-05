package br.com.iolab.socia.domain.user;

import br.com.iolab.commons.domain.model.Model;
import br.com.iolab.commons.domain.utils.IDUtils;
import br.com.iolab.commons.domain.validation.Result;
import br.com.iolab.commons.types.fields.Email;
import br.com.iolab.commons.types.fields.Phone;
import br.com.iolab.socia.domain.user.types.UserStatusType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.Instant;

import static br.com.iolab.commons.domain.utils.InstantUtils.now;
import static br.com.iolab.commons.types.Checks.checkNonNull;
import static br.com.iolab.commons.types.Checks.checkNotBlank;
import static br.com.iolab.socia.domain.user.types.UserStatusType.ACTIVE;
import static br.com.iolab.socia.domain.user.types.UserStatusType.PROSPECT;

@Getter
@ToString
public class User extends Model<UserID> {
    private final String name;
    private final Email email;
    private final String password;
    private final Phone phone;
    private final UserStatusType status;

    @Builder(toBuilder = true, access = AccessLevel.PRIVATE)
    private User (
            @NonNull final UserID id,
            @NonNull final Instant createdAt,
            @NonNull final Instant updatedAt,
            final String name,
            final Email email,
            final String password,
            final Phone phone,
            final UserStatusType status
    ) {
        super(id, createdAt, updatedAt);
        this.name = checkNotBlank(name, "Nome não pode ser vazio!");
        this.email = checkNonNull(email, "Email não pode ser nulo!");
        this.password = checkNotBlank(password, "Senha não pode ser vazia!");
        this.phone = checkNonNull(phone, "Telefone não pode ser nulo!");
        this.status = checkNonNull(status, "Status não pode ser nulo!");
    }

    public static Result<User> prospect (
            final Phone phone
    ) {
        var now = now();
        var id = UserID.generate(now);
        return new User(
                UserID.generate(now),
                now,
                now,
                "Desconhecido",
                Email.of(id.toString().concat("@gmail.com")),
                IDUtils.generate().toString(),
                phone,
                PROSPECT
        ).validate();
    }

    public static Result<User> create (
            final String name,
            final Email email,
            final String password,
            final Phone phone
    ) {
        var now = now();
        return new User(
                UserID.generate(now),
                now,
                now,
                name,
                email,
                password,
                phone,
                ACTIVE
        ).validate();
    }

    public static @NonNull User with (
            final UserID id,
            final Instant createdAt,
            final Instant updatedAt,
            final String name,
            final Email email,
            final String password,
            final Phone phone,
            final UserStatusType status
    ) {
        return new User(
                id,
                createdAt,
                updatedAt,
                name,
                email,
                password,
                phone,
                status
        );
    }

    @Override
    protected Result<User> validate () {
        var result = Result.builder(this);
        return result.build();
    }
}
