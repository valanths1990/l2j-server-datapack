package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.model.L2TeleportLocation;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.beans.ConstructorProperties;

public class TeleportPojo implements IRewardOperation {
    private final int teleportId;

    @ConstructorProperties("teleportId")
    public TeleportPojo(int teleportId) {
        this.teleportId = teleportId;
    }

    @Override
    public void executeOperation(L2PcInstance pc) {
        L2TeleportLocation loc = TeleportLocationTable.getInstance().getTemplate(teleportId);
        if (loc != null) {
            pc.teleToLocation(new Location(loc.getLocX(), loc.getLocY(), loc.getLocZ()));
        }
    }

    @Override
    public String getRewardIcon() {
        return "bookmark_scroll_i00";
    }

    @Override
    public Long getCount() {
        return 1L;
    }
}
