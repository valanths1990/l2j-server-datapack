package com.l2jserver.datapack.custom.home;

import com.l2jserver.datapack.votesystem.util.Url;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.network.serverpackets.OpenUrl;
import com.l2jserver.gameserver.util.HtmlUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Home implements IBypassHandler {
	private String[] COMMANDS = { "home;homepage" };

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String[] splitted = command.split(" ");
		String resultHmtl = "";
		if (splitted.length == 1) {
			resultHmtl = openHomePage(activeChar);
		} else if (splitted.length == 3) {
			if (splitted[2].equals("website")) {
				if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					try {
						Desktop.getDesktop().browse(new URI("http://www.example.com"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
				resultHmtl = openHomePage(activeChar);
			}
			if (splitted[2].equals("features")) {
				resultHmtl = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(),"data/custom/home/features.html");
			}
		}
		CommunityBoardHandler.separateAndSend(resultHmtl, activeChar);
		return true;
	}

	private String openHomePage(L2PcInstance pc) {
		return HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/home/home.html");
	}

	private void openUrl(L2PcInstance pc) {
		//	pc.sendPacket(new OpenUrl("https://l2-angel.com"));
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
