package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class SwordMuseSkillPreferences extends SkillPreferences {

    public SwordMuseSkillPreferences(@JsonProperty("isSkillsOnly") boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(arrest, StatusCondition.Distance, ComparisonCondition.MoreOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 300),
                new SkillUsageCondition(ultimateDefense, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 50),
                new SkillUsageCondition(songOfSilence, StatusCondition.Distance, ComparisonCondition.LessOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 100)
        );
        this.togglableSkills = Map.of(
                deflectArrow, false, holyBlade, true
        );
    }
}
