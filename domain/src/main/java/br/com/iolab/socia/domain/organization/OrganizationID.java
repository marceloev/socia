package br.com.iolab.socia.domain.organization;

import br.com.iolab.commons.domain.model.ModelID;
import br.com.iolab.commons.domain.utils.IDUtils;
import lombok.NonNull;

import java.time.Instant;
import java.util.UUID;

public class OrganizationID extends ModelID {
    protected OrganizationID (@NonNull final UUID value) {
        super(value);
    }

    public static @NonNull OrganizationID from (@NonNull final UUID value) {
        return new OrganizationID(value);
    }

    public static @NonNull OrganizationID generate (@NonNull final Instant instant) {
        return new OrganizationID(IDUtils.generate(instant));
    }
}
