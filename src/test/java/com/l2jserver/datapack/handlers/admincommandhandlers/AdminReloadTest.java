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
package com.l2jserver.datapack.handlers.admincommandhandlers;

import static com.l2jserver.gameserver.config.Configuration.general;
import static org.easymock.EasyMock.anyString;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.testng.Assert.assertFalse;

import org.powermock.api.easymock.annotation.Mock;
import org.testng.annotations.Test;

import com.l2jserver.datapack.test.AbstractTest;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Admin Reload test.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class AdminReloadTest extends AbstractTest {
	
	@Mock
	private L2PcInstance player;
	
	private final AdminReload adminReload = new AdminReload();
	
	@Test
	public void useAdminCommandTest() {
		general().setProperty("EverybodyHasAdminRights", "true");
		expect(player.getName()).andReturn("Zoey76");
		player.sendMessage(anyString());
		expectLastCall();
		replay(player);
		
		adminReload.useAdminCommand("admin_reload config general", player);
		assertFalse(general().everybodyHasAdminRights());
	}
}
