/*
 * Copyright (C) 2004-2015 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quests.Q00234_FatesWhisper;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.enums.QuestSound;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.quest.Quest;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.network.NpcStringId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.NpcSay;
import com.l2jserver.gameserver.util.Util;

/**
 * Fate's Whisper (234)
 * @author Zealar
 */
public final class Q00234_FatesWhisper extends Quest
{
	// NPCs
	private static final int zenkin = 30178;
	private static final int cliff = 30182;
	private static final int master_kaspar = 30833;
	private static final int head_blacksmith_ferris = 30847;
	private static final int maestro_leorin = 31002;
	private static final int coffer_of_the_dead = 31027;
	private static final int chest_of_kernon = 31028;
	private static final int chest_of_golkonda = 31029;
	private static final int chest_of_hallate = 31030;
	// Quest Items
	private static final int q_bloody_fabric_q0234 = 14361;
	private static final int q_white_fabric_q0234 = 14362;
	private static final int q_star_of_destiny = 5011;
	private static final int q_pipette_knife = 4665;
	private static final int q_reirias_soulorb = 4666;
	private static final int q_infernium_scepter_1 = 4667;
	private static final int q_infernium_scepter_2 = 4668;
	private static final int q_infernium_scepter_3 = 4669;
	private static final int q_maestro_reorins_hammer = 4670;
	private static final int q_maestro_reorins_mold = 4671;
	private static final int q_infernium_varnish = 4672;
	private static final int q_red_pipette_knife = 4673;
	// Other Items
	private static final int crystal_b = 1460;
	// Monsters
	private static final int platinum_tribe_grunt = 20823;
	private static final int platinum_tribe_archer = 20826;
	private static final int platinum_tribe_warrior = 20827;
	private static final int platinum_tribe_shaman = 20828;
	private static final int platinum_tribe_lord = 20829;
	private static final int guardian_angel = 20830;
	private static final int seal_angel = 20831;
	private static final int seal_angel_r = 20860;
	
	private static final int domb_death_cabrio = 25035;
	private static final int kernon = 25054;
	private static final int golkonda_longhorn = 25126;
	private static final int hallate_the_death_lord = 25220;
	private static final int baium = 29020;
	
	// B-grade
	private static final int sword_of_damascus = 79;
	private static final int sword_of_damascus_focus = 4717;
	private static final int sword_of_damascus_crt_damage = 4718;
	private static final int sword_of_damascus_haste = 4719;
	private static final int hazard_bow = 287;
	private static final int hazard_bow_guidence = 4828;
	private static final int hazard_bow_quickrecovery = 4829;
	private static final int hazard_bow_cheapshot = 4830;
	private static final int lancia = 97;
	private static final int lancia_anger = 4858;
	private static final int lancia_crt_stun = 4859;
	private static final int lancia_longblow = 4860;
	private static final int art_of_battle_axe = 175;
	private static final int art_of_battle_axe_health = 4753;
	private static final int art_of_battle_axe_rsk_focus = 4754;
	private static final int art_of_battle_axe_haste = 4755;
	private static final int staff_of_evil_sprit = 210;
	private static final int staff_of_evil_sprit_magicfocus = 4900;
	private static final int staff_of_evil_sprit_magicblessthebody = 4901;
	private static final int staff_of_evil_sprit_magicpoison = 4902;
	private static final int demons_sword = 234;
	private static final int demons_sword_crt_bleed = 4780;
	private static final int demons_sword_crt_poison = 4781;
	private static final int demons_sword_mightmotal = 4782;
	private static final int bellion_cestus = 268;
	private static final int bellion_cestus_crt_drain = 4804;
	private static final int bellion_cestus_crt_poison = 4805;
	private static final int bellion_cestus_rsk_haste = 4806;
	private static final int deadmans_glory = 171;
	private static final int deadmans_glory_anger = 4750;
	private static final int deadmans_glory_health = 4751;
	private static final int deadmans_glory_haste = 4752;
	private static final int samurai_longsword_samurai_longsword = 2626;
	private static final int guardians_sword = 7883;
	private static final int guardians_sword_crt_drain = 8105;
	private static final int guardians_sword_health = 8106;
	private static final int guardians_sword_crt_bleed = 8107;
	private static final int tears_of_wizard = 7889;
	private static final int tears_of_wizard_acumen = 8117;
	private static final int tears_of_wizard_magicpower = 8118;
	private static final int tears_of_wizard_updown = 8119;
	private static final int star_buster = 7901;
	private static final int star_buster_health = 8132;
	private static final int star_buster_haste = 8133;
	private static final int star_buster_rsk_focus = 8134;
	private static final int bone_of_kaim_vanul = 7893;
	private static final int bone_of_kaim_vanul_manaup = 8144;
	private static final int bone_of_kaim_vanul_magicsilence = 8145;
	private static final int bone_of_kaim_vanul_updown = 8146;
	// A-grade
	private static final int tallum_blade = 80;
	private static final int carnium_bow = 288;
	private static final int halbard = 98;
	private static final int elemental_sword = 150;
	private static final int dasparions_staff = 212;
	private static final int bloody_orchid = 235;
	private static final int blood_tornado = 269;
	private static final int meteor_shower = 2504;
	private static final int kshanberk_kshanberk = 5233;
	private static final int inferno_master = 7884;
	private static final int eye_of_soul = 7894;
	private static final int hammer_of_destroyer = 7899;
	
