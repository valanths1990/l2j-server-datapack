package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class LevelPojo implements IRewardOperation{
    private final int levelAdd;
    public LevelPojo(int levelAdd){
        this.levelAdd = levelAdd;
    }
    @Override
    public void executeOperation(L2PcInstance pc) {
        pc.setLevel(pc.getLevel()+levelAdd);
    }

    @Override
    public String getRewardIcon() {
        return null;
    }

    @Override
    public Long getCount() {
        return (long)levelAdd;
    }
}
