package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class EvasTemplarSkillPreferences extends SkillPreferences {

    public EvasTemplarSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(arrest, StatusCondition.Distance, ComparisonCondition.MoreOrEqualThan, ConditionValueType.Amount, TargetCondition.PlayerTarget, 300),
                new SkillUsageCondition(ultimateDefense, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 50),
                new SkillUsageCondition(touchOfLife, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 30),
                new SkillUsageCondition(magicalMirror, StatusCondition.Hp, ComparisonCondition.LessOrEqualThan, ConditionValueType.Percentage, TargetCondition.My, 40)
        );
        this.togglableSkills = Map.of(
                summonStormCubic, true, summonAttractiveCubic, true, deflectArrow, false, guardStance, true, holyArmor, false, shieldFortress, false, fortitude, false
        );
    }
}
