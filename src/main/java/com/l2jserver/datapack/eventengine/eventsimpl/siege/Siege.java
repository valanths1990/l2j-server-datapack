package com.l2jserver.datapack.eventengine.eventsimpl.siege;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.DistributionType;
import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.enums.ScoreType;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.eventsimpl.siege.config.SiegeConfig;
import com.l2jserver.datapack.eventengine.managers.CacheManager;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.entity.Team;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.model.instance.WorldInstance;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;
import com.l2jserver.gameserver.model.entity.capturetower.ITowerBehavior;
import com.l2jserver.gameserver.model.skills.Skill;
import okhttp3.Cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Siege extends BaseEvent<SiegeConfig> {

	private static final int TIME_RES_PLAYER = 10;
	private Team currentConquerer = null;

	@Override protected String getInstanceFile() {
		return getConfig().getInstanceFile();
	}

	@Override protected TeamsBuilder onCreateTeams() {
		List<LocationHolder> redLocs = this.zone.getRedStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
		List<LocationHolder> blueLocs = this.zone.getBlueStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
		TeamsBuilder builder = new TeamsBuilder().setPlayers(getPlayerEventManager().getAllEventPlayers()).setDistribution(DistributionType.TEAM);
		getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.RED).findFirst().map(t -> builder.addTeam(t, redLocs));
		getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.BLUE).findFirst().map(t -> builder.addTeam(t, blueLocs));
		return builder;
	}

	@Override protected void onEventStart() {
		addSuscription(ListenerType.ON_LOG_IN);
		addSuscription(ListenerType.ON_DEATH);
		addSuscription(ListenerType.ON_USE_TELEPORT);
		addSuscription(ListenerType.ON_INTERACT);
		addSuscription(ListenerType.ON_TOWER_CAPTURED);
		addSuscription(ListenerType.ON_DOOR_ACTION);
		addSuscription(ListenerType.ON_USE_SKILL);
		int instanceId = getInstanceWorldManager().getAllInstances().get(0).getInstanceId();
		for (L2DoorInstance door : InstanceManager.getInstance().getInstance(getInstanceWorldManager().getAllInstances().get(0).getInstanceId()).getDoors()) {
			door.setIsAttackableDoor(true);
			door.setInstanceId(instanceId);
		}

		spawnTowers();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ScoreTask(),0,1, TimeUnit.SECONDS);
	}

	@Override protected void onEventFight() {
	}

	@Override protected void onEventEnd() {
		//		giveRewardsTeams();
		getSpawnManager().removeAllNpcs();
	}

	@Override public void onDeath(OnDeathEvent event) {
		Player ph = getPlayerEventManager().getEventPlayer(event.getTarget());
		scheduleRevivePlayer(ph, TIME_RES_PLAYER);
	}

	@Override protected void onTowerCaptured(OnTowerCapturedEvent event) {

		System.out.println(event.getPlayer() +" capture tower");
		currentConquerer= event.getPlayer().getTeam();
	}

	@Override protected void onInteract(OnInteractEvent event) {
		System.out.println("interacted!!");
	}

	@Override protected void onDoorAction(OnDoorActionEvent event) {
//		event.getPlayer().getPcInstance().doAttack(event.getDoor());
	}

	@Override protected void onUseSkill(OnUseSkillEvent event) {
		if(currentConquerer==null){
			return;
		}
		if(event.getCaster().isPlayer()){
			Player p = (Player)event.getCaster();
			if(currentConquerer.getTeamType() == p.getTeamType()){
				event.setCancel(true);
			}
		}
	}


	private void spawnTowers() {
		WorldInstance instance = getInstanceWorldManager().getAllInstances().get(0);
		zone.getTowerSpawns().forEach(spawns -> {
//			CaptureTower tower = new CaptureTower(spawns, new TowerBehavior());
//			tower.getTower().setInstanceId(instance.getInstanceId());
//			tower.setTOWER_RANGE(100);
//			getSpawnManager().addNpc(tower.getTower());
		});
	}
	private void despawnTowers(){

	}

	private  final class ScoreTask implements  Runnable{

		@Override public void run() {
			if(currentConquerer==null){
				return;
			}
			currentConquerer.increasePoints(getConfig().getScoreType(),1);
			getPlayerEventManager().getAllEventPlayers().forEach(Siege.this::updateScore);
		}
	}

	private static final class TowerBehavior implements ITowerBehavior {

		@Override public L2PcInstance getCapturer(List<L2PcInstance> playersNearTower) {
			List<Player> players = playersNearTower.stream().map(p-> CacheManager.getInstance().getPlayer(p,true)).collect(Collectors.toList());
				Map<Boolean,List<Player>> redBlueTeams = players.stream().collect(Collectors.partitioningBy(p->p.getTeamType()==TeamType.BLUE));
				if(redBlueTeams.get(true).size()==redBlueTeams.get(false).size()){
					return null;
				}
				if(redBlueTeams.get(true).size()> redBlueTeams.get(false).size()){
					return redBlueTeams.get(true).get(Rnd.get(redBlueTeams.get(true).size())).getPcInstance();
				}
			return redBlueTeams.get(false).get(Rnd.get(redBlueTeams.get(false).size())).getPcInstance();
		}
	}


}
