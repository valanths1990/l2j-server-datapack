package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;



public class NoblesPojo implements IRewardOperation {

    @Override
    public void executeOperation(L2PcInstance pc) {
        pc.setNoble(true);
    }

    @Override
    public String getRewardIcon() {
        return "icon.skill1323"; //blessing of noblesse
    }

    @Override
    public Long getCount() {
        return 1L;
    }
}
