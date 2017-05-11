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
package ai.group_template;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.ItemChanceHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;

import ai.npc.AbstractNpcAI;

/**
 * Treasure Chest AI.
 * @author ivantotov
 */
public final class TreasureChest extends AbstractNpcAI
{
	private static final String TIMER_1 = "5001";
	private static final String TIMER_2 = "5002";
	private static final int MAX_SPAWN_TIME = 14400000;
	private static final int ATTACK_SPAWN_TIME = 5000;
	private static final int PLAYER_LEVEL_THRESHOLD = 78;
	private static final int MAESTROS_KEY_SKILL_ID = 22271;
	private static final SkillHolder[] TREASURE_BOMBS = new SkillHolder[]
	{
		new SkillHolder(4143, 1),
		new SkillHolder(4143, 2),
		new SkillHolder(4143, 3),
		new SkillHolder(4143, 4),
		new SkillHolder(4143, 5),
		new SkillHolder(4143, 6),
		new SkillHolder(4143, 7),
		new SkillHolder(4143, 8),
		new SkillHolder(4143, 9),
		new SkillHolder(4143, 10),
	};
	
	private static final Map<Integer, List<ItemChanceHolder>> DROPS = new HashMap<>();
	
	static
	{
		DROPS.put(18265, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 2703, 7), // Scroll of Escape
			new ItemChanceHolder(1061, 2365, 4), // Major Healing Potion
			new ItemChanceHolder(737, 3784, 4), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1136, 1), // Haste Potion
			new ItemChanceHolder(10261, 1136, 1), // Accuracy Juice
			new ItemChanceHolder(10262, 1136, 1), // Critical Damage Juice
			new ItemChanceHolder(10263, 1136, 1), // Critical Rate Juice
			new ItemChanceHolder(10264, 1136, 1), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1136, 1), // Evasion Juice
			new ItemChanceHolder(10266, 1136, 1), // M. Atk. Juice
			new ItemChanceHolder(10267, 1136, 1), // P. Atk. Potion
			new ItemChanceHolder(10268, 1136, 1), // Wind Walk Juice
			new ItemChanceHolder(5593, 2365, 6), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1136, 1), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1136, 1), // P. Def. Juice
			new ItemChanceHolder(10131, 4919, 1), // Transformation Scroll: Onyx Beast
			new ItemChanceHolder(10132, 4919, 1), // Transformation Scroll: Doom Wraith
			new ItemChanceHolder(10133, 4919, 1), // Transformation Scroll: Grail Apostle
			new ItemChanceHolder(1538, 3279, 1), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1230, 1), // Blessed Scroll of Resurrection
			new ItemChanceHolder(68, 2617, 1), // Falchion
			new ItemChanceHolder(21747, 320, 1))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18266, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 3159), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 2764), // Major Healing Potion
			new ItemChanceHolder(737, 4, 4422), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 1327), // Haste Potion
			new ItemChanceHolder(10261, 1, 1327), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 1327), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 1327), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 1327), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 1327), // Evasion Juice
			new ItemChanceHolder(10266, 1, 1327), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 1327), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 1327), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 2764), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 1327), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 1327), // P. Def. Juice
			new ItemChanceHolder(10131, 1, 5749), // Transformation Scroll: Onyx Beast
			new ItemChanceHolder(10132, 1, 5749), // Transformation Scroll: Doom Wraith
			new ItemChanceHolder(10133, 1, 5749), // Transformation Scroll: Grail Apostle
			new ItemChanceHolder(1538, 1, 3833), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 1438), // Blessed Scroll of Resurrection
			new ItemChanceHolder(68, 1, 3058), // Falchion
			new ItemChanceHolder(21747, 1, 374))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18267, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 3651), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 3194), // Major Healing Potion
			new ItemChanceHolder(737, 4, 5111), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 1534), // Haste Potion
			new ItemChanceHolder(10261, 1, 1534), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 1534), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 1534), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 1534), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 1534), // Evasion Juice
			new ItemChanceHolder(10266, 1, 1534), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 1534), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 1534), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 3194), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 1534), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 1534), // P. Def. Juice
			new ItemChanceHolder(10131, 1, 6644), // Transformation Scroll: Onyx Beast
			new ItemChanceHolder(10132, 1, 6644), // Transformation Scroll: Doom Wraith
			new ItemChanceHolder(10133, 1, 6644), // Transformation Scroll: Grail Apostle
			new ItemChanceHolder(1538, 1, 4429), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 1661), // Blessed Scroll of Resurrection
			new ItemChanceHolder(68, 1, 3534), // Falchion
			new ItemChanceHolder(21747, 1, 463))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18268, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 4200), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 3675), // Major Healing Potion
			new ItemChanceHolder(737, 4, 5879), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 1764), // Haste Potion
			new ItemChanceHolder(10261, 1, 1764), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 1764), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 1764), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 1764), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 1764), // Evasion Juice
			new ItemChanceHolder(10266, 1, 1764), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 1764), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 1764), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 3675), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 1764), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 1764), // P. Def. Juice
			new ItemChanceHolder(10134, 1, 5095), // Transformation Scroll: Unicorn
			new ItemChanceHolder(10135, 1, 5095), // Transformation Scroll: Lilim Knight
			new ItemChanceHolder(10136, 1, 5095), // Transformation Scroll: Golem Guardian
			new ItemChanceHolder(1538, 1, 5095), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 1911), // Blessed Scroll of Resurrection
			new ItemChanceHolder(69, 1, 1543), // Bastard Sword
			new ItemChanceHolder(21747, 1, 498))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18269, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 5010), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 4383), // Major Healing Potion
			new ItemChanceHolder(737, 4, 7013), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 2104), // Haste Potion
			new ItemChanceHolder(10261, 1, 2104), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 2104), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 2104), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 2104), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 2104), // Evasion Juice
			new ItemChanceHolder(10266, 1, 2104), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 2104), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 2104), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 4383), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 2104), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 2104), // P. Def. Juice
			new ItemChanceHolder(10134, 1, 6078), // Transformation Scroll: Unicorn
			new ItemChanceHolder(10135, 1, 6078), // Transformation Scroll: Lilim Knight
			new ItemChanceHolder(10136, 1, 6078), // Transformation Scroll: Golem Guardian
			new ItemChanceHolder(1538, 1, 6078), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 2280), // Blessed Scroll of Resurrection
			new ItemChanceHolder(69, 1, 1840), // Bastard Sword
			new ItemChanceHolder(21747, 1, 593))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18270, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 5894), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 5157), // Major Healing Potion
			new ItemChanceHolder(737, 4, 8252), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 2476), // Haste Potion
			new ItemChanceHolder(10261, 1, 2476), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 2476), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 2476), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 2476), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 2476), // Evasion Juice
			new ItemChanceHolder(10266, 1, 2476), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 2476), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 2476), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 5157), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 2476), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 2476), // P. Def. Juice
			new ItemChanceHolder(10134, 1, 7152), // Transformation Scroll: Unicorn
			new ItemChanceHolder(10135, 1, 7152), // Transformation Scroll: Lilim Knight
			new ItemChanceHolder(10136, 1, 7152), // Transformation Scroll: Golem Guardian
			new ItemChanceHolder(1538, 1, 7152), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 2682), // Blessed Scroll of Resurrection
			new ItemChanceHolder(69, 1, 2165), // Bastard Sword
			new ItemChanceHolder(21747, 1, 698))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18271, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 7, 6879), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 6019), // Major Healing Potion
			new ItemChanceHolder(737, 4, 9630), // Scroll of Resurrection
			new ItemChanceHolder(10260, 1, 2889), // Haste Potion
			new ItemChanceHolder(10261, 1, 2889), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 2889), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 2889), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 2889), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 2889), // Evasion Juice
			new ItemChanceHolder(10266, 1, 2889), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 2889), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 2889), // Wind Walk Juice
			new ItemChanceHolder(5593, 6, 6019), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 1, 2889), // SP Scroll (Mid-grade)
			new ItemChanceHolder(10269, 1, 2889), // P. Def. Juice
			new ItemChanceHolder(10134, 1, 8346), // Transformation Scroll: Unicorn
			new ItemChanceHolder(10135, 1, 8346), // Transformation Scroll: Lilim Knight
			new ItemChanceHolder(10136, 1, 8346), // Transformation Scroll: Golem Guardian
			new ItemChanceHolder(1538, 1, 8346), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 3130), // Blessed Scroll of Resurrection
			new ItemChanceHolder(69, 1, 2527), // Bastard Sword
			new ItemChanceHolder(21747, 1, 815))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18272, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 5, 6668), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 4168), // Major Healing Potion
			new ItemChanceHolder(737, 3, 2223), // Scroll of Resurrection
			new ItemChanceHolder(1539, 5, 6668), // Major Healing Potion
			new ItemChanceHolder(8625, 2, 3334), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 2874), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 3, 5557), // Elixir of CP (B-grade)
			new ItemChanceHolder(8636, 4, 5557), // Elixir of CP (C-grade)
			new ItemChanceHolder(8630, 2, 3832), // Elixir of Mind (C-grade)
			new ItemChanceHolder(8624, 2, 4631), // Elixir of Life (C-grade)
			new ItemChanceHolder(10260, 1, 5129), // Haste Potion
			new ItemChanceHolder(10261, 1, 5129), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 5129), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 5129), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 5129), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 5129), // Evasion Juice
			new ItemChanceHolder(10266, 1, 5129), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 5129), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 5129), // Wind Walk Juice
			new ItemChanceHolder(5593, 9, 7124), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 2, 6411), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 642), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 5129), // P. Def. Juice
			new ItemChanceHolder(10137, 1, 5418), // Transformation Scroll: Inferno Drake
			new ItemChanceHolder(10138, 1, 5418), // Transformation Scroll: Dragon Bomber
			new ItemChanceHolder(1538, 1, 7223), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 2709), // Blessed Scroll of Resurrection
			new ItemChanceHolder(5577, 1, 2167), // Red Soul Crystal - Stage 11
			new ItemChanceHolder(5578, 1, 2167), // Green Soul Crystal - Stage 11
			new ItemChanceHolder(5579, 1, 2167), // Blue Soul Crystal - Stage 11
			new ItemChanceHolder(70, 1, 1250), // Claymore
			new ItemChanceHolder(21747, 1, 940))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18273, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 5, 7662), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 4789), // Major Healing Potion
			new ItemChanceHolder(737, 3, 2554), // Scroll of Resurrection
			new ItemChanceHolder(1539, 5, 7662), // Major Healing Potion
			new ItemChanceHolder(8625, 2, 3831), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 3303), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 3, 6385), // Elixir of CP (B-grade)
			new ItemChanceHolder(8636, 4, 6385), // Elixir of CP (C-grade)
			new ItemChanceHolder(8630, 2, 4404), // Elixir of Mind (C-grade)
			new ItemChanceHolder(8624, 2, 5321), // Elixir of Life (C-grade)
			new ItemChanceHolder(10260, 1, 5894), // Haste Potion
			new ItemChanceHolder(10261, 1, 5894), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 5894), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 5894), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 5894), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 5894), // Evasion Juice
			new ItemChanceHolder(10266, 1, 5894), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 5894), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 5894), // Wind Walk Juice
			new ItemChanceHolder(5593, 9, 8186), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 2, 7367), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 737), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 5894), // P. Def. Juice
			new ItemChanceHolder(10137, 1, 6226), // Transformation Scroll: Inferno Drake
			new ItemChanceHolder(10138, 1, 6226), // Transformation Scroll: Dragon Bomber
			new ItemChanceHolder(1538, 1, 8301), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 3113), // Blessed Scroll of Resurrection
			new ItemChanceHolder(5577, 1, 2491), // Red Soul Crystal - Stage 11
			new ItemChanceHolder(5578, 1, 2491), // Green Soul Crystal - Stage 11
			new ItemChanceHolder(5579, 1, 2491), // Blue Soul Crystal - Stage 11
			new ItemChanceHolder(70, 1, 1437), // Claymore
			new ItemChanceHolder(21747, 1, 1080))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18274, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 5, 8719), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 5450), // Major Healing Potion
			new ItemChanceHolder(737, 3, 2907), // Scroll of Resurrection
			new ItemChanceHolder(1539, 5, 8719), // Major Healing Potion
			new ItemChanceHolder(8625, 2, 4360), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 3759), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 3, 7266), // Elixir of CP (B-grade)
			new ItemChanceHolder(8636, 4, 7266), // Elixir of CP (C-grade)
			new ItemChanceHolder(8630, 2, 5011), // Elixir of Mind (C-grade)
			new ItemChanceHolder(8624, 2, 6055), // Elixir of Life (C-grade)
			new ItemChanceHolder(10260, 1, 6707), // Haste Potion
			new ItemChanceHolder(10261, 1, 6707), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 6707), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 6707), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 6707), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 6707), // Evasion Juice
			new ItemChanceHolder(10266, 1, 6707), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 6707), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 6707), // Wind Walk Juice
			new ItemChanceHolder(5593, 9, 9315), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 2, 8384), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 839), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 6707), // P. Def. Juice
			new ItemChanceHolder(21180, 1, 7084), // Transformation Scroll: Heretic - Event
			new ItemChanceHolder(21181, 1, 5668), // Transformation Scroll: Veil Master - Event
			new ItemChanceHolder(1538, 1, 9446), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 3542), // Blessed Scroll of Resurrection
			new ItemChanceHolder(5577, 1, 2834), // Red Soul Crystal - Stage 11
			new ItemChanceHolder(5578, 1, 2834), // Green Soul Crystal - Stage 11
			new ItemChanceHolder(5579, 1, 2834), // Blue Soul Crystal - Stage 11
			new ItemChanceHolder(135, 1, 481), // Samurai Long Sword
			new ItemChanceHolder(21747, 1, 1229))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18275, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 5, 9881), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 6176), // Major Healing Potion
			new ItemChanceHolder(737, 3, 3294), // Scroll of Resurrection
			new ItemChanceHolder(1539, 5, 9881), // Major Healing Potion
			new ItemChanceHolder(8625, 2, 4941), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 4259), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 3, 8234), // Elixir of CP (B-grade)
			new ItemChanceHolder(8636, 4, 8234), // Elixir of CP (C-grade)
			new ItemChanceHolder(8630, 2, 5679), // Elixir of Mind (C-grade)
			new ItemChanceHolder(8624, 2, 6862), // Elixir of Life (C-grade)
			new ItemChanceHolder(10260, 1, 7601), // Haste Potion
			new ItemChanceHolder(10261, 1, 7601), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 7601), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 7601), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 7601), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 7601), // Evasion Juice
			new ItemChanceHolder(10266, 1, 7601), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 7601), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 7601), // Wind Walk Juice
			new ItemChanceHolder(5593, 9, 10557), // SP Scroll (Low-grade)
			new ItemChanceHolder(5594, 2, 9501), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 951), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 7601), // P. Def. Juice
			new ItemChanceHolder(21180, 1, 8028), // Transformation Scroll: Heretic - Event
			new ItemChanceHolder(21181, 1, 6423), // Transformation Scroll: Veil Master - Event
			new ItemChanceHolder(1538, 1, 10704), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4014), // Blessed Scroll of Resurrection
			new ItemChanceHolder(5577, 1, 3212), // Red Soul Crystal - Stage 11
			new ItemChanceHolder(5578, 1, 3212), // Green Soul Crystal - Stage 11
			new ItemChanceHolder(5579, 1, 3212), // Blue Soul Crystal - Stage 11
			new ItemChanceHolder(135, 1, 546), // Samurai Long Sword
			new ItemChanceHolder(21747, 1, 1393))); // Novice Adventurer's Treasure Sack
		
		DROPS.put(18276, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 8, 7727), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 7727), // Major Healing Potion
			new ItemChanceHolder(737, 3, 4121), // Scroll of Resurrection
			new ItemChanceHolder(8625, 2, 6182), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 5329), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 4, 7727), // Elixir of CP (B-grade)
			new ItemChanceHolder(8638, 3, 8242), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 4293), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 2, 4945), // Elixir of Life (A-grade)
			new ItemChanceHolder(10260, 1, 4451), // Haste Potion
			new ItemChanceHolder(10261, 1, 4451), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 4451), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 4451), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 4451), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 4451), // Evasion Juice
			new ItemChanceHolder(10266, 1, 4451), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 4451), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 4451), // Wind Walk Juice
			new ItemChanceHolder(5594, 2, 5563), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 557), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 4451), // P. Def. Juice
			new ItemChanceHolder(8736, 1, 6439), // Mid-grade Life Stone - Lv. 55
			new ItemChanceHolder(8737, 1, 5563), // Mid-grade Life Stone - Lv. 58
			new ItemChanceHolder(8738, 1, 4636), // Mid-grade Life Stone - Lv. 61
			new ItemChanceHolder(21182, 1, 5786), // Transformation Scroll: Saber Tooth Tiger - Event
			new ItemChanceHolder(21183, 1, 4822), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(1538, 2, 4822), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 3616), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9648, 1, 670), // Transformation Sealbook: Onyx Beast
			new ItemChanceHolder(9649, 1, 804), // Transformation Sealbook: Doom Wraith
			new ItemChanceHolder(5580, 1, 145), // Red Soul Crystal - Stage 12
			new ItemChanceHolder(5581, 1, 145), // Green Soul Crystal - Stage 12
			new ItemChanceHolder(5582, 1, 145), // Blue Soul Crystal - Stage 12
			new ItemChanceHolder(142, 1, 217), // Keshanberk
			new ItemChanceHolder(21748, 1, 92))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18277, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 8, 8657), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 8657), // Major Healing Potion
			new ItemChanceHolder(737, 3, 4617), // Scroll of Resurrection
			new ItemChanceHolder(8625, 2, 6926), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 5971), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 4, 8657), // Elixir of CP (B-grade)
			new ItemChanceHolder(8638, 3, 9234), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 4810), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 2, 5541), // Elixir of Life (A-grade)
			new ItemChanceHolder(10260, 1, 4987), // Haste Potion
			new ItemChanceHolder(10261, 1, 4987), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 4987), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 4987), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 4987), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 4987), // Evasion Juice
			new ItemChanceHolder(10266, 1, 4987), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 4987), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 4987), // Wind Walk Juice
			new ItemChanceHolder(5594, 2, 6233), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 624), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 4987), // P. Def. Juice
			new ItemChanceHolder(8736, 1, 7214), // Mid-grade Life Stone - Lv. 55
			new ItemChanceHolder(8737, 1, 6233), // Mid-grade Life Stone - Lv. 58
			new ItemChanceHolder(8738, 1, 5195), // Mid-grade Life Stone - Lv. 61
			new ItemChanceHolder(21183, 1, 5402), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(21184, 1, 5402), // Transformation Scroll: Doll Blader - Event
			new ItemChanceHolder(1538, 2, 5402), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4052), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9648, 1, 751), // Transformation Sealbook: Onyx Beast
			new ItemChanceHolder(9649, 1, 901), // Transformation Sealbook: Doom Wraith
			new ItemChanceHolder(5580, 1, 163), // Red Soul Crystal - Stage 12
			new ItemChanceHolder(5581, 1, 163), // Green Soul Crystal - Stage 12
			new ItemChanceHolder(5582, 1, 163), // Blue Soul Crystal - Stage 12
			new ItemChanceHolder(79, 1, 161), // Damascus Sword
			new ItemChanceHolder(21748, 1, 103))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18278, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(736, 8, 9646), // Scroll of Escape
			new ItemChanceHolder(1061, 4, 9646), // Major Healing Potion
			new ItemChanceHolder(737, 3, 5145), // Scroll of Resurrection
			new ItemChanceHolder(8625, 2, 7717), // Elixir of Life (B-grade)
			new ItemChanceHolder(8631, 2, 6652), // Elixir of Mind (B-grade)
			new ItemChanceHolder(8637, 4, 9646), // Elixir of CP (B-grade)
			new ItemChanceHolder(8638, 3, 10289), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 5359), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 2, 6173), // Elixir of Life (A-grade)
			new ItemChanceHolder(10260, 1, 5556), // Haste Potion
			new ItemChanceHolder(10261, 1, 5556), // Accuracy Juice
			new ItemChanceHolder(10262, 1, 5556), // Critical Damage Juice
			new ItemChanceHolder(10263, 1, 5556), // Critical Rate Juice
			new ItemChanceHolder(10264, 1, 5556), // Casting Spd. Juice
			new ItemChanceHolder(10265, 1, 5556), // Evasion Juice
			new ItemChanceHolder(10266, 1, 5556), // M. Atk. Juice
			new ItemChanceHolder(10267, 1, 5556), // P. Atk. Potion
			new ItemChanceHolder(10268, 1, 5556), // Wind Walk Juice
			new ItemChanceHolder(5594, 2, 6945), // SP Scroll (Mid-grade)
			new ItemChanceHolder(5595, 1, 695), // SP Scroll (High-grade)
			new ItemChanceHolder(10269, 1, 5556), // P. Def. Juice
			new ItemChanceHolder(8736, 1, 8038), // Mid-grade Life Stone - Lv. 55
			new ItemChanceHolder(8737, 1, 6945), // Mid-grade Life Stone - Lv. 58
			new ItemChanceHolder(8738, 1, 5788), // Mid-grade Life Stone - Lv. 61
			new ItemChanceHolder(21183, 1, 6019), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(21184, 1, 6019), // Transformation Scroll: Doll Blader - Event
			new ItemChanceHolder(1538, 2, 6019), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4514), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9648, 1, 836), // Transformation Sealbook: Onyx Beast
			new ItemChanceHolder(9649, 1, 1004), // Transformation Sealbook: Doom Wraith
			new ItemChanceHolder(5580, 1, 181), // Red Soul Crystal - Stage 12
			new ItemChanceHolder(5581, 1, 181), // Green Soul Crystal - Stage 12
			new ItemChanceHolder(5582, 1, 181), // Blue Soul Crystal - Stage 12
			new ItemChanceHolder(79, 1, 179), // Damascus Sword
			new ItemChanceHolder(21748, 1, 115))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18279, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 5714), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 5102), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 5, 5714), // Elixir of CP (S-grade)
			new ItemChanceHolder(8638, 6, 5714), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 5953), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 3, 4572), // Elixir of Life (A-grade)
			new ItemChanceHolder(729, 1, 96), // Scroll: Enchant Weapon (A-grade)
			new ItemChanceHolder(730, 1, 715), // Scroll: Enchant Armor (A-grade)
			new ItemChanceHolder(1540, 4, 4286), // Quick Healing Potion
			new ItemChanceHolder(10260, 3, 1929), // Haste Potion
			new ItemChanceHolder(10261, 3, 1929), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 1929), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 1929), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 1929), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 1929), // Evasion Juice
			new ItemChanceHolder(10266, 3, 1929), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 1929), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 1929), // Wind Walk Juice
			new ItemChanceHolder(5595, 1, 724), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 724), // SP Scroll (Top-grade)
			new ItemChanceHolder(10269, 3, 1929), // P. Def. Juice
			new ItemChanceHolder(8739, 1, 4822), // Mid-grade Life Stone - Lv. 64
			new ItemChanceHolder(8740, 1, 4018), // Mid-grade Life Stone - Lv. 67
			new ItemChanceHolder(8741, 1, 3349), // Mid-grade Life Stone - Lv. 70
			new ItemChanceHolder(8742, 1, 3014), // Mid-grade Life Stone - Lv. 76
			new ItemChanceHolder(21180, 1, 9117), // Transformation Scroll: Heretic - Event
			new ItemChanceHolder(21181, 1, 7294), // Transformation Scroll: Veil Master - Event
			new ItemChanceHolder(21182, 1, 7294), // Transformation Scroll: Saber Tooth Tiger - Event
			new ItemChanceHolder(1538, 2, 6078), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4559), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9654, 1, 845), // Transformation Sealbook: Inferno Drake
			new ItemChanceHolder(9655, 1, 845), // Transformation Sealbook: Dragon Bomber
			new ItemChanceHolder(5580, 1, 183), // Red Soul Crystal - Stage 12
			new ItemChanceHolder(5581, 1, 183), // Green Soul Crystal - Stage 12
			new ItemChanceHolder(5582, 1, 183), // Blue Soul Crystal - Stage 12
			new ItemChanceHolder(80, 1, 130), // Tallum Blade
			new ItemChanceHolder(21748, 1, 128))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18280, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 6323), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 5646), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 5, 6323), // Elixir of CP (S-grade)
			new ItemChanceHolder(8638, 6, 6323), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 6587), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 3, 5059), // Elixir of Life (A-grade)
			new ItemChanceHolder(729, 1, 106), // Scroll: Enchant Weapon (A-grade)
			new ItemChanceHolder(730, 1, 791), // Scroll: Enchant Armor (A-grade)
			new ItemChanceHolder(1540, 4, 4742), // Quick Healing Potion
			new ItemChanceHolder(10260, 3, 2134), // Haste Potion
			new ItemChanceHolder(10261, 3, 2134), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 2134), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 2134), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 2134), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 2134), // Evasion Juice
			new ItemChanceHolder(10266, 3, 2134), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 2134), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 2134), // Wind Walk Juice
			new ItemChanceHolder(5595, 1, 801), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 801), // SP Scroll (Top-grade)
			new ItemChanceHolder(10269, 3, 2134), // P. Def. Juice
			new ItemChanceHolder(8739, 1, 5335), // Mid-grade Life Stone - Lv. 64
			new ItemChanceHolder(8740, 1, 4446), // Mid-grade Life Stone - Lv. 67
			new ItemChanceHolder(8741, 1, 3705), // Mid-grade Life Stone - Lv. 70
			new ItemChanceHolder(8742, 1, 3335), // Mid-grade Life Stone - Lv. 76
			new ItemChanceHolder(21180, 1, 10088), // Transformation Scroll: Heretic - Event
			new ItemChanceHolder(21181, 1, 8070), // Transformation Scroll: Veil Master - Event
			new ItemChanceHolder(21182, 1, 8070), // Transformation Scroll: Saber Tooth Tiger - Event
			new ItemChanceHolder(1538, 2, 6725), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 5044), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9654, 1, 935), // Transformation Sealbook: Inferno Drake
			new ItemChanceHolder(9655, 1, 935), // Transformation Sealbook: Dragon Bomber
			new ItemChanceHolder(5580, 1, 202), // Red Soul Crystal - Stage 12
			new ItemChanceHolder(5581, 1, 202), // Green Soul Crystal - Stage 12
			new ItemChanceHolder(5582, 1, 202), // Blue Soul Crystal - Stage 12
			new ItemChanceHolder(80, 1, 144), // Tallum Blade
			new ItemChanceHolder(21748, 1, 141))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18281, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 6967), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 6220), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 5, 6967), // Elixir of CP (S-grade)
			new ItemChanceHolder(8638, 6, 6967), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 7257), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 3, 5573), // Elixir of Life (A-grade)
			new ItemChanceHolder(729, 1, 117), // Scroll: Enchant Weapon (A-grade)
			new ItemChanceHolder(730, 1, 871), // Scroll: Enchant Armor (A-grade)
			new ItemChanceHolder(1540, 4, 5225), // Quick Healing Potion
			new ItemChanceHolder(10260, 3, 2352), // Haste Potion
			new ItemChanceHolder(10261, 3, 2352), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 2352), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 2352), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 2352), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 2352), // Evasion Juice
			new ItemChanceHolder(10266, 3, 2352), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 2352), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 2352), // Wind Walk Juice
			new ItemChanceHolder(5595, 1, 882), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 882), // SP Scroll (Top-grade)
			new ItemChanceHolder(10269, 3, 2352), // P. Def. Juice
			new ItemChanceHolder(8739, 1, 5878), // Mid-grade Life Stone - Lv. 64
			new ItemChanceHolder(8740, 1, 4899), // Mid-grade Life Stone - Lv. 67
			new ItemChanceHolder(8741, 1, 4082), // Mid-grade Life Stone - Lv. 70
			new ItemChanceHolder(8742, 1, 3674), // Mid-grade Life Stone - Lv. 76
			new ItemChanceHolder(21183, 1, 7410), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(21184, 1, 7410), // Transformation Scroll: Doll Blader - Event
			new ItemChanceHolder(21185, 1, 3705), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(1538, 2, 7410), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 5558), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9654, 1, 1030), // Transformation Sealbook: Inferno Drake
			new ItemChanceHolder(9655, 1, 1030), // Transformation Sealbook: Dragon Bomber
			new ItemChanceHolder(5908, 1, 112), // Red Soul Crystal: Stage 13
			new ItemChanceHolder(5911, 1, 112), // Green Soul Crystal - Stage 13
			new ItemChanceHolder(5914, 1, 112), // Blue Soul Crystal: Stage 13
			new ItemChanceHolder(6364, 1, 52), // Forgotten Blade
			new ItemChanceHolder(21748, 1, 156))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18282, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 7649), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 6829), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 5, 7649), // Elixir of CP (S-grade)
			new ItemChanceHolder(8638, 6, 7649), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 7968), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 3, 6119), // Elixir of Life (A-grade)
			new ItemChanceHolder(729, 1, 128), // Scroll: Enchant Weapon (A-grade)
			new ItemChanceHolder(730, 1, 957), // Scroll: Enchant Armor (A-grade)
			new ItemChanceHolder(1540, 4, 5737), // Quick Healing Potion
			new ItemChanceHolder(10260, 3, 2582), // Haste Potion
			new ItemChanceHolder(10261, 3, 2582), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 2582), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 2582), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 2582), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 2582), // Evasion Juice
			new ItemChanceHolder(10266, 3, 2582), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 2582), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 2582), // Wind Walk Juice
			new ItemChanceHolder(5595, 1, 968), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 968), // SP Scroll (Top-grade)
			new ItemChanceHolder(10269, 3, 2582), // P. Def. Juice
			new ItemChanceHolder(8739, 1, 6454), // Mid-grade Life Stone - Lv. 64
			new ItemChanceHolder(8740, 1, 5378), // Mid-grade Life Stone - Lv. 67
			new ItemChanceHolder(8741, 1, 4482), // Mid-grade Life Stone - Lv. 70
			new ItemChanceHolder(8742, 1, 4034), // Mid-grade Life Stone - Lv. 76
			new ItemChanceHolder(21183, 1, 8136), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(21184, 1, 8136), // Transformation Scroll: Doll Blader - Event
			new ItemChanceHolder(21185, 1, 4068), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(1538, 2, 8136), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 6102), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9654, 1, 1130), // Transformation Sealbook: Inferno Drake
			new ItemChanceHolder(9655, 1, 1130), // Transformation Sealbook: Dragon Bomber
			new ItemChanceHolder(5908, 1, 123), // Red Soul Crystal: Stage 13
			new ItemChanceHolder(5911, 1, 123), // Green Soul Crystal - Stage 13
			new ItemChanceHolder(5914, 1, 123), // Blue Soul Crystal: Stage 13
			new ItemChanceHolder(6364, 1, 58), // Forgotten Blade
			new ItemChanceHolder(21748, 1, 171))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18283, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 8366), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 7470), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 5, 8366), // Elixir of CP (S-grade)
			new ItemChanceHolder(8638, 6, 8366), // Elixir of CP (A-grade)
			new ItemChanceHolder(8632, 2, 8715), // Elixir of Mind (A-grade)
			new ItemChanceHolder(8626, 3, 6693), // Elixir of Life (A-grade)
			new ItemChanceHolder(729, 1, 140), // Scroll: Enchant Weapon (A-grade)
			new ItemChanceHolder(730, 1, 1046), // Scroll: Enchant Armor (A-grade)
			new ItemChanceHolder(1540, 4, 6275), // Quick Healing Potion
			new ItemChanceHolder(10260, 3, 2824), // Haste Potion
			new ItemChanceHolder(10261, 3, 2824), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 2824), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 2824), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 2824), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 2824), // Evasion Juice
			new ItemChanceHolder(10266, 3, 2824), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 2824), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 2824), // Wind Walk Juice
			new ItemChanceHolder(5595, 1, 1059), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 1059), // SP Scroll (Top-grade)
			new ItemChanceHolder(10269, 3, 2824), // P. Def. Juice
			new ItemChanceHolder(8739, 1, 7059), // Mid-grade Life Stone - Lv. 64
			new ItemChanceHolder(8740, 1, 5883), // Mid-grade Life Stone - Lv. 67
			new ItemChanceHolder(8741, 1, 4902), // Mid-grade Life Stone - Lv. 70
			new ItemChanceHolder(8742, 1, 4412), // Mid-grade Life Stone - Lv. 76
			new ItemChanceHolder(21183, 1, 8898), // Transformation Scroll: Oel Mahum - Event
			new ItemChanceHolder(21184, 1, 8898), // Transformation Scroll: Doll Blader - Event
			new ItemChanceHolder(21185, 1, 4449), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(1538, 2, 8898), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 6674), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9654, 1, 1236), // Transformation Sealbook: Inferno Drake
			new ItemChanceHolder(9655, 1, 1236), // Transformation Sealbook: Dragon Bomber
			new ItemChanceHolder(5908, 1, 134), // Red Soul Crystal: Stage 13
			new ItemChanceHolder(5911, 1, 134), // Green Soul Crystal - Stage 13
			new ItemChanceHolder(5914, 1, 134), // Blue Soul Crystal: Stage 13
			new ItemChanceHolder(6364, 1, 63), // Forgotten Blade
			new ItemChanceHolder(21748, 1, 187))); // Experienced Adventurer's Treasure Sack
		
		DROPS.put(18284, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 6836), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 6103), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 4, 10000), // Elixir of CP (S-grade)
			new ItemChanceHolder(9546, 1, 821), // Fire Stone
			new ItemChanceHolder(9547, 1, 821), // Water Stone
			new ItemChanceHolder(9548, 1, 821), // Earth Stone
			new ItemChanceHolder(9549, 1, 821), // Wind Stone
			new ItemChanceHolder(9550, 1, 821), // Dark Stone
			new ItemChanceHolder(9551, 1, 821), // Holy Stone
			new ItemChanceHolder(959, 1, 42), // Scroll: Enchant Weapon (S-grade)
			new ItemChanceHolder(960, 1, 411), // Scroll: Enchant Armor (S-grade)
			new ItemChanceHolder(14701, 2, 2051), // Superior Quick Healing Potion
			new ItemChanceHolder(10260, 3, 3076), // Haste Potion
			new ItemChanceHolder(10261, 3, 3076), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 3076), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 3076), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 3076), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 3076), // Evasion Juice
			new ItemChanceHolder(10266, 3, 3076), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 3076), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 3076), // Wind Walk Juice
			new ItemChanceHolder(5595, 2, 577), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 231), // SP Scroll (Top-grade)
			new ItemChanceHolder(17185, 1, 116), // Scroll: 10,000 SP
			new ItemChanceHolder(10269, 3, 3076), // P. Def. Juice
			new ItemChanceHolder(9574, 1, 4006), // Mid-grade Life Stone - Lv. 80
			new ItemChanceHolder(10484, 1, 3338), // Mid-grade Life Stone - Lv. 82
			new ItemChanceHolder(14167, 1, 2783), // Mid-grade Life Stone - Lv. 84
			new ItemChanceHolder(21185, 1, 2539), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(21186, 1, 1524), // Transformation Scroll: Anakim - Event
			new ItemChanceHolder(21187, 1, 2177), // Transformation Scroll: Venom - Event
			new ItemChanceHolder(21188, 1, 2177), // Transformation Scroll: Gordon - Event
			new ItemChanceHolder(21189, 1, 2177), // Transformation Scroll: Ranku - Event
			new ItemChanceHolder(21190, 1, 2177), // Transformation Scroll: Kechi - Event
			new ItemChanceHolder(21191, 1, 2177), // Transformation Scroll: Demon Prince - Event
			new ItemChanceHolder(9552, 1, 191), // Fire Crystal
			new ItemChanceHolder(9553, 1, 191), // Water Crystal
			new ItemChanceHolder(9554, 1, 191), // Earth Crystal
			new ItemChanceHolder(9555, 1, 191), // Wind Crystal
			new ItemChanceHolder(9556, 1, 191), // Dark Crystal
			new ItemChanceHolder(9557, 1, 191), // Holy Crystal
			new ItemChanceHolder(6622, 1, 3047), // Lesser Giant's Codex
			new ItemChanceHolder(9627, 1, 191), // Lesser Giant's Codex - Mastery
			new ItemChanceHolder(1538, 2, 5078), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 3809), // Blessed Scroll of Resurrection
			new ItemChanceHolder(9570, 1, 39), // Red Soul Crystal - Stage 14
			new ItemChanceHolder(9572, 1, 39), // Green Soul Crystal - Stage 14
			new ItemChanceHolder(9571, 1, 39), // Blue Soul Crystal - Stage 14
			new ItemChanceHolder(9442, 1, 21), // Dynasty Sword
			new ItemChanceHolder(21749, 1, 25))); // Great Adventurer's Treasure Sack
		
		DROPS.put(18285, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 7420), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 6625), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 4, 10000), // Elixir of CP (S-grade)
			new ItemChanceHolder(9546, 1, 891), // Fire Stone
			new ItemChanceHolder(9547, 1, 891), // Water Stone
			new ItemChanceHolder(9548, 1, 891), // Earth Stone
			new ItemChanceHolder(9549, 1, 891), // Wind Stone
			new ItemChanceHolder(9550, 1, 891), // Dark Stone
			new ItemChanceHolder(9551, 1, 891), // Holy Stone
			new ItemChanceHolder(959, 1, 45), // Scroll: Enchant Weapon (S-grade)
			new ItemChanceHolder(960, 1, 446), // Scroll: Enchant Armor (S-grade)
			new ItemChanceHolder(14701, 2, 2226), // Superior Quick Healing Potion
			new ItemChanceHolder(10260, 3, 3339), // Haste Potion
			new ItemChanceHolder(10261, 3, 3339), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 3339), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 3339), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 3339), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 3339), // Evasion Juice
			new ItemChanceHolder(10266, 3, 3339), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 3339), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 3339), // Wind Walk Juice
			new ItemChanceHolder(5595, 2, 627), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 251), // SP Scroll (Top-grade)
			new ItemChanceHolder(17185, 1, 126), // Scroll: 10,000 SP
			new ItemChanceHolder(10269, 3, 3339), // P. Def. Juice
			new ItemChanceHolder(9574, 1, 4348), // Mid-grade Life Stone - Lv. 80
			new ItemChanceHolder(10484, 1, 3623), // Mid-grade Life Stone - Lv. 82
			new ItemChanceHolder(14167, 1, 3021), // Mid-grade Life Stone - Lv. 84
			new ItemChanceHolder(21185, 1, 2756), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(21186, 1, 1654), // Transformation Scroll: Anakim - Event
			new ItemChanceHolder(21187, 1, 2363), // Transformation Scroll: Venom - Event
			new ItemChanceHolder(21188, 1, 2363), // Transformation Scroll: Gordon - Event
			new ItemChanceHolder(21189, 1, 2363), // Transformation Scroll: Ranku - Event
			new ItemChanceHolder(21190, 1, 2363), // Transformation Scroll: Kechi - Event
			new ItemChanceHolder(21191, 1, 2363), // Transformation Scroll: Demon Prince - Event
			new ItemChanceHolder(9552, 1, 207), // Fire Crystal
			new ItemChanceHolder(9553, 1, 207), // Water Crystal
			new ItemChanceHolder(9554, 1, 207), // Earth Crystal
			new ItemChanceHolder(9555, 1, 207), // Wind Crystal
			new ItemChanceHolder(9556, 1, 207), // Dark Crystal
			new ItemChanceHolder(9557, 1, 207), // Holy Crystal
			new ItemChanceHolder(6622, 1, 3308), // Lesser Giant's Codex
			new ItemChanceHolder(9627, 1, 207), // Lesser Giant's Codex - Mastery
			new ItemChanceHolder(1538, 2, 5512), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4134), // Blessed Scroll of Resurrection
			new ItemChanceHolder(10480, 1, 21), // Red Soul Crystal - Stage 15
			new ItemChanceHolder(10482, 1, 21), // Green Soul Crystal - Stage 15
			new ItemChanceHolder(10481, 1, 21), // Blue Soul Crystal - Stage 15
			new ItemChanceHolder(10215, 1, 16), // Icarus Sawsword
			new ItemChanceHolder(21749, 1, 27))); // Great Adventurer's Treasure Sack
		
		DROPS.put(18286, Arrays.asList( // Treasure Chest
			new ItemChanceHolder(8627, 2, 8005), // Elixir of Life (S-grade)
			new ItemChanceHolder(8633, 2, 7147), // Elixir of Mind (S-grade)
			new ItemChanceHolder(8639, 4, 10000), // Elixir of CP (S-grade)
			new ItemChanceHolder(9546, 1, 961), // Fire Stone
			new ItemChanceHolder(9547, 1, 961), // Water Stone
			new ItemChanceHolder(9548, 1, 961), // Earth Stone
			new ItemChanceHolder(9549, 1, 961), // Wind Stone
			new ItemChanceHolder(9550, 1, 961), // Dark Stone
			new ItemChanceHolder(9551, 1, 961), // Holy Stone
			new ItemChanceHolder(959, 1, 49), // Scroll: Enchant Weapon (S-grade)
			new ItemChanceHolder(960, 1, 481), // Scroll: Enchant Armor (S-grade)
			new ItemChanceHolder(14701, 2, 2402), // Superior Quick Healing Potion
			new ItemChanceHolder(10260, 3, 3602), // Haste Potion
			new ItemChanceHolder(10261, 3, 3602), // Accuracy Juice
			new ItemChanceHolder(10262, 3, 3602), // Critical Damage Juice
			new ItemChanceHolder(10263, 3, 3602), // Critical Rate Juice
			new ItemChanceHolder(10264, 3, 3602), // Casting Spd. Juice
			new ItemChanceHolder(10265, 3, 3602), // Evasion Juice
			new ItemChanceHolder(10266, 3, 3602), // M. Atk. Juice
			new ItemChanceHolder(10267, 3, 3602), // P. Atk. Potion
			new ItemChanceHolder(10268, 3, 3602), // Wind Walk Juice
			new ItemChanceHolder(5595, 2, 676), // SP Scroll (High-grade)
			new ItemChanceHolder(9898, 1, 271), // SP Scroll (Top-grade)
			new ItemChanceHolder(17185, 1, 136), // Scroll: 10,000 SP
			new ItemChanceHolder(10269, 3, 3602), // P. Def. Juice
			new ItemChanceHolder(9574, 1, 4690), // Mid-grade Life Stone - Lv. 80
			new ItemChanceHolder(10484, 1, 3909), // Mid-grade Life Stone - Lv. 82
			new ItemChanceHolder(14167, 1, 3259), // Mid-grade Life Stone - Lv. 84
			new ItemChanceHolder(21185, 1, 2973), // Transformation Scroll: Zaken - Event
			new ItemChanceHolder(21186, 1, 1784), // Transformation Scroll: Anakim - Event
			new ItemChanceHolder(21187, 1, 2549), // Transformation Scroll: Venom - Event
			new ItemChanceHolder(21188, 1, 2549), // Transformation Scroll: Gordon - Event
			new ItemChanceHolder(21189, 1, 2549), // Transformation Scroll: Ranku - Event
			new ItemChanceHolder(21190, 1, 2549), // Transformation Scroll: Kechi - Event
			new ItemChanceHolder(21191, 1, 2549), // Transformation Scroll: Demon Prince - Event
			new ItemChanceHolder(9552, 1, 223), // Fire Crystal
			new ItemChanceHolder(9553, 1, 223), // Water Crystal
			new ItemChanceHolder(9554, 1, 223), // Earth Crystal
			new ItemChanceHolder(9555, 1, 223), // Wind Crystal
			new ItemChanceHolder(9556, 1, 223), // Dark Crystal
			new ItemChanceHolder(9557, 1, 223), // Holy Crystal
			new ItemChanceHolder(6622, 1, 3568), // Lesser Giant's Codex
			new ItemChanceHolder(9627, 1, 223), // Lesser Giant's Codex - Mastery
			new ItemChanceHolder(1538, 2, 5946), // Blessed Scroll of Escape
			new ItemChanceHolder(3936, 1, 4460), // Blessed Scroll of Resurrection
			new ItemChanceHolder(13071, 1, 12), // Red Soul Crystal - Stage 16
			new ItemChanceHolder(13073, 1, 12), // Green Soul Crystal - Stage 16
			new ItemChanceHolder(13072, 1, 12), // Blue Soul Crystal - Stage 16
			new ItemChanceHolder(13457, 1, 13), // Vesper Cutter
			new ItemChanceHolder(21749, 1, 29))); // Great Adventurer's Treasure Sack
	}
	
	private TreasureChest()
	{
		super(TreasureChest.class.getSimpleName(), "ai/group_template");
		
		addSpawnId(DROPS.keySet());
		addAttackId(DROPS.keySet());
	}
	
	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		switch (event)
		{
			case TIMER_1:
			case TIMER_2:
			{
				npc.deleteMe();
				break;
			}
		}
		return super.onAdvEvent(event, npc, player);
	}
	
	@Override
	public String onSpawn(L2Npc npc)
	{
		// TODO(Zoey76): Disable Core AI.
		npc.getVariables().set("MAESTRO_SKILL_USED", 0);
		startQuestTimer(TIMER_2, MAX_SPAWN_TIME, npc, null);
		return super.onSpawn(npc);
	}
	
	@Override
	public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon, Skill skill)
	{
		if (attacker.getLevel() < PLAYER_LEVEL_THRESHOLD)
		{
			npc.getVariables().set("MAX_LEVEL_DIFFERENCE", 6);
		}
		else
		{
			npc.getVariables().set("MAX_LEVEL_DIFFERENCE", 5);
		}
		
		if (npc.getVariables().getInt("MAESTRO_SKILL_USED") == 0)
		{
			if ((skill != null) && (skill.getId() == MAESTROS_KEY_SKILL_ID))
			{
				npc.getVariables().set("MAESTRO_SKILL_USED", 1);
				startQuestTimer(TIMER_1, ATTACK_SPAWN_TIME, npc, null);
				
				if ((npc.getLevel() - npc.getVariables().getInt("MAX_LEVEL_DIFFERENCE")) > attacker.getLevel())
				{
					addSkillCastDesire(npc, attacker, TREASURE_BOMBS[npc.getLevel() / 10], 1000000);
				}
				else
				{
					if (getRandom(100) < 10)
					{
						npc.doDie(null);
						
						final List<ItemChanceHolder> items = DROPS.get(npc.getId());
						if (items == null)
						{
							_log.warning("Tresure Chest ID " + npc.getId() + " doesn't have a drop list!");
						}
						else
						{
							for (ItemChanceHolder item : items)
							{
								if (getRandom(10000) < item.getChance())
								{
									npc.dropItem(attacker, item.getId(), item.getCount());
								}
							}
						}
					}
					else
					{
						addSkillCastDesire(npc, attacker, TREASURE_BOMBS[npc.getLevel() / 10], 1000000);
					}
				}
			}
			else
			{
				if (getRandom(100) < 30)
				{
					attacker.sendPacket(SystemMessageId.IF_YOU_HAVE_A_MAESTROS_KEY_YOU_CAN_USE_IT_TO_OPEN_THE_TREASURE_CHEST);
				}
			}
		}
		return super.onAttack(npc, attacker, damage, isSummon);
	}
	
	public static void main(String[] args)
	{
		new TreasureChest();
	}
}
