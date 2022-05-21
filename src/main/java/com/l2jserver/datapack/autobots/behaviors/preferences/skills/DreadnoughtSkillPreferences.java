package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class DreadnoughtSkillPreferences extends SkillPreferences {

    public DreadnoughtSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(braveheart, StatusCondition.Cp, ComparisonCondition.MoreOrEqualThan, ConditionValueType.MissingAmount, TargetCondition.My, 1000),
                new SkillUsageCondition(revival, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 5),
                new SkillUsageCondition(battleroar, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 80)
        );
        this.togglableSkills = Map.of(
                fellSwoop, false, viciousStance, true, warFrenzy, false, thrillFight, false);
    }
}
