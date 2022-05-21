package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Collections;
import java.util.List;

public class ArcherCombatPreferences implements CombatPreferences {
    @JsonProperty("kiteRadius")
    private int kiteRadius = 500;
    @JsonProperty("isKiting")
    private boolean isKiting = true;
    @JsonProperty("kitingDelay")
    private long kitingDelay = 700;
    @JsonProperty("targetingRadius")
    private int targetingRadius = AutobotData.getInstance().getSettings().targetingRange;
    @JsonProperty("attackPlayerType")
    private AttackPlayerType attackPlayerType = AutobotData.getInstance().getSettings().attackPlayerType;
    @JsonProperty("buffs")
    private List<SkillHolder> buffs = Collections.emptyList();
    @JsonProperty("targetingPreference")
    private TargetingPreference targetingPreference = AutobotData.getInstance().getSettings().targetingPreference;
    @JsonProperty("useManaPots")
    private boolean useManaPots = AutobotData.getInstance().getSettings().useManaPots;
    @JsonProperty("useQuickHealingPots")
    private boolean useQuickHealingPots = AutobotData.getInstance().getSettings().useQuickHealingPots;
    @JsonProperty("useGreaterHealingPots")
    private boolean useGreaterHealingPots = AutobotData.getInstance().getSettings().useGreaterHealingPots;
    @JsonProperty("useGreaterCpPots")
    private boolean useGreaterCpPots = AutobotData.getInstance().getSettings().useGreaterCpPots;

    public ArcherCombatPreferences(@JsonProperty("kiteRadius") int kiteRadius,
                                   @JsonProperty("isKiting") boolean isKiting,
                                   @JsonProperty("kitingDelay") long kitingDelay,
                                   @JsonProperty("targetingRadius") int targetingRadius,
                                   @JsonProperty("attackPlayerType") AttackPlayerType attackPlayerType,
                                   @JsonProperty("buffs") List<SkillHolder> buffs,
                                   @JsonProperty("targetingPreference") TargetingPreference targetingPreference,
                                   @JsonProperty("useManaPots") boolean useManaPots,
                                   @JsonProperty("useQuickHealingPots") boolean useQuickHealingPots,
                                   @JsonProperty("useGreaterHealingPots") boolean useGreaterHealingPots,
                                   @JsonProperty("useGreaterCpPots") boolean useGreaterCpPots) {
        this.kiteRadius = kiteRadius;
        this.isKiting = isKiting;
        this.kitingDelay = kitingDelay;
        this.targetingRadius = targetingRadius;
        this.attackPlayerType = attackPlayerType;
        this.buffs = buffs;
        this.targetingPreference = targetingPreference;
        this.useManaPots = useManaPots;
        this.useQuickHealingPots = useQuickHealingPots;
        this.useGreaterHealingPots = useGreaterHealingPots;
        this.useGreaterCpPots = useGreaterCpPots;
    }

    @Override
    public void setUseQuickHealingPots(boolean useQuickHealingPots) {
        this.useQuickHealingPots = useQuickHealingPots;
    }

    @Override
    public void setUseGreaterHealingPots(boolean useGreaterHealingPots) {
        this.useGreaterHealingPots = useGreaterHealingPots;
    }

    @Override
    public void setUseGreaterCpPots(boolean useGreaterCpPots) {
        this.useGreaterCpPots = useGreaterCpPots;
    }

    @Override
    public void setUseManaPots(boolean useManaPots) {
        this.useManaPots = useManaPots;
    }

    @Override
    public int getTargetingRadius() {
        return targetingRadius;
    }

    @Override
    public void setTargetingRadius(int targetingRadius) {
        this.targetingRadius = targetingRadius;
    }

    @Override
    public AttackPlayerType getAttackPlayerType() {
        return attackPlayerType;
    }

    @Override
    public void setAttackPlayerType(AttackPlayerType attackPlayerType) {
        this.attackPlayerType = attackPlayerType;
    }

    @Override
    public List<SkillHolder> getBuffs() {
        return buffs;
    }

    @Override
    public TargetingPreference getTargetingPreference() {
        return targetingPreference;
    }

    @Override
    public void setTargetingPreferences(TargetingPreference targetingPreference) {
        this.targetingPreference = targetingPreference;
    }

    @Override
    public boolean getUseManaPots() {
        return useManaPots;
    }

    @Override
    public boolean getUseQuickHealingPots() {
        return useQuickHealingPots;
    }

    @Override
    public boolean getUseGreaterHealingPots() {
        return useGreaterHealingPots;
    }

    @Override
    public boolean getUseGreaterCpPots() {
        return useGreaterCpPots;
    }

    public int getKiteRadius() {
        return kiteRadius;
    }

    public void setKiteRadius(int kiteRadius) {
        this.kiteRadius = kiteRadius;
    }

    public boolean isKiting() {
        return isKiting;
    }

    public void setKiting(boolean kiting) {
        this.isKiting = kiting;
    }

    public long getKitingDelay() {
        return kitingDelay;
    }
}
