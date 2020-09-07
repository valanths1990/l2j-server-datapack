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
import static com.l2jserver.gameserver.model.zone.ZoneId.PVP;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.Test;

import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.instancemanager.DuelManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Duel;
import com.l2jserver.gameserver.model.skills.Skill;

/**
 * Enemy Only test.
 * @author Zoey76
 * @version 2.6.2.0
 */
@PrepareForTest(DuelManager.class)
public class EnemyOnlyTest extends AbstractTest {
	
	@Mock
	private Skill skill;
	@Mock
	private L2Character activeChar;
	@Mock
	private L2Character target;
	@Mock
	private L2PcInstance player;
	@Mock
	private L2PcInstance targetPlayer;
	@Mock
	private L2PcInstance otherPlayer;
	@Mock
	private DuelManager duelManager;
	@Mock
	private Duel duel;
	
	private final EnemyOnly enemyOnly = new EnemyOnly();
	
	@Test
	public void test_invalid_affect_scope_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(NONE);
		replay(skill);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_null_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		replay(skill);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, null);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_dead_target_should_return_empty_target_list_with_invalid_target_message() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(true);
		activeChar.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_attackable_target_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(true);
		replay(skill, target);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_null_player_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(null);
		replay(skill, target, activeChar);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_olympiad_should_return_target_if_target_is_on_the_other_side() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(targetPlayer);
		expect(player.getOlympiadSide()).andReturn(0);
		expect(targetPlayer.getOlympiadSide()).andReturn(1);
		replay(skill, target, activeChar, player, targetPlayer);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_player_in_olympiad_should_return_empty_target_list_if_target_is_on_the_same_side() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(true);
		expect(target.getActingPlayer()).andReturn(targetPlayer);
		expect(player.getOlympiadSide()).andReturn(0);
		expect(targetPlayer.getOlympiadSide()).andReturn(0);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player, targetPlayer);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_duel_should_return_target_if_target_is_on_the_other_side() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(true);
		expect(player.getDuelId()).andReturn(1);
		expect(target.getActingPlayer()).andReturn(targetPlayer);
		
		mockStatic(DuelManager.class);
		expect(DuelManager.getInstance()).andReturn(duelManager);
		expect(duelManager.getDuel(1)).andReturn(duel);
		expect(duel.getTeamA()).andReturn(List.of(player));
		expect(duel.getTeamB()).andReturn(List.of(targetPlayer));
		replay(skill, target, activeChar, player, duelManager, duel, DuelManager.class);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_player_in_duel_should_return_empty_target_list_if_target_is_on_the_same_side() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(true);
		expect(player.getDuelId()).andReturn(1);
		expect(target.getActingPlayer()).andReturn(targetPlayer);
		
		mockStatic(DuelManager.class);
		expect(DuelManager.getInstance()).andReturn(duelManager);
		expect(duelManager.getDuel(1)).andReturn(duel);
		expect(duel.getTeamA()).andReturn(List.of(player, targetPlayer));
		expect(duel.getTeamB()).andReturn(List.of(otherPlayer));
		
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player, duelManager, duel, DuelManager.class);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_party_with_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(true);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_pvp_zone_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(true);
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
	
	@Test
	public void test_player_in_clan_with_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(false);
		expect(player.isInClanWith(target)).andReturn(true);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_alliance_with_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(false);
		expect(player.isInClanWith(target)).andReturn(false);
		expect(player.isInAllyWith(target)).andReturn(true);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_in_command_channel_with_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(false);
		expect(player.isInClanWith(target)).andReturn(false);
		expect(player.isInAllyWith(target)).andReturn(false);
		expect(player.isInCommandChannelWith(target)).andReturn(true);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_cannot_pvp_target_should_return_empty_target_list() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(false);
		expect(player.isInClanWith(target)).andReturn(false);
		expect(player.isInAllyWith(target)).andReturn(false);
		expect(player.isInCommandChannelWith(target)).andReturn(false);
		expect(player.checkIfPvP(target)).andReturn(false);
		player.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(EMPTY_TARGET_LIST, actual);
	}
	
	@Test
	public void test_player_can_pvp_target_should_return_target() {
		expect(skill.getAffectScope()).andReturn(SINGLE);
		expect(target.isDead()).andReturn(false);
		expect(target.isAttackable()).andReturn(false);
		expect(activeChar.getActingPlayer()).andReturn(player);
		expect(player.isInOlympiadMode()).andReturn(false);
		expect(player.isInDuelWith(target)).andReturn(false);
		expect(player.isInPartyWith(target)).andReturn(false);
		expect(player.isInsideZone(PVP)).andReturn(false);
		expect(player.isInClanWith(target)).andReturn(false);
		expect(player.isInAllyWith(target)).andReturn(false);
		expect(player.isInCommandChannelWith(target)).andReturn(false);
		expect(player.checkIfPvP(target)).andReturn(true);
		replay(skill, target, activeChar, player);
		
		final var actual = enemyOnly.getTargetList(skill, activeChar, false, target);
		assertEquals(target, actual[0]);
	}
}
