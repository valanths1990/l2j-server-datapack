package com.l2jserver.datapack.autobots.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.l2jserver.datapack.autobots.behaviors.preferences.AttackPlayerType;
import com.l2jserver.datapack.autobots.behaviors.preferences.TargetingPreference;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.config.Configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AutobotSettings {
    @JsonProperty("thinkIteration")
    public long iterationDelay;
    @JsonProperty("defaultTitle")
    public String defaultTitle;
    @JsonProperty("defaultTargetingRange")
    public int targetingRange;
    @JsonProperty("defaultAttackPlayerType")
    public AttackPlayerType attackPlayerType;
    @JsonProperty("defaultTargetingPreference")
    public TargetingPreference targetingPreference;
    @JsonProperty("useManaPots")
    public boolean useManaPots;
    @JsonProperty("useQuickHealingPots")
    public boolean useQuickHealingPots = false;
    @JsonProperty("useGreaterHealingPots")
    public boolean useGreaterHealingPots = true;
    @JsonProperty("useGreaterCpPots")
    public boolean useGreaterCpPots = true;

    public AutobotSettings() {
    }

    public void save() {
        try (FileWriter writer = new FileWriter(Configuration.server().getDatapackRoot() + "/data/autobots/config.xml", StandardCharsets.UTF_8)) {
            String content = Util.mapper.writeValueAsString(this);
            String header = "<?xml version='1.0' encoding='utf-8'?>\r\n";
            writer.write(header + content);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
