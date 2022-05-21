package com.l2jserver.datapack.autobots.behaviors.sequences;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.utils.CancellationToken;

import java.util.concurrent.CompletableFuture;


public interface Sequence {
    Autobot getPlayer();


    CancellationToken getCancellationToken();

    void setCancellationToken(CancellationToken cancellationToken);

    void definition();

    default void execute() {
        if (getPlayer().getActiveSequence() != null) {
            getPlayer().getActiveSequence().getCancellationToken().getCancelLambda().get();
            getPlayer().setActiveSequence(null);
        }
        CompletableFuture<Void> completableFuture
                = CompletableFuture.runAsync(this::definition);
        setCancellationToken(new CancellationToken(() -> completableFuture.cancel(false)));
        completableFuture.thenRun(() -> getPlayer().setActiveSequence(null));
        getPlayer().setActiveSequence(this);
        completableFuture.join();
    }

}
