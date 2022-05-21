package com.l2jserver.datapack.autobots.models;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.AutobotNameService;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.base.ClassId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CreateBotDetails {
    private String name = AutobotNameService.getInstance().getRandomAvailableName();
    private int level = 85;
    private Race race = Race.HUMAN;
    private String classType = "Fighter";
    private Sex gender = Sex.MALE;
    private String hairStyle = "Type A";
    private String hairColor = "Type A";
    private String face = "Type A";
    private List<ClassId> classIds = AutobotHelpers.getSupportedClassesForLevel(level).stream().filter(c -> c.getRace() == race).collect(Collectors.toList());
    private ClassId classId = classIds.get(Rnd.get(classIds.size()));
    private int weaponEnchant = 0;
    private int armorEnchant = 0;
    private int jewelEnchant = 0;

    public CreateBotDetails() {

    }

    public PcAppearance getAppearance() {
        return new PcAppearance(textToFace(face), textTohairColor(hairColor), textToHairStyle(hairStyle), gender == Sex.FEMALE);
    }

    public CreateBotDetails(String name, int level, Race race, String classType, Sex gender,
                            String hairStyle, String hairColor, String face, ClassId classId,
                            int weaponEnchant, int armorEnchant, int jewelEnchant) {
        this.name = name;
        this.level = level;
        this.race = race;
        this.classType = classType;
        this.gender = gender;
        this.hairStyle = hairStyle;
        this.hairColor = hairColor;
        this.face = face;
        this.classId = classId;
        this.weaponEnchant = weaponEnchant;
        this.armorEnchant = armorEnchant;
        this.jewelEnchant = jewelEnchant;
    }


    public void randomize() {
        name = AutobotNameService.getInstance().getRandomAvailableName();
        level = Rnd.get(1, 85);
        List<String> racesForDropdown = racesForDropdown();
        race = Race.valueOf(racesForDropdown.get(Rnd.get(racesForDropdown.size())).toUpperCase());

        classType = race.name().equals("DWARF") ? "Fighter" : List.of("Fighter", "Mystic").get(Rnd.get(2));
        gender = Sex.valueOf(List.of("MALE", "FEMALE").get(Rnd.get(2)));
        List<String> tempList = List.of("Type A", "Type B", "Type C", "Type D", "Type E");
        List<String> secondTempList = List.of("Type A", "Type B", "Type C", "Type D", "Type E", "Type F", "Type G");

        hairStyle = gender.name().equals("MALE") ? tempList.get(Rnd.get(tempList.size())) : secondTempList.get(Rnd.get(secondTempList.size()));

        tempList = List.of("Type A", "Type B", "Type C", "Type D", "Type E");

        hairColor = tempList.get(Rnd.get(tempList.size()));
        tempList = List.of("Type A", "Type B", "Type C", "Type D");
        face = tempList.get(Rnd.get(tempList.size()));
        classIds = AutobotHelpers.getSupportedClassesForLevel(level).stream().filter(c -> c.getRace() == race).collect(Collectors.toList());

        classId = classIds.get(Rnd.get(classIds.size()));
    }

    public static byte textTohairColor(String text) {
        return switch (text) {
            case "Type A" -> (byte) 0;
            case "Type B" -> (byte) 1;
            case "Type C" -> (byte) 2;
            case "Type D" -> (byte) 3;
            case "Type E" -> (byte) 4;
            default -> (byte) 0;
        };
    }

    public static byte textToHairStyle(String text) {
        return switch (text) {
            case "Type A" -> (byte) 0;
            case "Type B" -> (byte) 1;
            case "Type C" -> (byte) 2;
            case "Type D" -> (byte) 3;
            case "Type E" -> (byte) 4;
            case "Type F" -> (byte) 5;
            case "Type G" -> (byte) 6;
            default -> (byte) 0;
        };
    }

    public static byte textToFace(String text) {
        return switch (text) {
            case "Type A" -> (byte) 0;
            case "Type B" -> (byte) 1;
            case "Type C" -> (byte) 2;
            case "Type D" -> (byte) 3;
            default -> (byte) 0;
        };
    }

    public static List<String> racesForDropdown() {
        return new ArrayList<>(Arrays.asList("Human", "Elf", "Dark_elf", "Orc", "Dwarf"));

    }

    public static List<Sex> gendersForDropdown() {
        return new ArrayList<>(Arrays.asList(Sex.MALE, Sex.FEMALE));
    }

    public static List<String> facesForDropdown() {
        return new ArrayList<>(Arrays.asList("Type A", "Type B", "Type C"));
    }

    public static List<String> hairColorForDropdown() {
        return new ArrayList<>(Arrays.asList("Type A", "Type B", "Type C", "Type D"));
    }

    public static List<String> hairstyleForDropdown() {
        return new ArrayList<>(Arrays.asList("Type A", "Type B", "Type C", "Type D", "Type E", "Type F", "Type G"));
    }

    public static List<String> classesForDropdown(Race race) {
        return switch (race) {
            case HUMAN, ELF, DARK_ELF, ORC -> new ArrayList<>(Arrays.asList("Fighter", "Mystic"));
            default -> new ArrayList<>(Arrays.asList("Fighter"));
        };
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = Race.valueOf(race);
    }

    public String getClassType() {
        return classType;
    }

    public Sex getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = Sex.valueOf(gender);
    }

    public String getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(String hairStyle) {
        this.hairStyle = hairStyle;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public ClassId getClassId() {
        return classId;
    }

    public void setClassId(String className) {
        classId = ClassId.valueOf(className);
    }

    public int getWeaponEnchant() {
        return weaponEnchant;
    }

    public void setWeaponEnchant(int weaponEnchant) {
        this.weaponEnchant = weaponEnchant;
    }

    public int getArmorEnchant() {
        return armorEnchant;
    }

    public void setArmorEnchant(int armorEnchant) {
        this.armorEnchant = armorEnchant;
    }

    public int getJewelEnchant() {
        return jewelEnchant;
    }

    public void setJewelEnchant(int jewelEnchant) {
        this.jewelEnchant = jewelEnchant;
    }

    @Override
    public String toString() {
        return "CreateBotDetails{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", race=" + race +
                ", classType='" + classType + '\'' +
                ", gender=" + gender +
                ", hairStyle='" + hairStyle + '\'' +
                ", hairColor='" + hairColor + '\'' +
                ", face='" + face + '\'' +
                ", classId=" + classId +
                ", weaponEnchant=" + weaponEnchant +
                ", armorEnchant=" + armorEnchant +
                ", jewelEnchant=" + jewelEnchant +
                '}';
    }
}
