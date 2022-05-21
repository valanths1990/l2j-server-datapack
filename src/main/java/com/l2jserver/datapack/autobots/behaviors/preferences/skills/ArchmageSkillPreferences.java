package com.l2jserver.datapack.autobots.behaviors.preferences.skills;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;

import java.util.Map;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class ArchmageSkillPreferences extends SkillPreferences {

    public ArchmageSkillPreferences(@JsonProperty("isSkillsOnly")boolean isSkillsOnly) {
        super(isSkillsOnly);
        this.togglableSkills = Map.of(
                surrenderToFire, true, cancellation, false, arcanePower, true
        );
    }

}
