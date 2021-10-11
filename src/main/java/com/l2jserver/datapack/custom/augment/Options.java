package com.l2jserver.datapack.custom.augment;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Options {

    STR(23061, OptionsType.STAT,"Str +1"),
    INT(23063, OptionsType.STAT,"Int +1"),
    CON(23062, OptionsType.STAT,"Con +1"),
    MEN(23064, OptionsType.STAT,"Men +1"),
    PDEF(23065, OptionsType.STAT,"P. Def. increases by 24.52"),
    MDEF(23066, OptionsType.STAT,"M. Def. increases by 20.8"),
    MAXHP(23067, OptionsType.STAT,"Max HP increases by 90.29"),
    MAXMP(23068, OptionsType.STAT,"Max MP increases by 29.65"),
    MAXCP(23069, OptionsType.STAT,"Max CP increases by 162.52"),
    PATK(23070, OptionsType.STAT,"P. Atk. increases by 16.72"),
    MATK(23071, OptionsType.STAT,"M. Atk. increases by 21.03"),
    REGHP(23072, OptionsType.STAT,"HP Regen increases by 0.39"),
    REGMP(23073, OptionsType.STAT,"MP Regen increases by 0.17"),
    REGCP(23074, OptionsType.STAT,"CP Regen increases by 0.39"),
    REVAS(23075, OptionsType.STAT,"Evasion increases by 1.99"),
    ACCCOMBAT(23076, OptionsType.STAT,"Accuracy increases by 1.99"),
    CRITRATE(23077, OptionsType.STAT,"Critical increases by 14.96"),

    PASSIVE_PRAYER(23000, OptionsType.PASSIVE,""),
    PASSIVE_MIGHT(23003, OptionsType.PASSIVE,""),
    PASSIVE_EMPOWER(23001, OptionsType.PASSIVE,""),
    PASSIVE_DUELMIGHT(23005, OptionsType.PASSIVE,""),
    PASSIVE_SHIELD(23004, OptionsType.PASSIVE,""),
    PASSIVE_MAGICBARRIE(23002, OptionsType.PASSIVE,""),
    PASSIVE_HEALEMPOWE(24637, OptionsType.PASSIVE,""),
    PASSIVE_AGILITY(24690, OptionsType.PASSIVE,""),
    PASSIVE_GUIDANCE(21417, OptionsType.PASSIVE,""),
    PASSIVE_FOCUS(21415, OptionsType.PASSIVE,""),
    PASSIVE_WILDMAGIC(21418, OptionsType.PASSIVE,""),
    PASSIVE_SKILLCLARITY(16338, OptionsType.PASSIVE,""),
    PASSIVE_SPELLCLARITY(21422, OptionsType.PASSIVE,""),
    PASSIVE_MUSICCLARITY(21421, OptionsType.PASSIVE,""),
    PASSIVE_CLARITY(21413, OptionsType.PASSIVE,""),
    PASSIVE_REFLECTDAMAGE(21416, OptionsType.PASSIVE,""),

    ACTIVE_BATTLEROAR(21280, OptionsType.ACTIVE,""),
    ACTIVE_PRAYER(22914, OptionsType.ACTIVE,""),
    ACTIVE_MIGHT(22926, OptionsType.ACTIVE,""),
    ACTIVE_EMPOWER(22916, OptionsType.ACTIVE,""),
    ACTIVE_DUELMIGHT(16211, OptionsType.ACTIVE,""),
    ACTIVE_SHIELD(16208, OptionsType.ACTIVE,""),
    ACTIVE_MAGICBARRIER(16201, OptionsType.ACTIVE,""),
    ACTIVE_DUELWEAKNESS(16210, OptionsType.ACTIVE,""),
    ACTIVE_HEALEMPOWER(21275, OptionsType.ACTIVE,""),
    ACTIVE_AGILITY(22901, OptionsType.ACTIVE,""),
    ACTIVE_GUIDANCE(24548, OptionsType.ACTIVE,""),
    ACTIVE_FOCUS(16289, OptionsType.ACTIVE,""),
    ACTIVE_WILDMAGIC(23014, OptionsType.ACTIVE,""),
    ACTIVE_VAMPIRICRAGE(23024, OptionsType.ACTIVE,""),
    ACTIVE_CLARITY(24646, OptionsType.ACTIVE,""),
    ACTIVE_REFRESH(24645, OptionsType.ACTIVE,""),
    ACTIVE_REFLECTDAMAGE(23010, OptionsType.ACTIVE,"");

    private final int id;
    private final OptionsType type;
    private final String descr;

    Options(int id, OptionsType type, String descr) {
        this.id = id;
        this.type = type;
        this.descr = descr;
    }

    public int getId() {
        return this.id;
    }

    public OptionsType getType() {
        return this.type;
    }
    public String getDescr()
    {
        return this.descr;
    }
    public static List<Options> getOptoinByType(OptionsType type) {
        return Arrays.stream(values()).filter(o -> o.getType() == type).collect(Collectors.toList());
    }
    public static OptionsType getTypeById(int id){
        return Arrays.stream(values()).filter(o->o.getId() == id).map(Options::getType).findFirst().orElse(null);
    }
    public static Options getOptionById(int id){
        return Arrays.stream(values()).filter(o->o.getId() == id).findFirst().orElse(null);
    }
}