	public Q00234_FatesWhisper()
	{
		super(234, Q00234_FatesWhisper.class.getSimpleName(), "Fate's Whisper");
		addStartNpc(maestro_leorin);
		addTalkId(zenkin, cliff, master_kaspar, head_blacksmith_ferris, maestro_leorin);
		addTalkId(coffer_of_the_dead, chest_of_kernon, chest_of_hallate, chest_of_golkonda);
		
		addKillId(platinum_tribe_grunt, platinum_tribe_archer, platinum_tribe_warrior, platinum_tribe_shaman, platinum_tribe_lord, guardian_angel, seal_angel, seal_angel_r);
		addKillId(domb_death_cabrio, kernon, golkonda_longhorn, hallate_the_death_lord);
		
		addSpawnId(coffer_of_the_dead, chest_of_kernon, chest_of_hallate, chest_of_golkonda);
		addAttackId(baium);
		registerQuestItems(q_bloody_fabric_q0234, q_white_fabric_q0234, q_pipette_knife, q_reirias_soulorb, q_infernium_scepter_1, q_infernium_scepter_2, q_infernium_scepter_3, q_maestro_reorins_hammer, q_maestro_reorins_mold, q_infernium_varnish, q_red_pipette_knife);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		switch (npc.getId())
		{
			case coffer_of_the_dead:
			{
				startQuestTimer("23401", 1000 * 120, npc, null);
				break;
			}
			case chest_of_kernon:
			{
				startQuestTimer("23402", 1000 * 120, npc, null);
				break;
			}
			case chest_of_hallate:
			{
				startQuestTimer("23403", 1000 * 120, npc, null);
				break;
			}
			case chest_of_golkonda:
			{
				startQuestTimer("23404", 1000 * 120, npc, null);
				break;
			}
		}
		return super.onSpawn(npc);
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		final QuestState qs = getQuestState(player, true);
		String htmltext = getNoQuestMsg(player);
		if (qs == null)
		{
			return htmltext;
		}
		switch (npc.getId())
		{
			case zenkin:
			{
				switch (qs.getMemoState())
				{
					case 6:
						return "30178-01.html";
					case 7:
						return "30178-03.html";
					case 8:
						return "30178-04.html";
				}
				break;
			}
			case cliff:
			{
				if (qs.isMemoState(4) && !qs.hasQuestItems(q_infernium_varnish))
				{
					return "30182-01.html";
				}
				if (qs.isMemoState(4) && qs.hasQuestItems(q_infernium_varnish))
				{
					return "30182-05.html";
				}
				if (qs.getMemoState() >= 5)
				{
					return "30182-06.html";
				}
			}
			case master_kaspar:
			{
				if (qs.isMemoState(7))
				{
					return "30833-01.html";
				}
				if (qs.isMemoState(8) && !qs.hasQuestItems(q_red_pipette_knife) && !qs.hasQuestItems(q_bloody_fabric_q0234, q_white_fabric_q0234))
				{
					return "30833-03.html";
				}
				if (qs.isMemoState(8) && qs.hasQuestItems(q_red_pipette_knife) && !qs.hasQuestItems(q_bloody_fabric_q0234, q_white_fabric_q0234))
				{
					qs.giveItems(q_maestro_reorins_mold, 1);
					qs.takeItems(q_red_pipette_knife, 1);
					qs.setMemoState(9);
					qs.setCond(10, true);
					qs.showQuestionMark(234);
					return "30833-04.html";
				}
				if (qs.isMemoState(8) && !qs.hasQuestItems(q_red_pipette_knife) && (qs.getQuestItemsCount(q_bloody_fabric_q0234) < 30) && ((qs.getQuestItemsCount(q_bloody_fabric_q0234) + qs.getQuestItemsCount(q_white_fabric_q0234)) >= 30))
				{
					return "30833-03c.html";
				}
				if (qs.isMemoState(8) && !qs.hasQuestItems(q_red_pipette_knife) && (qs.getQuestItemsCount(q_bloody_fabric_q0234) >= 30) && ((qs.getQuestItemsCount(q_bloody_fabric_q0234) + qs.getQuestItemsCount(q_white_fabric_q0234)) >= 30))
				{
					qs.giveItems(q_maestro_reorins_mold, 1);
					qs.takeItems(q_bloody_fabric_q0234, -1);
					qs.setMemoState(9);
					qs.setCond(10, true);
					qs.showQuestionMark(234);
					return "30833-03d.html";
				}
				if (qs.isMemoState(8) && !qs.hasQuestItems(q_red_pipette_knife) && ((qs.getQuestItemsCount(q_bloody_fabric_q0234) + qs.getQuestItemsCount(q_white_fabric_q0234)) < 30) && ((qs.getQuestItemsCount(q_bloody_fabric_q0234) + qs.getQuestItemsCount(q_white_fabric_q0234)) > 0))
				{
					qs.giveItems(q_white_fabric_q0234, 30 - qs.getQuestItemsCount(q_white_fabric_q0234));
					qs.takeItems(q_bloody_fabric_q0234, -1);
					return "30833-03e.html";
				}
				if (qs.getMemoState() >= 9)
				{
					return "30833-05.html";
				}
				break;
			}
			case head_blacksmith_ferris:
			{
				if (qs.isMemoState(5))
				{
					if (qs.hasQuestItems(q_maestro_reorins_hammer))
					{
						return "30847-02.html";
					}
					qs.giveItems(q_maestro_reorins_hammer, 1);
					return "30847-01.html";
				}
				if (qs.getMemoState() >= 6)
				{
					return "30847-03.html";
				}
				break;
			}
			case maestro_leorin:
			{
				if (qs.isCreated() && (player.getLevel() >= 75))
				{
					return "31002-01.htm";
				}
				if (qs.isCreated() && (player.getLevel() < 75))
				{
					return "31002-01a.htm";
				}
				if (qs.isCompleted())
				{
					return getAlreadyCompletedMsg(player);
				}
				if (qs.isMemoState(1) && !qs.hasQuestItems(q_reirias_soulorb))
				{
					return "31002-09.html";
				}
				if (qs.isMemoState(1) && qs.hasQuestItems(q_reirias_soulorb))
				{
					return "31002-10.html";
				}
				if (qs.isMemoState(2) && !qs.hasQuestItems(q_infernium_scepter_1, q_infernium_scepter_2, q_infernium_scepter_3))
				{
					return "31002-12.html";
				}
				if (qs.isMemoState(2) && qs.hasQuestItems(q_infernium_scepter_1, q_infernium_scepter_2, q_infernium_scepter_3))
				{
					return "31002-13.html";
				}
				if (qs.isMemoState(4) && !qs.hasQuestItems(q_infernium_varnish))
				{
					return "31002-15.html";
				}
				if (qs.isMemoState(4) && qs.hasQuestItems(q_infernium_varnish))
				{
					return "31002-16.html";
				}
				if (qs.isMemoState(5) && !qs.hasQuestItems(q_maestro_reorins_hammer))
				{
					return "31002-18.html";
				}
				if (qs.isMemoState(5) && qs.hasQuestItems(q_maestro_reorins_hammer))
				{
					return "31002-19.html";
				}
				if ((qs.getMemoState() < 9) && (qs.getMemoState() >= 6))
				{
					return "31002-21.html";
				}
				if (qs.isMemoState(9) && qs.hasQuestItems(q_maestro_reorins_mold))
				{
					return "31002-22.html";
				}
				if (qs.isMemoState(10) && (qs.getQuestItemsCount(crystal_b) < 984))
				{
					return "31002-24.html";
				}
				if (qs.isMemoState(10) && (qs.getQuestItemsCount(crystal_b) >= 984))
				{
					return "31002-25.html";
				}
				switch (qs.getMemoState())
				{
					case 11:
						if (hasAtLeastOneQuestItem(player, sword_of_damascus, sword_of_damascus_focus, sword_of_damascus_crt_damage, sword_of_damascus_haste))
						{
							return "31002-35.html";
						}
						return "31002-35a.html";
					case 12:
						if (hasAtLeastOneQuestItem(player, hazard_bow_guidence, hazard_bow_quickrecovery, hazard_bow_cheapshot, hazard_bow))
						{
							return "31002-36.html";
						}
						return "31002-36a.html";
					case 13:
						if (hasAtLeastOneQuestItem(player, lancia_anger, lancia_crt_stun, lancia_longblow, lancia))
						{
							return "31002-37.html";
						}
						return "31002-37a.html";
					case 14:
						if (hasAtLeastOneQuestItem(player, art_of_battle_axe_health, art_of_battle_axe_rsk_focus, art_of_battle_axe_haste, art_of_battle_axe))
						{
							return "31002-38.html";
						}
						return "31002-38a.html";
					case 15:
						if (hasAtLeastOneQuestItem(player, staff_of_evil_sprit_magicfocus, staff_of_evil_sprit_magicblessthebody, staff_of_evil_sprit_magicpoison, staff_of_evil_sprit))
						{
							return "31002-39.html";
						}
						return "31002-39a.html";
					case 16:
						if (hasAtLeastOneQuestItem(player, demons_sword_crt_bleed, demons_sword_crt_poison, demons_sword_mightmotal, demons_sword))
						{
							return "31002-40.html";
						}
						return "31002-40a.html";
					case 17:
						if (hasAtLeastOneQuestItem(player, bellion_cestus_crt_drain, bellion_cestus_crt_poison, bellion_cestus_rsk_haste, bellion_cestus))
						{
							return "31002-41.html";
						}
						return "31002-41a.html";
					case 18:
						if (hasAtLeastOneQuestItem(player, deadmans_glory_anger, deadmans_glory_health, deadmans_glory_haste, deadmans_glory))
						{
							return "31002-42.html";
						}
						return "31002-42a.html";
					case 19:
						if (hasAtLeastOneQuestItem(player, samurai_longsword_samurai_longsword))
						{
							return "31002-43.html";
						}
						return "31002-43a.html";
					case 41:
						if (hasAtLeastOneQuestItem(player, guardians_sword, guardians_sword_crt_drain, guardians_sword_health, guardians_sword_crt_bleed))
						{
							return "31002-43b.html";
						}
						return "31002-43c.html";
					case 42:
						if (hasAtLeastOneQuestItem(player, tears_of_wizard, tears_of_wizard_acumen, tears_of_wizard_magicpower, tears_of_wizard_updown))
						{
							return "31002-43d.html";
						}
						return "31002-43e.html";
					case 43:
						if (hasAtLeastOneQuestItem(player, star_buster, star_buster_health, star_buster_haste, star_buster_rsk_focus))
						{
							return "31002-43f.html";
						}
						return "31002-43g.html";
					case 44:
						if (hasAtLeastOneQuestItem(player, bone_of_kaim_vanul, bone_of_kaim_vanul_manaup, bone_of_kaim_vanul_magicsilence, bone_of_kaim_vanul_updown))
						{
							return "31002-43h.html";
						}
						return "31002-43i.html";
				}
				break;
			}
			case coffer_of_the_dead:
			{
				if (qs.isMemoState(1) && !qs.hasQuestItems(q_reirias_soulorb))
				{
					qs.giveItems(q_reirias_soulorb, 1);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					return "31027-01.html";
				}
				if ((qs.getMemoState() > 1) || qs.hasQuestItems(q_reirias_soulorb))
				{
					return "31027-02.html";
				}
				break;
			}
			case chest_of_kernon:
			{
				if (qs.isMemoState(2) && !qs.hasQuestItems(q_infernium_scepter_1))
				{
					qs.giveItems(q_infernium_scepter_1, 1);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					return "31028-01.html";
				}
				if ((qs.getMemoState() != 2) || qs.hasQuestItems(q_infernium_scepter_1))
				{
					return "31028-02.html";
				}
				break;
			}
			case chest_of_golkonda:
			{
				if (qs.isMemoState(2) && !qs.hasQuestItems(q_infernium_scepter_2))
				{
					qs.giveItems(q_infernium_scepter_2, 1);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					return "31029-01.html";
				}
				if ((qs.getMemoState() != 2) || qs.hasQuestItems(q_infernium_scepter_2))
				{
					return "31029-02.html";
				}
				break;
			}
			case chest_of_hallate:
			{
				if (qs.isMemoState(2) && !qs.hasQuestItems(q_infernium_scepter_3))
				{
					qs.giveItems(q_infernium_scepter_3, 1);
					qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					return "31030-01.html";
				}
				if ((qs.getMemoState() != 2) || qs.hasQuestItems(q_infernium_scepter_3))
				{
					return "31030-02.html";
				}
				break;
			}
		}
		return htmltext;
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (player == null)
		{
			if (event.equals("23401") || event.equals("23402") || event.equals("23403") || event.equals("23404"))
			{
				npc.decayMe();
			}
			return super.onAdvEvent(event, npc, player);
		}
		
		final QuestState qs = getQuestState(player, false);
		if (qs == null)
		{
			return null;
		}
		
		String htmltext = null;
		
		if (event.equals("QUEST_ACCEPTED"))
		{
			qs.setMemoState(1);
			qs.startQuest();
			qs.showQuestionMark(234);
			qs.playSound(QuestSound.ITEMSOUND_QUEST_ACCEPT);
			return "31002-06.html";
		}
		if (event.contains(".htm"))
		{
			return event;
		}
		
		int npcId = npc.getId();
		int eventID = Integer.parseInt(event);
		
		switch (npcId)
		{
			case zenkin:
			{
				switch (eventID)
				{
					case 1:
					{
						qs.setMemoState(7);
						qs.setCond(6);
						qs.showQuestionMark(234);
						qs.playSound(QuestSound.ITEMSOUND_QUEST_MIDDLE);
						return "30178-02.html";
					}
				}
			}
			case cliff:
			{
				switch (eventID)
				{
					case 1:
					{
						return "30182-02.html";
					}
					case 2:
					{
						return "30182-03.html";
					}
					case 3:
					{
						if ((qs.getMemoState() == 4) && !qs.hasQuestItems(q_infernium_varnish))
						{
							qs.giveItems(q_infernium_varnish, 1);
							return "30182-04.html";
						}
					}
				}
				break;
			}
			case master_kaspar:
			{
				switch (eventID)
				{
					case 1:
					{
						if (qs.isMemoState(7))
						{
							return "30182-02.html";
						}
						break;
					}
					case 2:
					{
						if (qs.isMemoState(7))
						{
							qs.giveItems(q_pipette_knife, 1);
							qs.setMemoState(8);
							qs.setCond(7, true);
							qs.showQuestionMark(234);
							return "30833-03a.html";
						}
						break;
					}
					case 3:
					{
						if (qs.isMemoState(7))
						{
							qs.giveItems(q_white_fabric_q0234, 30);
							qs.setMemoState(8);
							qs.setCond(8, true);
							qs.showQuestionMark(234);
							return "30833-03b.html";
						}
						break;
					}
				}
				break;
			}
			case maestro_leorin:
			{
				switch (eventID)
				{
					case 1:
						return "31002-02.htm";
					case 2:
						return "31002-03.html";
					case 3:
						return "31002-04.html";
					case 4:
					{
						if (!qs.isCompleted() && (player.getLevel() >= 75))
						{
							return "31002-05.html";
						}
						break;
					}
					case 5:
					{
						if (qs.isMemoState(1) && qs.hasQuestItems(q_reirias_soulorb))
						{
							qs.takeItems(q_reirias_soulorb, 1);
							qs.setMemoState(2);
							qs.setCond(2, true);
							qs.showQuestionMark(234);
							return "31002-11.html";
						}
						break;
					}
					case 6:
					{
						if (qs.isMemoState(2) && qs.hasQuestItems(q_infernium_scepter_1, q_infernium_scepter_2, q_infernium_scepter_3))
						{
							qs.takeItems(q_infernium_scepter_1, -1);
							qs.takeItems(q_infernium_scepter_2, -1);
							qs.takeItems(q_infernium_scepter_3, -1);
							qs.setMemoState(4);
							qs.setCond(3, true);
							qs.showQuestionMark(234);
							return "31002-14.html";
						}
						break;
					}
					case 7:
					{
						if (qs.isMemoState(4) && qs.hasQuestItems(q_infernium_varnish))
						{
							qs.takeItems(q_infernium_varnish, 1);
							qs.setMemoState(5);
							qs.setCond(4, true);
							qs.showQuestionMark(234);
							return "31002-17.html";
						}
						break;
					}
					case 8:
					{
						if (qs.isMemoState(5) && qs.hasQuestItems(q_maestro_reorins_hammer))
						{
							qs.takeItems(q_maestro_reorins_hammer, 1);
							qs.setMemoState(6);
							qs.setCond(5, true);
							qs.showQuestionMark(234);
							return "31002-20.html";
						}
						break;
					}
					case 9:
					{
						if (qs.isMemoState(9) && qs.hasQuestItems(q_maestro_reorins_mold))
						{
							qs.takeItems(q_maestro_reorins_mold, 1);
							qs.setMemoState(10);
							qs.setCond(11, true);
							qs.showQuestionMark(234);
							return "31002-23.html";
						}
						break;
					}
					case 10:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(11);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-26.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 11:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(19);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-26a.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 12:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(12);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-27.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 13:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(13);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-28.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 14:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(14);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-29.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 15:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(15);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-30.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 16:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(16);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-31.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 17:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(17);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-32.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 18:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(18);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-33.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 41:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(41);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-33a.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 42:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(42);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-33b.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 43:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(43);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-33c.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 44:
					{
						if (qs.isMemoState(10))
						{
							if (qs.getQuestItemsCount(crystal_b) >= 984)
							{
								qs.takeItems(crystal_b, 984);
								qs.setMemoState(44);
								qs.setCond(12, true);
								qs.showQuestionMark(234);
								return "31002-33d.html";
							}
							return "31002-34.html";
						}
						break;
					}
					case 21:
					{
						if (calculateReward(qs, player, tallum_blade))
						{
							return "31002-44.html";
						}
						break;
					}
					case 22:
					{
						if (calculateReward(qs, player, carnium_bow))
						{
							return "31002-44.html";
						}
						break;
					}
					case 23:
					{
						if (calculateReward(qs, player, halbard))
						{
							return "31002-44.html";
						}
						break;
					}
					case 24:
					{
						if (calculateReward(qs, player, elemental_sword))
						{
							return "31002-44.html";
						}
						break;
					}
					case 25:
					{
						if (calculateReward(qs, player, dasparions_staff))
						{
							return "31002-44.html";
						}
						break;
					}
					case 26:
					{
						if (calculateReward(qs, player, bloody_orchid))
						{
							return "31002-44.html";
						}
						break;
					}
					case 27:
					{
						if (calculateReward(qs, player, blood_tornado))
						{
							return "31002-44.html";
						}
						break;
					}
					case 28:
					{
						if (calculateReward(qs, player, meteor_shower))
						{
							return "31002-44.html";
						}
						break;
					}
					case 29:
					{
						if (calculateReward(qs, player, kshanberk_kshanberk))
						{
							return "31002-44.html";
						}
						break;
					}
					case 30:
					{
						if (calculateReward(qs, player, inferno_master))
						{
							return "31002-44.html";
						}
						break;
					}
					case 31:
					{
						if (calculateReward(qs, player, eye_of_soul))
						{
							return "31002-44.html";
						}
						break;
					}
					case 32:
					{
						if (calculateReward(qs, player, hammer_of_destroyer))
						{
							return "31002-44.html";
						}
						break;
					}
				}
			}
			
		}
		return htmltext;
	}
	
