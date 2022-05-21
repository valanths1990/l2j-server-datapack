package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.interfaces.IEventListener;

public class PvPListener implements IEventListener {
    private final L2PcInstance player;

    public PvPListener(L2PcInstance player) {
        this.player = player;
    }

    @Override
    public boolean isOnEvent() {
        return false;
    }

    @Override
    public boolean isBlockingExit() {
        return false;
    }

    @Override
    public boolean isBlockingDeathPenalty() {
        return false;
    }

    @Override
    public boolean canRevive() {
        return false;
    }

    @Override
    public L2PcInstance getPlayer() {
        return player;
    }
}
