package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.List;

public interface CombatPreferences {
    int getTargetingRadius();

    void setTargetingRadius(int targetingRadius);

    AttackPlayerType getAttackPlayerType();

    void setAttackPlayerType(AttackPlayerType attackPlayerType);

    List<SkillHolder> getBuffs();

    TargetingPreference getTargetingPreference();

    void setTargetingPreferences(TargetingPreference targetingPreference);

    boolean getUseManaPots();

    boolean getUseQuickHealingPots();

    boolean getUseGreaterHealingPots();

    boolean getUseGreaterCpPots();

    void setUseQuickHealingPots(boolean useQuickHealingPots);
    void setUseGreaterHealingPots(boolean useGreaterHealingPots);
    void setUseGreaterCpPots(boolean useGreaterCpPots);
    void setUseManaPots(boolean useManaPots);
}
