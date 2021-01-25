/*
 * Copyright © 2004-2021 L2J DataPack
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
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_ATTACK;
import static com.l2jserver.gameserver.ai.CtrlIntention.AI_INTENTION_IDLE;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replayAll;

import java.util.Map;

import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.ai.L2CharacterAI;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * Betray instant effect test.
 * @author Zoey76
 * @version 2.6.2.0
 */
@PrepareForTest({
	BuffInfo.class,
	Formulas.class
})
public class InstantBetrayTest extends AbstractTest {
	
	@Mock
	private BuffInfo buffInfo;
	@Mock
	private L2Character effector;
	@Mock
	private L2Character effected;
	@Mock
	private L2Attackable effectedMinion;
	@Mock
	private L2Attackable effectedLeader;
	@Mock
	private Skill skill;
	@Mock
	private L2PcInstance masterPlayer;
	@Mock
	private L2CharacterAI creatureAI;
	
	private InstantBetray effect;
	
	@BeforeSuite
	void init() {
		final var set = new StatsSet(Map.of("name", "InstantBetray"));
		final var params = new StatsSet(Map.of("chance", "80", "time", "30"));
		effect = new InstantBetray(null, null, set, params);
	}
	
	@Test
	public void test_null_effected() {
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_is_raid() {
		expect(buffInfo.getEffected()).andReturn(effected);
		expect(effected.isRaid()).andReturn(true);
		replayAll();
		
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_not_servitor_summon_raid_minion() {
		expect(buffInfo.getEffected()).andReturn(effected);
		expect(effected.isRaid()).andReturn(false);
		expect(effected.isServitor()).andReturn(false);
		expect(effected.isSummon()).andReturn(false);
		expect(effected.isRaidMinion()).andReturn(false);
		replayAll();
		
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_is_servitor_probability_fail() {
		mockStatic(Formulas.class);
		expect(buffInfo.getEffected()).andReturn(effected);
		expect(buffInfo.getEffector()).andReturn(effector);
		expect(buffInfo.getSkill()).andReturn(skill);
		expect(effected.isRaid()).andReturn(false);
		expect(effected.isServitor()).andReturn(true);
		expect(effected.getActingPlayer()).andReturn(masterPlayer);
		expect(Formulas.calcProbability(80, effector, effected, skill)).andReturn(false);
		expect(effected.getAI()).andReturn(creatureAI);
		replayAll();
		
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_is_servitor() {
		mockStatic(Formulas.class);
		expect(buffInfo.getEffected()).andReturn(effected);
		expect(buffInfo.getEffector()).andReturn(effector);
		expect(buffInfo.getSkill()).andReturn(skill);
		expect(effected.isRaid()).andReturn(false);
		expect(effected.isServitor()).andReturn(true);
		expect(effected.getActingPlayer()).andReturn(masterPlayer);
		expect(Formulas.calcProbability(80, effector, effected, skill)).andReturn(true);
		expect(effected.getAI()).andReturn(creatureAI);
		creatureAI.setIntention(AI_INTENTION_ATTACK, masterPlayer);
		expectLastCall();
		creatureAI.setIntention(AI_INTENTION_IDLE, masterPlayer);
		expectLastCall();
		replayAll();
		
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_is_summon() {
		mockStatic(Formulas.class);
		expect(buffInfo.getEffected()).andReturn(effected);
		expect(buffInfo.getEffector()).andReturn(effector);
		expect(buffInfo.getSkill()).andReturn(skill);
		expect(effected.isRaid()).andReturn(false);
		expect(effected.isServitor()).andReturn(false);
		expect(effected.isSummon()).andReturn(true);
		expect(effected.getActingPlayer()).andReturn(masterPlayer);
		expect(Formulas.calcProbability(80, effector, effected, skill)).andReturn(true);
		expect(effected.getAI()).andReturn(creatureAI);
		creatureAI.setIntention(AI_INTENTION_ATTACK, masterPlayer);
		expectLastCall();
		creatureAI.setIntention(AI_INTENTION_IDLE, masterPlayer);
		expectLastCall();
		replayAll();
		
		effect.onStart(buffInfo);
	}
	
	@Test
	public void test_effected_is_raid_minion() {
		mockStatic(Formulas.class);
		expect(buffInfo.getEffected()).andReturn(effectedMinion);
		expect(buffInfo.getEffector()).andReturn(effector);
		expect(buffInfo.getSkill()).andReturn(skill);
		expect(effectedMinion.isRaid()).andReturn(false);
		expect(effectedMinion.isServitor()).andReturn(false);
		expect(effectedMinion.isSummon()).andReturn(false);
		expect(effectedMinion.isRaidMinion()).andReturn(true);
		expect(effectedMinion.getLeader()).andReturn(effectedLeader);
		expect(Formulas.calcProbability(80, effector, effectedMinion, skill)).andReturn(true);
		expect(effectedMinion.getAI()).andReturn(creatureAI);
		creatureAI.setIntention(AI_INTENTION_ATTACK, effectedLeader);
		expectLastCall();
		creatureAI.setIntention(AI_INTENTION_IDLE, effectedLeader);
		expectLastCall();
		replayAll();
		
		effect.onStart(buffInfo);
	}
}
