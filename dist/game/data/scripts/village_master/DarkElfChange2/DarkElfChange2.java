/*
 * Copyright (C) 2004-2017 L2J DataPack
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
package village_master.DarkElfChange2;

import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;

import ai.npc.AbstractNpcAI;

/**
 * Dark Elf class transfer AI.
 * @author Adry_85
 * @since 2.6.0.0
 */
public final class DarkElfChange2 extends AbstractNpcAI
{
	// NPCs
	private static final int[] NPCS =
	{
		30195, // Brecson
		30474, // Angus
		30699, // Medown
		30862, // Oltran
		30910, // Xairakin
		31285, // Samael
		31324, // Andromeda
		31334, // Tifaren
		31974, // Drizzit
	};
	
	// Classes
	private static final int SHILLIEN_KNIGHT = 33;
	private static final int BLADEDANCER = 34;
	private static final int ABYSS_WALKER = 36;
	private static final int PHANTOM_RANGER = 37;
	private static final int SPELLHOWLER = 40;
	private static final int PHANTOM_SUMMONER = 41;
	private static final int SHILLIEN_ELDER = 43;
	// Items
	private static final int MARK_OF_CHALLENGER = 2627; // proof12x
	private static final int MARK_OF_DUTY = 2633; // proof11x
	private static final int MARK_OF_SEEKER = 2673; // proof21x, proof22x
	private static final int MARK_OF_SCHOLAR = 2674; // proof31x, proof32x
	private static final int MARK_OF_PILGRIM = 2721; // proof41x
	private static final int MARK_OF_DUELIST = 2762; // proof12z
	private static final int MARK_OF_SEARCHER = 2809; // proof21z
	private static final int MARK_OF_REFORMER = 2821; // proof41z
	private static final int MARK_OF_MAGUS = 2840; // proof31z
	private static final int MARK_OF_FATE = 3172; // proof11y, proof12y, proof21y, proof22y, proof31y, proof32y, proof41y
	private static final int MARK_OF_SAGITTARIUS = 3293; // proof22z
	private static final int MARK_OF_WITCHCRAFT = 3307; // proof11z
	private static final int MARK_OF_SUMMONER = 3336; // proof32z
	// Reward
	private static final int SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE = 8870;
	// Misc
	private static final int MIN_LEVEL = 40;
	
	private DarkElfChange2()
	{
		super(DarkElfChange2.class.getSimpleName(), "village_master");
		addStartNpc(NPCS);
		addTalkId(NPCS);
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		switch (event)
		{
			case "30195-02.htm": // master_lv3_de003k
			case "30195-03.htm": // master_lv3_de006ka
			case "30195-04.htm": // master_lv3_de007ka
			case "30195-05.htm": // master_lv3_de007kat
			case "30195-06.htm": // master_lv3_de006kb
			case "30195-07.htm": // master_lv3_de007kb
			case "30195-08.htm": // master_lv3_de007kbt
			case "30195-09.htm": // master_lv3_de003s
			case "30195-10.htm": // master_lv3_de006sa
			case "30195-11.htm": // master_lv3_de007sa
			case "30195-12.htm": // master_lv3_de007sat
			case "30195-13.htm": // master_lv3_de006sb
			case "30195-14.htm": // master_lv3_de007sb
			case "30195-15.htm": // master_lv3_de007sbt
			case "30195-16.htm": // master_lv3_de003w
			case "30195-17.htm": // master_lv3_de006wa
			case "30195-18.htm": // master_lv3_de007wa
			case "30195-19.htm": // master_lv3_de007wat
			case "30195-20.htm": // master_lv3_de006wb
			case "30195-21.htm": // master_lv3_de007wb
			case "30195-22.htm": // master_lv3_de007wbt
			case "30195-23.htm": // master_lv3_de003o
			case "30195-24.htm": // master_lv3_de006oa
			case "30195-25.htm": // master_lv3_de007oa
			case "30195-26.htm": // master_lv3_de007oat
			{
				htmltext = event;
				break;
			}
			case "33":
			case "34":
			case "36":
			case "37":
			case "40":
			case "41":
			case "43":
			{
				htmltext = ClassChangeRequested(player, Integer.valueOf(event));
				break;
			}
		}
		return htmltext;
	}
	
