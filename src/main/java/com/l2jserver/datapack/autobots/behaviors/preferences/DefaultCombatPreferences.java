package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.ArrayList;
import java.util.List;

public class DefaultCombatPreferences implements CombatPreferences {
    private int targetingRadius = AutobotData.getInstance().getSettings().targetingRange;
    private AttackPlayerType attackPlayerType = AutobotData.getInstance().getSettings().attackPlayerType;
    private List<SkillHolder> buffs = new ArrayList<>();
    private TargetingPreference targetingPreference = AutobotData.getInstance().getSettings().targetingPreference;
    private boolean useManaPots = AutobotData.getInstance().getSettings().useManaPots;
    private boolean useQuickHealingPots = AutobotData.getInstance().getSettings().useQuickHealingPots;
    private boolean useGreaterHealingPots = AutobotData.getInstance().getSettings().useGreaterHealingPots;
    private boolean useGreaterCpPots = AutobotData.getInstance().getSettings().useGreaterCpPots;

    public DefaultCombatPreferences(@JsonProperty("targetingRadius") int targetingRadius,
                                    @JsonProperty("attackPlayerType") AttackPlayerType attackPlayerType,
                                    @JsonProperty("buffs") List<SkillHolder> buffs,
                                    @JsonProperty("targetingPreference") TargetingPreference targetingPreference,
                                    @JsonProperty("useManaPots") boolean useManaPots,
                                    @JsonProperty("useQuickHealingPots") boolean useQuickHealingPots,
                                    @JsonProperty("useGreaterHealingPots") boolean useGreaterHealingPots,
                                    @JsonProperty("useGreaterCpPots") boolean useGreaterHealingCpPots) {
        this.targetingRadius = targetingRadius;
        this.attackPlayerType = attackPlayerType;
        this.buffs = buffs;
        this.targetingPreference = targetingPreference;
        this.useManaPots = useManaPots;
        this.useQuickHealingPots = useQuickHealingPots;
        this.useGreaterCpPots = useGreaterHealingCpPots;
        this.useGreaterHealingPots = useGreaterHealingPots;

    }

    public DefaultCombatPreferences() {

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
}
