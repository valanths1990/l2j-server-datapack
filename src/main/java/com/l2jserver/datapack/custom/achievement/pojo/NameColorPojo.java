package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.beans.ConstructorProperties;

public class NameColorPojo implements IRewardOperation {
    private final int color;

    @ConstructorProperties("color")
    public NameColorPojo(String color) {
        if (!color.contains("#")) {
            color = "#" + color;
        }
        this.color = Integer.decode(color);
    }

    public int getColor() {
        return color;
    }

    @Override
    public void executeOperation(L2PcInstance pc) {
        pc.getAppearance().setNameColor(color);
    }

    @Override
    public String getRewardIcon() {
        return "color_name_i00";
    }

    @Override
    public Long getCount() {
        return 1L;
    }
}