	private String ClassChangeRequested(L2PcInstance player, int classId)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.THIRD_CLASS_GROUP))
		{
			htmltext = "30195-29.htm"; // fnYouAreThirdClass
		}
		else if ((classId == SHILLIEN_KNIGHT) && (player.getClassId() == ClassId.palusKnight))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_FATE, MARK_OF_WITCHCRAFT))
				{
					htmltext = "30195-30.htm"; // fnLowLevel11
				}
				else
				{
					htmltext = "30195-31.htm"; // fnLowLevelNoProof11
				}
			}
			else if (hasQuestItems(player, MARK_OF_DUTY, MARK_OF_FATE, MARK_OF_WITCHCRAFT))
			{
				takeItems(player, -1, MARK_OF_DUTY, MARK_OF_FATE, MARK_OF_WITCHCRAFT);
				player.setClassId(SHILLIEN_KNIGHT);
				player.setBaseClass(SHILLIEN_KNIGHT);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-32.htm"; // fnAfterClassChange11
			}
			else
			{
				htmltext = "30195-33.htm"; // fnNoProof11
			}
		}
		else if ((classId == BLADEDANCER) && (player.getClassId() == ClassId.palusKnight))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_FATE, MARK_OF_DUELIST))
				{
					htmltext = "30195-34.htm"; // fnLowLevel12
				}
				else
				{
					htmltext = "30195-35.htm"; // fnLowLevelNoProof12
				}
			}
			else if (hasQuestItems(player, MARK_OF_CHALLENGER, MARK_OF_FATE, MARK_OF_DUELIST))
			{
				takeItems(player, -1, MARK_OF_CHALLENGER, MARK_OF_FATE, MARK_OF_DUELIST);
				player.setClassId(BLADEDANCER);
				player.setBaseClass(BLADEDANCER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-36.htm"; // fnAfterClassChange12
			}
			else
			{
				htmltext = "30195-37.htm"; // fnNoProof12
			}
		}
		else if ((classId == ABYSS_WALKER) && (player.getClassId() == ClassId.assassin))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SEARCHER))
				{
					htmltext = "30195-38.htm"; // fnLowLevel21
				}
				else
				{
					htmltext = "30195-39.htm"; // fnLowLevelNoProof21
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SEARCHER))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SEARCHER);
				player.setClassId(ABYSS_WALKER);
				player.setBaseClass(ABYSS_WALKER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-40.htm"; // fnAfterClassChange21
			}
			else
			{
				htmltext = "30195-41.htm"; // fnNoProof21
			}
		}
		else if ((classId == PHANTOM_RANGER) && (player.getClassId() == ClassId.assassin))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SAGITTARIUS))
				{
					htmltext = "30195-42.htm"; // fnLowLevel22
				}
				else
				{
					htmltext = "30195-43.htm"; // fnLowLevelNoProof22
				}
			}
			else if (hasQuestItems(player, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SAGITTARIUS))
			{
				takeItems(player, -1, MARK_OF_SEEKER, MARK_OF_FATE, MARK_OF_SAGITTARIUS);
				player.setClassId(PHANTOM_RANGER);
				player.setBaseClass(PHANTOM_RANGER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-44.htm"; // fnAfterClassChange22
			}
			else
			{
				htmltext = "30195-45.htm"; // fnNoProof22
			}
		}
		else if ((classId == SPELLHOWLER) && (player.getClassId() == ClassId.darkWizard))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_MAGUS))
				{
					htmltext = "30195-46.htm"; // fnLowLevel31
				}
				else
				{
					htmltext = "30195-47.htm"; // fnLowLevelNoProof31
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_MAGUS))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_MAGUS);
				player.setClassId(SPELLHOWLER);
				player.setBaseClass(SPELLHOWLER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-48.htm"; // fnAfterClassChange31
			}
			else
			{
				htmltext = "30195-49.htm"; // fnNoProof31
			}
		}
		else if ((classId == PHANTOM_SUMMONER) && (player.getClassId() == ClassId.darkWizard))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_SUMMONER))
				{
					htmltext = "30195-50.htm"; // fnLowLevel32
				}
				else
				{
					htmltext = "30195-51.htm"; // fnLowLevelNoProof32
				}
			}
			else if (hasQuestItems(player, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_SUMMONER))
			{
				takeItems(player, -1, MARK_OF_SCHOLAR, MARK_OF_FATE, MARK_OF_SUMMONER);
				player.setClassId(PHANTOM_SUMMONER);
				player.setBaseClass(PHANTOM_SUMMONER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-52.htm"; // fnAfterClassChange32
			}
			else
			{
				htmltext = "30195-53.htm"; // fnNoProof32
			}
		}
		else if ((classId == SHILLIEN_ELDER) && (player.getClassId() == ClassId.shillienOracle))
		{
			if (player.getLevel() < MIN_LEVEL)
			{
				if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_FATE, MARK_OF_REFORMER))
				{
					htmltext = "30195-54.htm"; // fnLowLevel41
				}
				else
				{
					htmltext = "30195-55.htm"; // fnLowLevelNoProof41
				}
			}
			else if (hasQuestItems(player, MARK_OF_PILGRIM, MARK_OF_FATE, MARK_OF_REFORMER))
			{
				takeItems(player, -1, MARK_OF_PILGRIM, MARK_OF_FATE, MARK_OF_REFORMER);
				player.setClassId(SHILLIEN_ELDER);
				player.setBaseClass(SHILLIEN_ELDER);
				// SystemMessage and cast skill is done by setClassId
				player.broadcastUserInfo();
				giveItems(player, SHADOW_ITEM_EXCHANGE_COUPON_C_GRADE, 15);
				htmltext = "30195-56.htm"; // fnAfterClassChange41
			}
			else
			{
				htmltext = "30195-57.htm"; // fnNoProof41
			}
		}
		return htmltext;
	}
	
	@Override
	public String onTalk(L2Npc npc, L2PcInstance player)
	{
		String htmltext = null;
		if (player.isInCategory(CategoryType.FOURTH_CLASS_GROUP) && (player.isInCategory(CategoryType.DELF_MALL_CLASS) || player.isInCategory(CategoryType.DELF_FALL_CLASS)))
		{
			htmltext = "30195-01.htm"; // fnYouAreFourthClass
		}
		else if ((player.isInCategory(CategoryType.DELF_MALL_CLASS) || player.isInCategory(CategoryType.DELF_FALL_CLASS)))
		{
			final ClassId classId = player.getClassId();
			if ((classId == ClassId.palusKnight) || (classId == ClassId.shillienKnight) || (classId == ClassId.bladedancer))
			{
				htmltext = "30195-02.htm"; // fnClassList1
			}
			else if ((classId == ClassId.assassin) || (classId == ClassId.abyssWalker) || (classId == ClassId.phantomRanger))
			{
				htmltext = "30195-09.htm"; // fnClassList2
			}
			else if ((classId == ClassId.darkWizard) || (classId == ClassId.spellhowler) || (classId == ClassId.phantomSummoner))
			{
				htmltext = "30195-16.htm"; // fnClassList3
			}
			else if ((classId == ClassId.shillienOracle) || (classId == ClassId.shillenElder))
			{
				htmltext = "30195-23.htm"; // fnClassList4
			}
			else
			{
				htmltext = "30195-27.htm"; // fnYouAreFirstClass
			}
		}
		else
		{
			htmltext = "30195-28.htm"; // fnClassMismatch
		}
		return htmltext;
	}
	
	public static void main(String[] args)
	{
		new DarkElfChange2();
	}
}
