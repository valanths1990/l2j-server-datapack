package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.datapack.custom.zones.pvpzone.ZoneMode;
import com.l2jserver.datapack.handlers.chathandlers.ChatTell;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerChat;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPartyRequest;
import com.l2jserver.gameserver.model.events.returns.ChatFilterReturn;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.skills.AbnormalVisualEffect;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;
import com.l2jserver.gameserver.util.Broadcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Deathmatch extends ZoneMode {

	private Map<Integer, String> playersName = new ConcurrentHashMap<>();

	public Deathmatch(L2PvpZone zone) {
		super(zone);

	}

	@Override protected TerminateReturn receivedEvent(IBaseEvent event) {
		switch (event.getType()) {
			case ON_PLAYER_PARTY_REQUEST -> new TerminateReturn(true, false, true);
			case ON_PLAYER_CHAT -> new ChatFilterReturn(((OnPlayerChat) event).getText(), true, !isAllowedToTalk(((OnPlayerChat) event).getChatType()));
			case ON_CREATURE_ZONE_ENTER -> {
				OnCreatureZoneEnter zoneEnter = ((OnCreatureZoneEnter) event);
				if (zoneEnter.getZone() != currentZone) {
					return null;
				}
				if (!(zoneEnter.getCreature() instanceof L2PcInstance)) {
					return null;
				}
				if (!playersName.containsKey(zoneEnter.getCreature().getObjectId())) {
					playersName.put(zoneEnter.getCreature().getObjectId(), ((L2PcInstance) zoneEnter.getCreature()).getAppearance().getVisibleName());
				}

				((L2PcInstance) zoneEnter.getCreature()).getAppearance().setVisibleName(((L2PcInstance) zoneEnter.getCreature()).getClassId().name());
				zoneEnter.getCreature().broadcastPacket(new CharInfo((L2PcInstance) zoneEnter.getCreature()));
			}
			case ON_CREATURE_ZONE_EXIT -> {
				OnCreatureZoneExit zoneExit = ((OnCreatureZoneExit) event);
				if (zoneExit.getZone() != currentZone) {
					return null;
				}
				if (!(zoneExit.getCreature() instanceof L2PcInstance)) {
					return null;
				}
				if (!playersName.containsKey(zoneExit.getCreature().getObjectId())) {
					return null;
				}
				((L2PcInstance) zoneExit.getCreature()).getAppearance().setVisibleName(playersName.get(zoneExit.getCreature().getObjectId()));
				zoneExit.getCreature().broadcastPacket(new CharInfo((L2PcInstance) zoneExit.getCreature()));
			}
			case ON_PLAYER_LOGOUT -> {
				OnPlayerLogout logout = (OnPlayerLogout) event;
				int objectId = logout.getActiveChar().getObjectId();
				if (!playersName.containsKey(objectId)) {
					return null;
				}
				logout.getActiveChar().getAppearance().setVisibleName(playersName.get(objectId));
			}
		}
		return null;
	}

	private void restoreNames() {
		playersName.forEach((i, s) -> {
			L2World.getInstance().getPlayer(i).getAppearance().setVisibleName(s);
		});
	}

	private boolean isAllowedToTalk(int type) {
		switch (type) {
			case Say2.ALL, Say2.SHOUT, Say2.HERO_VOICE, Say2.TRADE -> {
				return false;
			}
		}
		return true;
	}

	@Override public void onZoneModeEnd() {
		restoreNames();
		this.removeAllListeners();
	}

	@Override public void onZoneModeStart() {
		registerEvent(EventType.ON_PLAYER_PARTY_REQUEST);
		registerEvent(EventType.ON_PLAYER_CHAT);
		registerEvent(EventType.ON_CREATURE_ZONE_ENTER);
		registerEvent(EventType.ON_CREATURE_ZONE_EXIT);
		registerEvent(EventType.ON_PLAYER_LOGOUT);
	}
}
