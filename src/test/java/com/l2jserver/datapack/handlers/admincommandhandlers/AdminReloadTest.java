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
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void useAdminCommandTest() {
		general().setProperty("EverybodyHasAdminRights", "true");
		cmd.useAdminCommand("admin_reload config general", activeChar);
		assertFalse(general().everybodyHasAdminRights());
	}
}
