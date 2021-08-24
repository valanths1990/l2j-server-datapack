package com.l2jserver.datapack.custom.action;

import com.l2jserver.datapack.handlers.itemhandlers.Bypass;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Action implements IBypassHandler {
	private String[] COMMANDS = {
		"action;homepage"
	};

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		BypassHandler.getInstance().getHandler("eventengine;homepage").useBypass(command,activeChar,bypassOrigin);
		return false;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
