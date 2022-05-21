package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;

import java.util.function.Function;

//internal data class MiscItem(val miscItemId: (Player) -> Int, val countToGive: Int = 500, val minimumAmountBeforeGive: Int =  20)
public class MiscItem {
    private final Function<Autobot, Integer> miscItemId;
    private int countToGive = 500;
    private int minimumAmountBeforeGive = 20;

    public MiscItem(Function<Autobot, Integer> miscItemId) {
        this.miscItemId = miscItemId;
    }

    public MiscItem(Function<Autobot, Integer> miscItemId, int countToGive, int minimumAmountBeforeGive) {
        this.miscItemId = miscItemId;
        this.countToGive = countToGive;
        this.minimumAmountBeforeGive = minimumAmountBeforeGive;
    }

    public Function<Autobot, Integer> getMiscItemId() {
        return miscItemId;
    }

    public int getCountToGive() {
        return countToGive;
    }

    public int getMinimumAmountBeforeGive() {
        return minimumAmountBeforeGive;
    }
}
