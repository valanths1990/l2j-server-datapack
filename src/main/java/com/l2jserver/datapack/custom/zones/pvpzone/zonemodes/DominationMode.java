package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.datapack.custom.reward.RewardManager;
import com.l2jserver.datapack.custom.zones.pvpzone.ZoneMode;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.enums.TowerMode;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.*;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTowerCapture;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DominationMode extends ZoneMode {
	private List<CaptureTower> towers ;
	private final Map<CaptureTower, List<L2PcInstance>> capturedTowers = new ConcurrentHashMap<>();
	private final Map<L2PcInstance,CaptureTower> playerTower = new ConcurrentHashMap<>();
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

		towers = currentZone.getSpawns().stream().map(this::createNewTower).collect(Collectors.toList());
		towers.forEach(CaptureTower::spawnMe);
	}
	private CaptureTower createNewTower(Location l ){
		CaptureTower tower = new CaptureTower(NpcData.getInstance().getTemplate(Configuration.customs().getCaptureTowerId())
			,List.of(new CheckForSinglePlayer(),new CheckForParty()),t->t.setTitle(t.getCapturer().getName()+" "+t.getProgress()+"%"),null);
		tower.setLocation(l);
		return tower;
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
				capturedTowers.put(tower, player.getParty()==null?List.of(player):player.getParty().getMembers());
				playerTower.put(player,tower);
			}
			case ON_PLAYER_PVP_KILL -> {
				L2PcInstance player = ((OnPlayerPvPKill) event).getActiveChar();
				if (!currentZone.isInsideZone(player)) {
					return null;
				}
				if (capturedTowers.values().stream().flatMap(Collection::stream).anyMatch(p->p == player)) {
					RewardManager.getInstance().rewardPlayer(player, "tower");
					if (player.getParty() != null) {
						List<L2PcInstance> ptMembers = player.getParty().getMembers();
						ptMembers.forEach(m -> RewardManager.getInstance().rewardPlayer(m, "tower"));
					}
				}
			}
			case ON_CREATURE_ZONE_EXIT -> {
				L2Character character = ((OnCreatureZoneExit) event).getCreature();
				if (character instanceof L2PcInstance) {
					L2PcInstance player = (L2PcInstance) character;
					if (playerTower.containsKey(player)) {
						CaptureTower tower = playerTower.remove(player);
						capturedTowers.remove(tower);
						tower.resetMe();
					}
				}
			}
		}
		return null;
	}

}

