package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class AdventurerSkillPreferences extends SkillPreferences {


    public AdventurerSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillOnly) {
        super(isSkillOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(dash, StatusCondition.Distance, ComparisonCondition.MoreOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 300),
                new SkillUsageCondition(ultimateEvasion, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 60),
                new SkillUsageCondition(mirage, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 40)
        );
        togglableSkills = Map.of(
                focusSkillMastery, true, focusPower, true, trick, true, _switch, true, viciousStance, true);

    }
}
