/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.custom.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.ZoneId;

/**
 * @author Silvar
 */
public class BufferHandler implements IBypassHandler {
    private final String[] COMMANDS = {"buffer;general", "buffer;dance", "buffer;song", "buffer;simple", "buffer;resist", "buffer;homepage", "buffer;specific"};
    private final String schemeButtonHtml = "<tr><td><button value=\"pet\" action=\"bypass buffer;homepage pet %name%\" width=30 height=30 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"%schemename%\" action=\"bypass buffer;homepage scheme %name%\" width=100 height=30back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"X\" action=\"bypass buffer;homepage delete %schemename%\" width=30 height=30back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>";
    private final SchemeBufferTable schemeTable = SchemeBufferTable.getInstance();
    private final int[] warriorBuffs = {
            1035, 1499, 1500, 1501, 1502, 1503, 1504, 1519, 1062, 1078, 1085, 1259, 1352, 1353, 1354
            , 1363, 1388, 1397, 1461, 1542, 1364, 1307, 1232, 982, 1416
            , 4699, 271, 274, 275, 276, 310, 915, 264, 267, 268, 269, 304, 349, 364, 764, 914, 305
    };
    private final int[] mageBuffs = {
            1500, 1501, 1503, 1504, 1062, 1078, 1085, 1303, 1352
            , 1353, 1354, 1357, 1389, 13971, 1461, 1542
            , 1364, 1307, 1232, 1416, 4703, 273, 276, 365, 915
            , 264, 267, 268, 304, 305, 349, 363, 764, 914, 830
    };

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
        String[] tmp = command.split(" ");
        String currentPage = tmp[0].split(";")[1];
        if (tmp.length == 1) {
            openBuffBoard(player, currentPage);
            return true;
        }

        switch (tmp[1]) {
            case "save" -> {
                if (tmp.length == 3) {
                    saveScheme(player, tmp[2]);
                }
            }
            case "delete" -> deleteScheme(player, tmp[2]);
            case "scheme" -> schemeBuff(player, tmp[2]);
            case "pet"->petBuff(player,tmp[2]);
            case "heal" -> {
                if (!isAllowed(player)) {
                    return false;
                }
                player.setCurrentHp(player.getMaxHp());
                player.setCurrentMp(player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
            }
            case "cancel" -> player.getEffectList().stopAllEffects();
            case "autobuff" -> {
                if (!isAllowed(player)) {
                    return false;
                }
                autoBuff(player);
            }
            case "general", "simple", "specific", "resist", "dance", "song" -> {
                openBuffBoard(player, tmp[1]);
                return true;
            }
            default -> {
                if (!isAllowed(player)) {
                    return false;
                }
                buffPlayer(player, tmp[1]);
            }
        }
        openBuffBoard(player, currentPage);
        return true;
    }

    public void saveScheme(L2PcInstance player, String schemeName) {
        if (schemeName.length() <= 2) {
            return;
        }
        List<Integer> buffsIds = new ArrayList<>();
        player.getEffectList().getBuffs().stream().filter(b -> schemeTable.getAvailableBuff(b.getSkill().getId()) != null).forEach(b -> buffsIds.add(b.getSkill().getId()));
        player.getEffectList().getDances().stream().filter(b -> schemeTable.getAvailableBuff(b.getSkill().getId()) != null).forEach(b -> buffsIds.add(b.getSkill().getId()));
//		player.getEffectList().getBuffs().stream().filter(b -> {
//			buffsIds.add(b.getSkill().getId());
//		});
//		player.getEffectList().getDances().forEach(b -> {
//			buffsIds.add(b.getSkill().getId());
//		});
        schemeTable.setScheme(player.getObjectId(), schemeName.trim().replaceAll(" ", ""), buffsIds);
        schemeTable.saveSchemes();
    }

    public void deleteScheme(L2PcInstance player, String schemeName) {
        schemeTable.deleteScheme(player.getObjectId(), schemeName);
    }

