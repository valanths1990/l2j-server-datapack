package com.l2jserver.datapack.custom.bounty.pojo;

import java.beans.ConstructorProperties;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import java.awt.Color;

@JsonTypeName("pvp")
public class PvpBounty extends Bounty {

    @ConstructorProperties({ "base", "multiplier", "maxbounty", "transfer", "color", "rewards" })
    public PvpBounty(double base, double multiplier, double maxBounty, double transfer, String color,
             List<RewardBounty> rewards) {
        this.base = base;
        this.multiplier = multiplier;
        this.maxBounty = maxBounty;
        this.transfer = transfer;
        this.color = Color.decode("#" + color);
        this.rewards = rewards;
    }

    public PvpBounty(PvpBounty o) {
        this.base = o.base;
        this.multiplier = o.multiplier;
        this.maxBounty = o.maxBounty;
        this.color = o.color;
        this.rewards = o.rewards;
        this.transfer = o.transfer;
    }

    @Override
    public String getType() {
        return "pvp";
    }

}
