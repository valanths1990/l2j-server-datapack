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
package com.l2jserver.datapack.handlers.targethandlers;

import static com.l2jserver.gameserver.handler.ITargetTypeHandler.EMPTY_TARGET_LIST;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.testng.Assert.assertEquals;

import java.util.List;

import org.powermock.api.easymock.annotation.Mock;
import org.testng.annotations.Test;

import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.AffectScope;

/**
 * Enemy Not test.
 * @author Zoey76
 * @version 2.6.2.0
 */
public class EnemyNotTest extends AbstractTest {
	
	@Mock
	private Skill skill;
	@Mock
	private L2Character activeChar;
	@Mock
	private L2Character target;
	@Mock
	private AffectScope affectScope;
	
	private final EnemyNot handler = new EnemyNot();
	
	@Test
	public void test_target_null() {
		assertEquals(handler.getTargetList(skill, activeChar, false, null), EMPTY_TARGET_LIST);
	}
	
	@Test
	public void test_target_is_dead() {
		expect(target.isDead()).andReturn(true);
		activeChar.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replayAll();
		
		assertEquals(handler.getTargetList(skill, activeChar, false, target), EMPTY_TARGET_LIST);
	}
	
	@Test
	public void test_target_is_not_autoattackable() {
		expect(target.isDead()).andReturn(false);
		expect(target.isAutoAttackable(activeChar)).andReturn(false);
		expect(skill.getAffectScope()).andReturn(affectScope);
		expect(affectScope.affectTargets(activeChar, target, skill)).andReturn(List.of(activeChar, target));
		replayAll();
		
		assertEquals(handler.getTargetList(skill, activeChar, false, target), new L2Character[] {
			activeChar,
			target
		});
	}
	
	@Test
	public void test_target_is_autoattackable() {
		expect(target.isDead()).andReturn(false);
		expect(target.isAutoAttackable(activeChar)).andReturn(true);
		activeChar.sendPacket(INCORRECT_TARGET);
		expectLastCall().once();
		replayAll();
		
		assertEquals(handler.getTargetList(skill, activeChar, false, target), EMPTY_TARGET_LIST);
	}
}
