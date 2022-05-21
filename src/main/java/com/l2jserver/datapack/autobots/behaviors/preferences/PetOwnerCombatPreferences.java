package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Collections;
import java.util.List;

public class PetOwnerCombatPreferences implements CombatPreferences {
    private boolean petAssists = true;
    private boolean summonPet = true;
    private boolean petUsesShots = false;
    private int petShotId = 0;
    private boolean petHasBuffs = true;
    private int targetingRadius = AutobotData.getInstance().getSettings().targetingRange;
    private AttackPlayerType attackPlayerType = AutobotData.getInstance().getSettings().attackPlayerType;
    private List<SkillHolder> buffs = Collections.emptyList();
    private TargetingPreference targetingPreference = AutobotData.getInstance().getSettings().targetingPreference;
    private boolean useManaPots = AutobotData.getInstance().getSettings().useManaPots;
    private boolean useQuickHealingPots = AutobotData.getInstance().getSettings().useQuickHealingPots;
    private boolean useGreaterHealingPots = AutobotData.getInstance().getSettings().useGreaterHealingPots;
    private boolean useGreaterCpPots = AutobotData.getInstance().getSettings().useGreaterCpPots;

    public PetOwnerCombatPreferences(@JsonProperty("petAssists") boolean petAssists,
                                     @JsonProperty("summonPet") boolean summonPet,
                                     @JsonProperty("petUsesShots") boolean petUsesShots,
                                     @JsonProperty("petShotId") int petShotId,
                                     @JsonProperty("petHasBuffs") boolean petHasBuffs,
                                     @JsonProperty("targetingRadius") int targetingRadius,
                                     @JsonProperty("attackPlayerType") AttackPlayerType attackPlayerType,
                                     @JsonProperty("buffs") List<SkillHolder> buffs,
                                     @JsonProperty("targetingPreference") TargetingPreference targetingPreference,
                                     @JsonProperty("useManaPots") boolean useManaPots,
                                     @JsonProperty("useQuickHealingPots") boolean useQuickHealingPots,
                                     @JsonProperty("useGreaterHealingPots") boolean useGreaterHealingPots,
                                     @JsonProperty("useGreaterCpPots") boolean useGreaterCpPots) {
        this.petAssists = petAssists;
        this.summonPet = summonPet;
        this.petUsesShots = petUsesShots;
        this.petShotId = petShotId;
        this.petHasBuffs = petHasBuffs;
        this.targetingRadius = targetingRadius;
        this.attackPlayerType = attackPlayerType;
        this.buffs = buffs;
        this.targetingPreference = targetingPreference;
        this.useManaPots = useManaPots;
        this.useQuickHealingPots = useQuickHealingPots;
        this.useGreaterHealingPots = useGreaterHealingPots;
        this.useGreaterCpPots = useGreaterCpPots;

    }

    public PetOwnerCombatPreferences() {

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

    public boolean isPetAssists() {
        return petAssists;
    }

    public boolean isSummonPet() {
        return summonPet;
    }

    public boolean isPetUsesShots() {
        return petUsesShots;
    }

    public int getPetShotId() {
        return petShotId;
    }

    public boolean isPetHasBuffs() {
        return petHasBuffs;
    }

}
