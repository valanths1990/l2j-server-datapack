package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.datapack.custom.reward.RewardManager;
import com.l2jserver.datapack.custom.zones.pvpzone.ZoneMode;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.enums.TowerMode;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;
import com.l2jserver.gameserver.model.entity.capturetower.SinglePlayerBehavior;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTowerCapture;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DominationMode extends ZoneMode {
	private final List<CaptureTower> towers = new ArrayList<>();
	private final Map<CaptureTower, L2PcInstance> capturedTowers = new ConcurrentHashMap<>();

	public DominationMode(L2PvpZone currentZone) {
		super(currentZone);
	}

	@Override public void onZoneModeEnd() {
		towers.forEach(CaptureTower::deleteMe);
		removeAllListeners();
	}

	@Override public void onZoneModeStart() {
		registerEvent(EventType.ON_PLAYER_PVP_KILL);
		registerEvent(EventType.ON_CREATURE_ZONE_EXIT);
		registerEvent(EventType.ON_CREATURE_ZONE_ENTER);
		registerEvent(EventType.ON_PLAYER_TOWER_CAPTURE);
//		currentZone.getSpawns().forEach(l -> towers.add(new CaptureTower(NpcData.getInstance().getTemplate(40009), new SinglePlayerBehavior())));
//		towers.forEach(CaptureTower::dospawn);
	}

	@Override protected TerminateReturn receivedEvent(IBaseEvent event) {
		switch (event.getType()) {
			case ON_PLAYER_TOWER_CAPTURE -> {
				OnPlayerTowerCapture onTowerCaptureEvent = (OnPlayerTowerCapture) event;
				CaptureTower tower = onTowerCaptureEvent.getTower();
				L2PcInstance player = onTowerCaptureEvent.getActiveChar();
				if (!currentZone.isInsideZone(player)) {
					return null;
				}
				capturedTowers.put(tower, player);
			}
			case ON_PLAYER_PVP_KILL -> {
				L2PcInstance player = ((OnPlayerPvPKill) event).getActiveChar();
				if (!currentZone.isInsideZone(player)) {
					return null;
				}
				if (capturedTowers.containsValue(player)) {
					RewardManager.getInstance().rewardPlayer(player, "tower");
					if (player.getParty() != null) {
						List<L2PcInstance> ptMembers = player.getParty().getMembers();
						ptMembers.remove(player);
						ptMembers.forEach(m -> RewardManager.getInstance().rewardPlayer(m, "tower"));
					}
				}
			}
			case ON_CREATURE_ZONE_EXIT -> {
				L2Character character = ((OnCreatureZoneExit) event).getCreature();
				if (character instanceof L2PcInstance) {
					L2PcInstance player = (L2PcInstance) character;
					if (capturedTowers.containsValue(player)) {
						Optional<CaptureTower> t = capturedTowers.entrySet().stream().filter(entry -> entry.getValue() == player).map(Map.Entry::getKey).findFirst();
						t.ifPresent(CaptureTower::resetMe);
						t.ifPresent(capturedTowers::remove);
					}
//					towers.stream().filter(t -> t.getCapturer() == player).forEach(CaptureTower::resetMe);
				}
			}
		}
		return null;
	}

}

