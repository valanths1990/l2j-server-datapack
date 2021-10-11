package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.datapack.custom.zones.AbstractTimeZone;
import com.l2jserver.datapack.custom.zones.pvpzone.zonemodes.Deathmatch;
import com.l2jserver.datapack.custom.zones.pvpzone.zonemodes.DominationMode;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class PvpZoneManager extends AbstractTimeZone {

	private final Map<L2PcInstance, Integer> currentZoneScore = new ConcurrentHashMap<>();
	private ZoneMode z;

	private PvpZoneManager() {
		super();
		this.zoneTime = Configuration.customs().getPvpZoneTime();
		zones = new ArrayList<>(ZoneManager.getInstance().getAllZones(L2PvpZone.class));
		zones.forEach(z -> z.setEnabled(false));
		Collections.shuffle(zones);
		handler = new PvpZoneHandler();
		BypassHandler.getInstance().registerHandler(handler);
		scheduleNewZone();
	}

	@Override protected void registerListeners() {
		super.registerListeners();
		registeredListeners.add(currentZone.addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, this::onPvpKill, this)));
	}

	@Override protected void startNewZone() {
		super.startNewZone();
		z = new DominationMode((L2PvpZone) currentZone);
		z.onZoneModeStart();
	}

	private void onPvpKill(IBaseEvent event) {

		OnPlayerPvPKill e = (OnPlayerPvPKill) event;
		if (currentZone != null && !currentZone.isCharacterInZone(e.getActiveChar())) {
			return;
		}
		if (!currentZoneScore.containsKey(e.getActiveChar())) {
			currentZoneScore.put(e.getActiveChar(), 1);
			return;
		}
		currentZoneScore.put(e.getActiveChar(), currentZoneScore.get(e.getActiveChar()) + 1);
	}

	private void showScoreBoard() {
		//		currentZoneScore.forEach(); // show players the scoreboard
		currentZoneScore.clear();
	}

	@Override protected void clearCurrentZone() {
		super.clearCurrentZone();
		showScoreBoard();
		if (z != null) {
			z.onZoneModeEnd();
		}
	}

	@Override protected void teleportPlayersOut() {
		if (currentZone == null) {
			return;
		}
		currentZone.getPlayersInside().forEach(p -> handler.useBypass("pvpzone;homepage leave", p, null));
	}

	public static PvpZoneManager getInstance() {
		return PvpZoneManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final PvpZoneManager instance = new PvpZoneManager();
	}

	public static void main(String[] args) {
		PvpZoneManager.getInstance();
	}
}
