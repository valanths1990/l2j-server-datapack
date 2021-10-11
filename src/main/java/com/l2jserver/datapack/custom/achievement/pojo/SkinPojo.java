package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.custom.skin.SkinHolder;
import com.l2jserver.gameserver.custom.skin.SkinManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.beans.ConstructorProperties;

public class SkinPojo implements IRewardOperation {

    private final int skinId;

    @ConstructorProperties("skinId")
    public SkinPojo(int skinId) {
        this.skinId = skinId;
    }

    @Override
    public void executeOperation(L2PcInstance pc) {
        SkinManager.getInstance().addNewSkinForPlayer(pc, skinId);
    }

    @Override
    public String getRewardIcon() {
        return SkinManager.getInstance().getSkinHolder(skinId).map(SkinHolder::getIcon).orElse("");
    }

    @Override
    public Long getCount() {
        return 1L;
    }
}
