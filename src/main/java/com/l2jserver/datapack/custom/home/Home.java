package com.l2jserver.datapack.custom.home;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;

public class Home implements IBypassHandler {
	private String[] COMMANDS = { "home;homepage" };

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
//		EventDispatcher.getInstance().notifyEvent(new OnPlayerPvPKill(activeChar,null), Containers.Players());
//		HtmCache.getInstance().reload();
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/home/home.html");
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
