package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.gameserver.model.actor.L2Playable;

import java.util.function.Function;

public class Consumable {
    private final int consumableId;
    private Function<L2Playable, Boolean> condition = (p) -> true;

    public Consumable(int consumableId, Function<L2Playable, Boolean> condition) {
        this.consumableId = consumableId;
        this.condition = condition;
    }

    public Consumable(int consumableId) {
        this.consumableId = consumableId;
    }

    public int getConsumableId() {
        return consumableId;
    }

    public Function<L2Playable, Boolean> getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "Consumable{" +
                "consumableId=" + consumableId +
                ", condition=" + condition +
                '}';
    }
}
