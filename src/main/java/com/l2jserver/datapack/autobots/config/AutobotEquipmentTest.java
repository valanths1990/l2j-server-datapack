package com.l2jserver.datapack.autobots.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.WeaponType;


public class AutobotEquipmentTest {
    @JsonProperty("classId")
    public ClassId classId;
    @JsonProperty("grade")
    public CrystalType grade;
    @JsonProperty("setId")
    public int setId;
    @JsonProperty("weaponType")
    public WeaponType weaponType;
    @JsonProperty("isMagicWeapon")
    public boolean isMagicWeapon = false;
    @JsonProperty("isTwoHanded")
    public boolean isTwoHanded = false;
    @JsonProperty("useShield")
    public boolean useShield = false;
    @JsonProperty("shield")
    public ArmorType shield = ArmorType.SHIELD;
    @JsonProperty("giveAccessories")
    public boolean giveAccessories = true;
    @JsonProperty("raidBossAccessories")
    public boolean raidBossAccessories = false;
    @JsonProperty("SA")
    public String sa = "";

}
