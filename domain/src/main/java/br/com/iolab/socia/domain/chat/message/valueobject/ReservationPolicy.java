package br.com.iolab.socia.domain.chat.message.valueobject;

import br.com.iolab.commons.domain.utils.InstantUtils;
import br.com.iolab.socia.domain.chat.message.types.MessageStatusType;

import java.time.Duration;
import java.time.Instant;

import static br.com.iolab.commons.types.Checks.*;

public record ReservationPolicy(
        MessageStatusType status,
        int maxChats,
        int maxMessages,
        Instant reservedAt,
        Instant reservedUntil
) {
    public ReservationPolicy {
        checkNonNull(status, "status must not be null");
        checkNonNull(maxChats, "maxChats must be non null.");
        checkNonNull(maxMessages, "maxMessages must be non null.");
        checkNonNull(reservedAt, "reservedAt must be non null.");
        checkNonNull(reservedUntil, "reservedUntil must be non null.");
        check(reservedUntil.isAfter(reservedAt), "ReservedAt must be after ReservedUntil.");
    }

    public static ReservationPolicy with (
            final MessageStatusType status,
            final int maxChats,
            final Duration duration
    ) {
        var now = InstantUtils.now();
        return new ReservationPolicy(
                status,
                maxChats,
                500,
                now,
                now.plus(duration)
        );
    }

    public static ReservationPolicy with (
            final MessageStatusType status,
            final int maxChats,
            final int maxMessages,
            final Duration duration
    ) {
        var now = InstantUtils.now();
        return new ReservationPolicy(
                status,
                maxChats,
                maxMessages,
                now,
                now.plus(duration)
        );
    }
}