	private boolean calculateReward(QuestState qs, L2PcInstance player, int REWARD)
	{
		switch (qs.getMemoState())
		{
			case 11:
				return getReward(qs, player, sword_of_damascus, sword_of_damascus_focus, sword_of_damascus_crt_damage, sword_of_damascus_haste, REWARD);
			case 12:
				return getReward(qs, player, hazard_bow, hazard_bow_guidence, hazard_bow_quickrecovery, hazard_bow_cheapshot, REWARD);
			case 13:
				return getReward(qs, player, lancia, lancia_anger, lancia_crt_stun, lancia_longblow, REWARD);
			case 14:
				return getReward(qs, player, art_of_battle_axe, art_of_battle_axe_health, art_of_battle_axe_rsk_focus, art_of_battle_axe_haste, REWARD);
			case 15:
				return getReward(qs, player, staff_of_evil_sprit, staff_of_evil_sprit_magicfocus, staff_of_evil_sprit_magicblessthebody, staff_of_evil_sprit_magicpoison, REWARD);
			case 16:
				return getReward(qs, player, demons_sword, demons_sword_crt_bleed, demons_sword_crt_poison, demons_sword_mightmotal, REWARD);
			case 17:
				return getReward(qs, player, bellion_cestus, bellion_cestus_crt_drain, bellion_cestus_crt_poison, bellion_cestus_rsk_haste, REWARD);
			case 18:
				return getReward(qs, player, deadmans_glory, deadmans_glory_anger, deadmans_glory_health, deadmans_glory_haste, REWARD);
			case 19:
				return getReward(qs, player, samurai_longsword_samurai_longsword, 0, 0, 0, REWARD);
			case 41:
				return getReward(qs, player, guardians_sword, guardians_sword_crt_drain, guardians_sword_health, guardians_sword_crt_bleed, REWARD);
			case 42:
				return getReward(qs, player, tears_of_wizard, tears_of_wizard_acumen, tears_of_wizard_magicpower, tears_of_wizard_updown, REWARD);
			case 43:
				return getReward(qs, player, star_buster, star_buster_health, star_buster_haste, star_buster_rsk_focus, REWARD);
			case 44:
				return getReward(qs, player, bone_of_kaim_vanul, bone_of_kaim_vanul_manaup, bone_of_kaim_vanul_magicsilence, bone_of_kaim_vanul_updown, REWARD);
		}
		return false;
	}
	
