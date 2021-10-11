package com.l2jserver.datapack.instances;

import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;

import java.util.function.BiConsumer;

public final class OnCaptureTowerAction implements BiConsumer<CaptureTower, L2PcInstance> {

    @Override
    public void accept(CaptureTower entryTower, L2PcInstance l2PcInstance) {
        boolean canEnter = false;
        if (entryTower.getCapturer() == null) {
            return;
        }
        if (l2PcInstance == entryTower.getCapturer()) {
            canEnter = true;
        } else if (entryTower.getCapturer().getParty() != null) {
            if (entryTower.getCapturer().getParty().containsPlayer(l2PcInstance)) {
                canEnter = true;
            } else if (entryTower.getCapturer().getParty().isInCommandChannel()) {
                if (entryTower.getCapturer().getParty().getCommandChannel().containsPlayer(l2PcInstance)) {
                    canEnter = true;
                }
            }
        } else if (entryTower.getCapturer().getClan() != null && entryTower.getCapturer().getClan().isMember(l2PcInstance.getObjectId())) {
            canEnter = true;
        }
        if (canEnter) {
            BypassHandler.getInstance().getHandler("raidboss;homepage").useBypass("raidboss;homepage show", l2PcInstance, entryTower);
        }
    }
}