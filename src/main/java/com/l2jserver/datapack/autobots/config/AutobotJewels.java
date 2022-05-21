package com.l2jserver.datapack.autobots.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.items.type.CrystalType;

public class AutobotJewels {
    @JsonProperty("grade")
    public CrystalType grade;
    @JsonProperty("neck")
    public int neck;
    @JsonProperty("lear")
    public int leftEar;
    @JsonProperty("rear")
    public int rightEar;
    @JsonProperty("lring")
    public int leftRing;
    @JsonProperty("rring")
    public int rightRing;
    @JsonProperty("isRaidboss")
    public boolean isRaidboss = false;
    @JsonProperty("isMage")
    public boolean isMage = false;
    public CrystalType getGrade(){
        return grade;
    }
}