    public void openBuffBoard(L2PcInstance player, String board) {

        if (board.equals("homepage")) {
            Map<String, List<Integer>> playerSchemes = null;
            if ((playerSchemes = schemeTable.getPlayerSchemes(player.getObjectId())) != null) {
                StringBuilder html = new StringBuilder(HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/buffer/homepage.html"));
                for (String scheme : playerSchemes.keySet()) {
                    String tmp = schemeButtonHtml.replaceAll("%schemename%", scheme);
                    tmp = tmp.replaceAll("%name%", scheme);

                    // doc.children().select("#schemes tbody").append(tmp);
                    html.insert(html.indexOf("schemes") + 9, tmp);
                }
                int buffsAmount = player.getEffectList().getBuffCount();
                int songDanceAmount = player.getEffectList().getDanceCount();
                String tmp = html.toString();
                tmp = tmp.replace("%buffs%", String.valueOf(buffsAmount));
                tmp = tmp.replace("%sd%", String.valueOf(songDanceAmount));
                CommunityBoardHandler.separateAndSend(tmp, player);
                return;
            }
        }

        int buffsAmount = player.getEffectList().getBuffCount();
        int songDanceAmount = player.getEffectList().getDanceCount();
        String tmp = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/buffer/" + board + ".html");
        tmp = tmp.replace("%buffs%", String.valueOf(buffsAmount));
        tmp = tmp.replace("%sd%", String.valueOf(songDanceAmount));
        CommunityBoardHandler.separateAndSend(tmp, player);
    }

    public void autoBuff(L2PcInstance player) {
        Skill skill;
        if (player.getClassId().isMage()) {
            for (int i : mageBuffs) {
                skill = SkillData.getInstance().getSkill(i, schemeTable.getAvailableBuff(i).getLevel());
                if (skill != null) {
                    skill.applyEffects(player, player, true, 10800);
                }
            }
        } else {

            for (int i : warriorBuffs) {
                skill = SkillData.getInstance().getSkill(i, schemeTable.getAvailableBuff(i).getLevel());
                if (skill != null) {
                    skill.applyEffects(player, player, true, 10800);
                }
            }

            if (player.getActiveWeaponItem() == null) {
                skill = SkillData.getInstance().getSkill(825, schemeTable.getAvailableBuff(826).getLevel());
                if (skill != null) {
                    skill.applyEffects(player, player, true, 10800);
                }
            } else {
                skill = switch (player.getActiveWeaponItem().getItemType()) {
                    case BOW -> SkillData.getInstance().getSkill(26074, schemeTable.getAvailableBuff(26074).getLevel());
                    case FIST -> SkillData.getInstance().getSkill(826, schemeTable.getAvailableBuff(826).getLevel());
                    default -> SkillData.getInstance().getSkill(825, schemeTable.getAvailableBuff(825).getLevel());
                };
            }
            if (skill != null) {
                skill.applyEffects(player, player, true, 10800);
            }
            if (player.getActiveChestArmorItem() == null) {
                if (player.getClassId().isMage()) {
                    skill = SkillData.getInstance().getSkill(830, schemeTable.getAvailableBuff(830).getLevel());
                } else {
                    skill = SkillData.getInstance().getSkill(829, schemeTable.getAvailableBuff(829).getLevel());
                }
            } else {
                switch (player.getActiveChestArmorItem().getItemType()) {
                    case HEAVY -> skill = SkillData.getInstance().getSkill(828, schemeTable.getAvailableBuff(828).getLevel());
                    case LIGHT -> skill = SkillData.getInstance().getSkill(829, schemeTable.getAvailableBuff(829).getLevel());
                    case MAGIC -> skill = SkillData.getInstance().getSkill(830, schemeTable.getAvailableBuff(830).getLevel());
                    default -> {
                    }
                }
            }
            if (skill != null) {
                skill.applyEffects(player, player, true, 10800);
            }

        }
    }

    public void buffPlayer(L2PcInstance player, String buff) {
        if (!isAllowed(player)) {
            return;
        }
        try {
            int buffId = Integer.parseInt(buff);
            BuffSkillHolder buffskillholder = schemeTable.getAvailableBuff(buffId);
            if (buffskillholder == null) {
                return;
            }
            Skill skill = SkillData.getInstance().getSkill(buffskillholder.getId(), buffskillholder.getLevel());
            if (skill != null) {
                skill.applyEffects(player, player, true, 10800);
                player.reduceAdena("Buffer", buffskillholder.getPrice(), null, false);
//                if (player.getSummon() != null) {
//                    skill.applyEffects(player.getSummon(), player.getSummon(), true, 10800);
//                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }
    private void petBuff(L2PcInstance player,String schemeName){
        if(!player.hasSummon() || player.getSummon()==null){
            return;
        }
        player.getSummon().stopAllEffects();
        List<Integer> buffs = schemeTable.getScheme(player.getObjectId(), schemeName);
        buffs.forEach(i -> {
            Skill skill = SkillData.getInstance().getSkill(i, schemeTable.getAvailableBuff(i).getLevel());
            if (skill != null) {
                skill.applyEffects(player.getSummon(), player.getSummon(), true, 10800);
            }
        });
    }
    private void schemeBuff(L2PcInstance player, String schemename) {
        List<Integer> buffs = schemeTable.getScheme(player.getObjectId(), schemename);
        buffs.forEach(i -> {
            Skill skill = SkillData.getInstance().getSkill(i, schemeTable.getAvailableBuff(i).getLevel());
            if (skill != null) {
                skill.applyEffects(player, player, true, 10800);
            }
        });
    }

    public boolean isAllowed(L2PcInstance player) {

        if (player.isInsideZone(ZoneId.PEACE)) {
            return true;
        }
        if(!(player.isAttackingNow() || player.isInCombat()) ){
            return player.getPvpFlag() <= 0 && !player.isInsideZone(ZoneId.SIEGE);
        }
//        else if (player.isInsideZone(ZoneId.PVP) && !(player.isInCombat() || player.isAttackingNow())) {
//            return true;
//        } else if (player.isInsideZone(ZoneId.PVP) && !(player.isInCombat() || player.isAttackingNow())) {
//            return true;
//        } else if ((player.getPvpFlag() > 0 && !player.isInsideZone(ZoneId.PEACE)) || (player.isInCombat() || player.isAttackingNow())) {
//            return false;
//        }
        return false;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }

}
