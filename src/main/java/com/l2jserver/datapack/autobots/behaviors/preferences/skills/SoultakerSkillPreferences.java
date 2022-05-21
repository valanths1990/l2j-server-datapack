package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;

import java.util.List;
import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class SoultakerSkillPreferences extends SkillPreferences {

    public SoultakerSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.skillUsageConditions = List.of(
                new SkillUsageCondition(vampiricClaw, StatusCondition.Hp, ComparisonCondition.LessThan, ConditionValueType.Percentage, TargetCondition.My, 100)
        );
        this.togglableSkills = Map.of(
                curseGloom, true, curseOfAbyss, true, curseOfDoom, true, silence, true, anchor, true, transferPain, true, arcanePower, true
        );
    }
}
