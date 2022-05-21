package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class SagittariusSkillPreferences extends SkillPreferences {


    public SagittariusSkillPreferences(@JsonProperty("isSkillsOnly") boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(dash, StatusCondition.Distance, ComparisonCondition.MoreOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 1000),
                new SkillUsageCondition(ultimateEvasion, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 60)
        );
        this.togglableSkills = Map.of(
                hawkEye, false, viciousStance, true, accuracy, true
        );
    }

}
