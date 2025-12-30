package br.com.iolab.socia.domain.member;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class MemberID extends ModelID {
    protected MemberID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull MemberID from (@NonNull final UUID value) {
        return new MemberID(value);
    }

    public static @NonNull MemberID generate (@NonNull final Instant instant) {
        return new MemberID(IDUtils.generate(instant));
    }
}