	private boolean getReward(QuestState qs, L2PcInstance player, int ITEM1, int ITEM2, int ITEM3, int ITEM4, int REWARD)
	{
		if (hasAtLeastOneQuestItem(player, ITEM1, ITEM2, ITEM3, ITEM4))
		{
			qs.giveItems(REWARD, 1);
			qs.giveItems(q_star_of_destiny, 1);
			if (qs.hasQuestItems(ITEM1))
			{
				qs.takeItems(ITEM1, 1);
			}
			else if (qs.hasQuestItems(ITEM2))
			{
				qs.takeItems(ITEM2, 1);
			}
			else if (qs.hasQuestItems(ITEM3))
			{
				qs.takeItems(ITEM3, 1);
			}
			else if (qs.hasQuestItems(ITEM4))
			{
				qs.takeItems(ITEM4, 1);
			}
			qs.exitQuest(false, true);
			player.broadcastSocialAction(3);
			return true;
		}
		return false;
	}
	
	@Override
	public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
	{
		switch (npc.getId())
		{
			case domb_death_cabrio:
			{
				addSpawn(coffer_of_the_dead, npc.getLocation());
				return super.onKill(npc, killer, isSummon);
			}
			case kernon:
			{
				addSpawn(chest_of_kernon, npc.getLocation());
				return super.onKill(npc, killer, isSummon);
			}
			case golkonda_longhorn:
			{
				addSpawn(chest_of_golkonda, npc.getLocation());
				return super.onKill(npc, killer, isSummon);
			}
			case hallate_the_death_lord:
			{
				addSpawn(chest_of_hallate, npc.getLocation());
				return super.onKill(npc, killer, isSummon);
			}
		}
		final QuestState qs = getRandomPlayerFromParty(killer, npc, 8);
		if (qs != null)
		{
			switch (npc.getId())
			{
				case platinum_tribe_grunt:
				case platinum_tribe_archer:
				case platinum_tribe_warrior:
				case platinum_tribe_shaman:
				case platinum_tribe_lord:
				case guardian_angel:
				case seal_angel:
				case seal_angel_r:
				{
					giveItemRandomly(qs.getPlayer(), npc, q_bloody_fabric_q0234, 1, 0, 1, false);
					qs.takeItems(q_white_fabric_q0234, 1);
					if (qs.getQuestItemsCount(q_bloody_fabric_q0234) >= 29)
					{
						qs.setCond(9, true);
						qs.showQuestionMark(234);
					}
					else
					{
						qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
					}
					break;
				}
			}
			
		}
		return super.onKill(npc, killer, isSummon);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon)
	{
		QuestState qs = attacker.getQuestState(getName());
		if ((qs != null) && (npc.getId() == baium))
		{
			if ((attacker.getActiveWeaponItem() != null) && (attacker.getActiveWeaponItem().getId() == q_pipette_knife))
			{
				qs.takeItems(q_pipette_knife, 1);
				qs.giveItems(q_red_pipette_knife, 1);
				qs.playSound(QuestSound.ITEMSOUND_QUEST_ITEMGET);
				npc.broadcastPacket(new NpcSay(npc.getObjectId(), Say2.ALL, npc.getId(), NpcStringId.WHO_DARES_TO_TRY_AND_STEAL_MY_NOBLE_BLOOD));
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	private QuestState getRandomPlayerFromParty(L2PcInstance player, L2Npc npc, int memoState)
	{
		QuestState qs = player.getQuestState(getName());
		final List<QuestState> candidates = new ArrayList<>();
		
		if ((qs != null) && qs.isStarted() && (qs.getMemoState() == memoState) && !qs.hasQuestItems(q_white_fabric_q0234))
		{
			candidates.add(qs);
			candidates.add(qs);
		}
		
		if (player.isInParty())
		{
			player.getParty().getMembers().stream().forEach(pm ->
			{
				
				QuestState qss = pm.getQuestState(getName());
				if ((qss != null) && qss.isStarted() && (qss.getMemoState() == memoState) && !qss.hasQuestItems(q_white_fabric_q0234) && Util.checkIfInRange(1500, npc, pm, true))
				{
					candidates.add(qss);
				}
			});
		}
		return candidates.isEmpty() ? null : candidates.get(getRandom(candidates.size()));
	}
}
