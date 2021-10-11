package com.l2jserver.datapack.custom.bounty.pojo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.awt.Color;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = PvpBounty.class, name = "pvp"),
        @JsonSubTypes.Type(value = PkBounty.class, name = "pk"),
        @JsonSubTypes.Type(value = AssistBounty.class, name = "assist") })
public abstract class Bounty {
    protected double base;
    protected double multiplier;
    protected double maxBounty;
    protected double transfer;
    protected Color color;
    protected double currentBounty;
    protected List<RewardBounty> rewards;

    public void increaseBounty() {
        currentBounty += base * multiplier;
        if (currentBounty >= maxBounty) {
            currentBounty = maxBounty;
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
//     public Color getColor() {
//
////     float[] hsb = new float[3];
////     float base = Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(),
////     baseColor.getBlue(), hsb)[0];
////
////     float end = Color.RGBtoHSB(endColor.getRed(), endColor.getGreen(),
////     endColor.getBlue(), hsb)[0];
////
////     float difference = (end - base);
////     float steps = (float) ((double) difference / maxBounty);
////     return Color.getHSBColor((float) ((steps * getCurrentBounty()) + base), 1,
////     1);
//
//     }

    public void increaseBounty(double currentBounty) {
        this.currentBounty += currentBounty;
        increaseBounty();
    }

    public void setBounty(double currentBounty) {
        this.currentBounty = currentBounty;
    }

    public double getTransfer() {
        return transfer;
    }

    public void setTransfer(double transfer) {
        this.transfer = transfer;
    }

    public double getCurrentBounty() {
        return currentBounty;
    }

    public List<RewardBounty> getRewards() {
        return rewards;
    }

    public void setRewards(List<RewardBounty> rewards) {
        this.rewards = rewards;
    }

    public double getBase() {
        return base;
    }

    public void setBase(double base) {
        this.base = base;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMaxBounty() {
        return maxBounty;
    }

    public void setMaxBounty(double maxBounty) {
        this.maxBounty = maxBounty;
    }

    public abstract String getType();
}
