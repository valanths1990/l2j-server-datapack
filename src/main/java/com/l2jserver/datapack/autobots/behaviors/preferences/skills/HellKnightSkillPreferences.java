package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class HellKnightSkillPreferences extends SkillPreferences {

    public HellKnightSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(shackle, StatusCondition.Distance, ComparisonCondition.MoreOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 300),
                new SkillUsageCondition(ultimateDefense, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 40),
                new SkillUsageCondition(touchOfDeath, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.Target, 75),
                new SkillUsageCondition(shieldOfRevenge, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 50)
        );
        this.togglableSkills =
                Map.of(
                        deflectArrow, false, shieldFortress, false, fortitude, false
                );
    }
}
