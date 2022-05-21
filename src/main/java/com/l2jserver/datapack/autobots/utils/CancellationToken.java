package com.l2jserver.datapack.autobots.utils;

import java.util.UUID;
import java.util.function.Supplier;

public class CancellationToken {
    private final Supplier<Boolean> cancelLambda;
    private final UUID id = UUID.randomUUID();

    public CancellationToken(Supplier<Boolean> cancelLambda) {
        this.cancelLambda = cancelLambda;
    }

    public Supplier<Boolean> getCancelLambda() {
        return cancelLambda;
    }

    public UUID getId() {
        return id;
    }
}
