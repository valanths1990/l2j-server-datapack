package com.l2jserver.datapack.custom.store;

import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Store implements IBypassHandler {
	private String[] COMMANDS = {
		"store;homepage"
	};

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		return false;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
