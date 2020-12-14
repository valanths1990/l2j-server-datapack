/*
 * Copyright © 2004-2020 L2J DataPack
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
package com.l2jserver.datapack.handlers.targethandlers;

import static com.l2jserver.gameserver.handler.ITargetTypeHandler.EMPTY_TARGET_LIST;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.NONE;
import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.testng.Assert.assertEquals;

import org.powermock.api.easymock.annotation.Mock;
import org.testng.annotations.Test;

import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillUseHolder;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Enemy test.
 * @author Zoey76
 * @version 2.6.2.0
 */
public class EnemyTest extends AbstractTest {
	
	@Mock
	private Skill skill;
	@Mock
	private SkillUseHolder skillUseHolder;
	@Mock
	private L2Character activeChar;
	@Mock
	private L2Character target;
	@Mock
	private L2PcInstance player;
	
	private final Enemy enemy = new Enemy();
	
	@Test
	public void test_invalid_affect_scope_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(NONE);
		replay(skill);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_null_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		replay(skill);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, null);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_dead_target_should_return_empty_target_list_with_invalid_target_message() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(true);
		activeChar.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_attackable_target_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(true);
		replay(skill, target);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_null_player_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(null);
		replay(skill, target, activeChar);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_cannot_pvp_target_and_no_ctrl_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.checkIfPvP(target)).andReturn(false);
		expect(player.getCurrentSkill()).andReturn(skillUseHolder);
		expect(skillUseHolder.isCtrlPressed()).andReturn(false);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player, skillUseHolder);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_cannot_pvp_target_and_ctrl_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.checkIfPvP(target)).andReturn(false);
		expect(player.getCurrentSkill()).andReturn(skillUseHolder);
		expect(skillUseHolder.isCtrlPressed()).andReturn(true);
		replay(skill, target, activeChar, player, skillUseHolder);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_player_can_pvp_target_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.checkIfPvP(target)).andReturn(true);
		replay(skill, target, activeChar, player);
		
		final var actual = enemy.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
}
