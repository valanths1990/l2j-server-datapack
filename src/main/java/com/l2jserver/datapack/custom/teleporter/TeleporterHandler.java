package com.l2jserver.datapack.custom.teleporter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.l2jserver.datapack.custom.zones.farmzone.FarmZoneHandler;
import com.l2jserver.datapack.custom.zones.farmzone.FarmZoneManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.enums.Team;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.L2TeleportLocation;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.Participant;
import com.l2jserver.gameserver.model.zone.type.L2FarmZone;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.network.serverpackets.ExShowPVPMatchRecord;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

public class TeleporterHandler implements IBypassHandler {

	private String[] COMMANDS = { "teleport;homepage" };
	private Random rand = new Random();

	public TeleporterHandler() {
	}

	@Override public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
		String[] splitted = command.split(" ");
		String currentPage = splitted[0].split(";")[1];
		if (splitted.length == 1) {
			String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/teleporter/" + currentPage + ".html");
			CommunityBoardHandler.separateAndSend(html, player);
			return true;
		}

		teleportPlayer(player, splitted[1]);
		return false;
	}

	public void teleportPlayer(L2PcInstance player, String id) {
		L2TeleportLocation tp = TeleportLocationTable.getInstance().getTemplate(Integer.parseInt(id));
		if (player.getAdena() < tp.getPrice()) {
			player.sendPacket(SystemMessageId.YOU_NOT_ENOUGH_ADENA);
			return;
		}
		Location loc = new Location(tp.getLocX(), tp.getLocY(), tp.getLocZ());
		loc.setInstanceId(0);
		player.reduceAdena("Teleport", tp.getPrice(), null, true);
		player.teleToLocation(loc,true);
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
