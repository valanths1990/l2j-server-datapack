package com.l2jserver.datapack.instances;

import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;
import com.l2jserver.gameserver.util.Broadcast;

import java.util.function.Consumer;

public final class OnTowerProgressUpdate implements Consumer<CaptureTower> {
    @Override
    public void accept(CaptureTower captureTower) {
        String name = "";
        name = captureTower.isConquerorIsClan() ? captureTower.getCapturer().getClan().getName() : captureTower.getCapturer().getName();
        captureTower.setTitle(name + " " + captureTower.getProgress() + "%");
        if (captureTower.getProgress() % 25 == 0) {
            Broadcast.toAllOnlinePlayers(name + " has captured " + captureTower.getProgress() + "% of Grand Boss Entry!");
        }
        if (captureTower.getProgress() == 100) {
            captureTower.getTemplate().setIsTargetable(true);
            captureTower.setCollisionRadius(16);
        }
        if (captureTower.getProgress() < 100) {
            captureTower.getTemplate().setIsTargetable(false);
            captureTower.setCollisionRadius(0);
        }
    }
}