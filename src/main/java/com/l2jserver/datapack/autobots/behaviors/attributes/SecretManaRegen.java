package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;

public interface SecretManaRegen {
    default void regenMana(Autobot player) {
        if (player.getMpPercentage() < 20) {
            player.setCurrentMp(player.getMaxMp());
        }
    }
}
