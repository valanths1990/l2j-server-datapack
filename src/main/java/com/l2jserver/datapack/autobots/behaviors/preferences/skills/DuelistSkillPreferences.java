package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.battleroar;
import static com.l2jserver.datapack.autobots.utils.AutobotSkills.braveheart;

public class DuelistSkillPreferences extends SkillPreferences {
    private boolean useSkillsOnMobs;

    public DuelistSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.useSkillsOnMobs = false;
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(braveheart, StatusCondition.Cp, ComparisonCondition.MoreOrEqualThan, ConditionValueType.MissingAmount, TargetCondition.My, 1000),
                new SkillUsageCondition(battleroar, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 80)
        );
    }

    public boolean isUseSkillsOnMobs() {
        return useSkillsOnMobs;
    }

    public void setUseSkillsOnMobs(boolean useSkillsOnMobs) {
        this.useSkillsOnMobs = useSkillsOnMobs;
    }
}
