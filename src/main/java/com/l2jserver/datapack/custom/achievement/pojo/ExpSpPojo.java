package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.beans.ConstructorProperties;

public class ExpSpPojo implements IRewardOperation {

    private final long exp;
    private final int sp;

    @ConstructorProperties({"exp", "sp"})
    public ExpSpPojo(long exp, int sp) {
        this.exp = exp;
        this.sp = sp;
    }

    @Override
    public void executeOperation(L2PcInstance pc) {
        pc.addExpAndSp(exp, sp);
    }

    @Override
    public String getRewardIcon() {
        return "icon.etc_exp_point_i00";
    }

    @Override
    public Long getCount() {
        return exp;
    }

}
