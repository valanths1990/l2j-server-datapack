package com.l2jserver.datapack.custom.zones.farmzone;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.enums.DuelState;
import com.l2jserver.gameserver.enums.Team;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.TeleportWhereType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.HtmlUtil;

import java.util.List;
import java.util.stream.Collectors;

public class FarmZoneHandler implements IBypassHandler {

	private String[] COMMANDS = { "farmzone;homepage join", "farmzone;homepage leave", "farmzone;homepage" };
	private String button = "<button value=\"%name%\" action=\"bypass farmzone;homepage join %bypassCode%\" width=130 height=40 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {

		//		String currentPage = command.split(" ")[0].split(";")[1];
		if (command.split(" ").length >= 2) {
			String action = command.split(" ")[1];
			if (action.equals("join")) {
				joinFarmZone(command.split(" ",3)[2], activeChar);
			} else if (action.equals("leave")) {
				MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
				return false;
			}
		}
		List<Location> locations = FarmZoneManager.getInstance().getSpawnLocations();
		String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/farmzone/farmzonespawns.html");
		List<String> buttons = locations.stream().map(l -> button.replaceFirst("%name%", l.getName()).replaceFirst("%bypassCode%", l.getName())).collect(Collectors.toList());
		for (String s : buttons) {
			html = html.replaceFirst("%button%", s);
		}
		html = html.replaceAll("%button%", "");
		html = html.replaceFirst("%mapName%", FarmZoneManager.getInstance().getCurrentZone().getName());
		CommunityBoardHandler.separateAndSend(html, activeChar);
		return true;
	}

	private void joinFarmZone(String locationName, L2PcInstance player) {
		FarmZoneManager.getInstance().getSpawnLocations().stream().filter(l -> l.getName().equals(locationName)).findFirst().ifPresent(l -> player.teleToLocation(l, true));
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
