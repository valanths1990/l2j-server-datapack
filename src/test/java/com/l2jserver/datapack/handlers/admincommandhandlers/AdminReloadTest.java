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
import static org.testng.Assert.assertFalse;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * Admin Reload test.
 * @author Zoey76
 * @version 2.6.1.0
 */
public class AdminReloadTest {
	
	private final IAdminCommandHandler cmd = new AdminReload();
	
	@Mock
	private L2PcInstance activeChar;
	
	@BeforeMethod
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}
	
	@Test
	public void useAdminCommandTest() {
		general().setProperty("EverybodyHasAdminRights", "true");
		cmd.useAdminCommand("admin_reload config general", activeChar);
		assertFalse(general().everybodyHasAdminRights());
	}
}
