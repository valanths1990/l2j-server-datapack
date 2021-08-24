package com.l2jserver.datapack.custom.augment;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;

public class Augment implements IBypassHandler {
	private static final String[] COMMANDS = { "augment;homepage" };

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String[] splitte = command.split(" ");
		if (splitte.length == 2) {
			if (splitte[1].equals("add")) {
				activeChar.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
			}
			if (splitte[1].equals("remove")) {
				activeChar.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
			}
		} else {
			CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/augment/augment.html"), activeChar);
		}
		return false;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
