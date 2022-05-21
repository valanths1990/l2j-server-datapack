package com.l2jserver.datapack.autobots.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IconsTable {
    private Map<Integer, String> icons;

    public IconsTable() {

        try {

            List<L2Icon> temp = Util.mapper.readValue(new File(Configuration.server().getDatapackRoot() + "/data/autobots/icons.xml"), Util.mapper.getTypeFactory().constructCollectionType(List.class, L2Icon.class));
            icons = temp.stream().collect(Collectors.toMap(L2Icon::getId, L2Icon::getValue,(k1,k2)->k1));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getSkillIcon(int skillId) {
        String skillIdAsText = switch (String.valueOf(skillId).length()) {
            case 1 -> "000" + skillId;
            case 2 -> "00" + skillId;
            case 3 -> "0" + skillId;
            default -> skillId + "";
        };
        return "Icon.skill" + skillIdAsText;
    }

    public String getItemIcon(int itemId) {
        return icons.getOrDefault(itemId, "");
    }

    public Map<Integer, String> getIcons() {
        return icons;
    }

    private static final class L2Icon {
        public @JsonProperty("Id")
        int id;
        public @JsonProperty("value")
        String value;

        public int getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }

    public static IconsTable getInstance() {
        return IconsTable.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final IconsTable INSTANCE = new IconsTable();
    }

}
