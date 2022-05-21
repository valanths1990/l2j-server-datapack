package com.l2jserver.datapack.autobots.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.base.ClassId;

public class AutobotEquipment {
    @JsonProperty("classid")
    public ClassId classId;
    @JsonProperty("minLevel")
    public int minLevel;
    @JsonProperty("maxLevel")
    public int maxLevel;
    @JsonProperty("rhand")
    public int rightHand;
    @JsonProperty("lhand")
    public int leftHand;
    @JsonProperty("head")
    public int head;
    @JsonProperty("chest")
    public int chest;
    @JsonProperty("legs")
    public int legs;
    @JsonProperty("hands")
    public int hands;
    @JsonProperty("feet")
    public int feet;
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
}
