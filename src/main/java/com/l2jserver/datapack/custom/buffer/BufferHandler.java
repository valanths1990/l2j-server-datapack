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

/**
 * @author Silvar
 */
public class BufferHandler implements IBypassHandler {
	private final String[] COMMANDS = { "buffer;general", "buffer;dance", "buffer;song", "buffer;simple", "buffer;resist", "buffer;homepage", "buffer;specific" };
	private final String schemeButtonHtml = "<tr><td><button value=\"%schemename%\" action=\"bypass buffer;homepage scheme %name%\" width=120 height=30back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td><td><button value=\"X\" action=\"bypass buffer;homepage;delete;%schemename%\" width=30 height=30back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>";
	private final SchemeBufferTable schemeTable = SchemeBufferTable.getInstance();
	private final int[] warriorBuffs = {
		1035, 1499, 1500, 1501, 1502, 1503, 1504, 1519, 1062, 1078, 1085, 1259, 1352, 1353, 1354, 1363, 1388, 1397, 1461, 1542, 1364, 1273, 1232, 982, 1416, 4699, 271, 274, 275, 276, 310, 915, 264, 267, 268, 269, 304, 349, 364, 764, 914, 305
	};
	private final int[] mageBuffs = {
		1500, 1501, 1503, 1504, 1062, 1078, 1085, 1303, 1352, 1353, 1354, 1357, 1389, 13971, 1461, 1542, 1364, 1273, 1232, 1416, 4703, 273, 276, 365, 915, 264, 267, 268, 304, 305, 349, 363, 764, 914, 830
	};

	@Override public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
		HtmCache.getInstance().reload();
		String[] tmp = command.split(" ");
		String currentPage = tmp[0].split(";")[1];
		if (tmp.length == 1) {
			openBuffBoard(player, currentPage);
			return true;
		}

		switch (tmp[1]) {
			case "save" -> saveScheme(player, tmp[2]);
			case "delete" -> deleteScheme(player, tmp[2]);
			case "scheme" -> schemeBuff(player, tmp[2]);
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
				buffplayer(player, tmp[1]);
			}
		}
		openBuffBoard(player, currentPage);
		return true;
	}

	public void saveScheme(L2PcInstance player, String schemeName) {

		List<Integer> buffsIds = new ArrayList<>();
		player.getEffectList().getBuffs().forEach(b -> {
			buffsIds.add(b.getSkill().getId());
		});
		player.getEffectList().getDances().forEach(b -> {
			buffsIds.add(b.getSkill().getId());
		});
		schemeTable.setScheme(player.getId(), schemeName.trim().replaceAll(" ", ""), buffsIds);
		schemeTable.saveSchemes();
	}

	public void deleteScheme(L2PcInstance player, String schemeName) {
		schemeTable.deleteScheme(player.getId(), schemeName);
	}

	public void openBuffBoard(L2PcInstance player, String board) {

		if (board.equals("homepage")) {
			Map<String, List<Integer>> playerSchemes = null;
			if ((playerSchemes = schemeTable.getPlayerSchemes(player.getId())) != null) {
				StringBuilder html = new StringBuilder(HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/buffer/custom/homepage.html"));
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
					case HEAVY:
						skill = SkillData.getInstance().getSkill(828, schemeTable.getAvailableBuff(828).getLevel());
						break;
					case LIGHT:
						skill = SkillData.getInstance().getSkill(829, schemeTable.getAvailableBuff(829).getLevel());
						break;
					case MAGIC:
						skill = SkillData.getInstance().getSkill(830, schemeTable.getAvailableBuff(830).getLevel());

						break;
					default:
				}
			}
			if (skill != null) {
				skill.applyEffects(player, player, true, 10800);
			}

		}
	}

	public void buffplayer(L2PcInstance player, String buff) {
		if (!isAllowed(player)) {
			System.out.println("player not allowed to take buff");
			return;
		}
		try {
			int buffId = Integer.parseInt(buff);
			BuffSkillHolder buffskillholder = schemeTable.getAvailableBuff(buffId);
			Skill skill = SkillData.getInstance().getSkill(buffskillholder.getId(), buffskillholder.getLevel());
			if (skill != null) {
				// player.setCurrentHp(player.getMaxHp() / 2);
				// player.setCurrentMp(player.getMaxMp() / 2);
				// player.setCurrentCp(player.getMaxCp() / 2);
				skill.applyEffects(player, player, true, 10800);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

	}

	private void schemeBuff(L2PcInstance player, String schemename) {
		List<Integer> buffs = schemeTable.getScheme(player.getId(), schemename);
		buffs.stream().forEach(i -> {
			Skill skill = SkillData.getInstance().getSkill(i, schemeTable.getAvailableBuff(i).getLevel());
			if (skill != null) {
				skill.applyEffects(player, player, true, 10800);
			}
		});
	}

	public boolean isAllowed(L2PcInstance player) {

		//		if (player.isInsideZone(ZoneId.PEACE)) {
		//			System.out.println("peace zone");
		//			return true;
		//		} else if (player.isInsideZone(ZoneId.PVP) && !(player.isInCombat() || player.isAttackingNow())) {
		//			System.out.println("pvp zone and not in combat");
		//			return true;
		//		} else if (player.isInsideZone(ZoneId.FlagZone) && !(player.isInCombat() || player.isAttackingNow())) {
		//			System.out.println("pvp zone and not in combat");
		//			return true;
		//		} else if ((player.getPvpFlag() > 0 && !player.isInsideZone(ZoneId.PEACE)) || (player.isInCombat() || player.isAttackingNow())) {
		//			System.out.println("in combat flagged and outside peace zone");
		//			return false;
		//		}
		return true;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}

}
