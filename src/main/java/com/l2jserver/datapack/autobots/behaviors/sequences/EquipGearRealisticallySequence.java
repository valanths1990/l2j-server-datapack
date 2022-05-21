package com.l2jserver.datapack.autobots.behaviors.sequences;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.CancellationToken;

public class EquipGearRealisticallySequence implements Sequence {
    private final Autobot player;
    private CancellationToken cancellationToken;

    public EquipGearRealisticallySequence(Autobot player) {
        this.player = player;
    }

    @Override
    public Autobot getPlayer() {
        return player;
    }

    @Override
    public CancellationToken getCancellationToken() {
        return cancellationToken;
    }

    @Override
    public void setCancellationToken(CancellationToken cancellationToken) {
        this.cancellationToken = cancellationToken;
    }

    @Override
    public void definition() {
        AutobotHelpers.giveItemsByGrade(player, true);
    }
}
