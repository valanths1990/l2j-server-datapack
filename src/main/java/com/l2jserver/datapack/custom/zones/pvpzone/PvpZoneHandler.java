package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class PvpZoneHandler implements IBypassHandler {
	private final String[] COMMANDS = { "pvpzone;homepage join", "pvpzone;homepage leave", "pvpzone;homepage" };
	private final Location regroupRoom = new Location(-86966, -81809, -8357); // kratei cube resting zone

	@Override public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
		String[] splitCommand = command.split(" ");
		String currentPage = splitCommand[0].split(";")[1];
		if (splitCommand.length == 1) {
			openBoard(player, currentPage);
		}

		switch (splitCommand[1]) {
			case "join" -> {
				Location loc = PvpZoneManager.getInstance().getRandomLocation();
				player.teleToLocation(loc, true);
				if (player.isInParty()) {
					//notify party
				}

			}
			case "joinparty" -> {

			}
			case "leave", "regroup" -> {
				player.teleToLocation(regroupRoom, true);
			}

		}

		return true;
	}

	private void openBoard(L2PcInstance player, String currentPage) {
		String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/pvpzone/html/" + currentPage + ".html");
		CommunityBoardHandler.separateAndSend(html, player);
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
