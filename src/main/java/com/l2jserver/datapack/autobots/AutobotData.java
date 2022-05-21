package com.l2jserver.datapack.autobots;

import com.l2jserver.datapack.autobots.config.*;
import com.l2jserver.datapack.autobots.models.AutobotLocation;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.type.CrystalType;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.l2jserver.datapack.autobots.utils.Util.mapper;

public class AutobotData {

    private List<AutobotEquipment> equipment;
    private Map<ClassId, Map<CrystalType, AutobotEquipmentTest>> testEquipmentMap;
    private Map<ClassId, List<SkillHolder>> buffs;
    private List<AutobotSymbol> symbols;
    private List<AutobotLocation> teleportLocations;
    private AutobotSettings settings;
    private Map<CrystalType, List<AutobotJewels>> jewels;

    private AutobotData() {
        load();
    }

    public void load() {
        try {
            List<AutobotJewels> tmp = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/jewelset.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotJewels.class));
            jewels = tmp.stream().collect(Collectors.groupingBy(AutobotJewels::getGrade));
            List<AutobotEquipmentTest> testEquipment = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/armorset.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotEquipmentTest.class));
            testEquipment.remove(0); //remove root element // need to be fixed
            testEquipmentMap = testEquipment.stream().collect(Collectors.groupingBy(keyOne -> keyOne.classId, Collectors.toMap(keyTwo -> keyTwo.grade, v -> v, (k1, k2) -> k1)));
            settings = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/config.xml"), AutobotSettings.class);
            equipment = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/equipment.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotEquipment.class));
            List<AutobotBuffs> autobotBuffs = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/buffs.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotBuffs.class));
            buffs = autobotBuffs.stream().collect(Collectors.toMap(AutobotBuffs::getClassId, AutobotBuffs::getBuffsContent));
            teleportLocations = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/teleports.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotLocation.class));
            symbols = mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/symbols.xml"), mapper.getTypeFactory().constructCollectionType(List.class, AutobotSymbol.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<AutobotEquipment> getEquipment() {
        return equipment;
    }

    public Map<ClassId, List<SkillHolder>> getBuffs() {
        return buffs;
    }

    public List<AutobotSymbol> getSymbols() {
        return symbols;
    }

    public List<AutobotLocation> getTeleportLocations() {
        return teleportLocations;
    }

    public AutobotSettings getSettings() {
        return settings;
    }

    public Map<ClassId, Map<CrystalType, AutobotEquipmentTest>> getTestEquipmentMap() {
        return testEquipmentMap;
    }

    public Map<CrystalType, List<AutobotJewels>> getJewels() {
        return jewels;
    }

    public static AutobotData getInstance() {
        return AutobotData.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotData INSTANCE = new AutobotData();
    }
}
