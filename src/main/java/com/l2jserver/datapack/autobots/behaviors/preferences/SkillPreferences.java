package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkillPreferences {
    @JsonProperty("isSkillsOnly")
    public Boolean isSkillsOnly;
    @JsonProperty("skillUsageConditions")
    public List<SkillUsageCondition> skillUsageConditions = Collections.emptyList();
    @JsonProperty("togglableSkills")
    public Map<Integer, Boolean> togglableSkills = Collections.emptyMap();

    public SkillPreferences() {

    }

    public SkillPreferences(@JsonProperty("isSkillsOnly") boolean isSkillsOnly) {
        this.isSkillsOnly = isSkillsOnly;
    }
}
