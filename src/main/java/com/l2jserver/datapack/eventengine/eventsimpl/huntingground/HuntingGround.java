package com.l2jserver.datapack.eventengine.eventsimpl.huntingground;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.config.BaseConfigLoader;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnDeathEvent;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnKillEvent;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnPlayableHitEvent;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnUnequipItem;
import com.l2jserver.datapack.eventengine.enums.DistributionType;
import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.enums.ScoreType;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.eventsimpl.huntingground.config.HuntingGroundConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.entity.Character;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.util.EventUtil;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.network.serverpackets.*;

import java.util.*;
import java.util.stream.Collectors;

public class HuntingGround extends BaseEvent<HuntingGroundConfig> {
	private static final int TIME_RES_PLAYER = 10;
	private static final int[] bowIds = { 13, 276, 281, 284, 289, 8684 };
	//	private static final int[] crossBowIds = { 9212, 9842, 9845, 9312, 9348 };

	private static final int[] arrows = { 17, 1341, 1342, 1343, 1344 }; // order is important
	//	private static final int[] bolts = { 9632, 9633, 9634, 9635, 9636 }; // order is important

	//	private static final int [][] rangeSkills = {{113,9},{5560,1},{431,1}};

	private Set<L2ItemInstance> eventItems = new HashSet<>();

	@Override protected String getInstanceFile() {
		return getConfig().getInstanceFile();
	}

	@Override protected TeamsBuilder onCreateTeams() {
		List<LocationHolder> redLocs = this.zone.getRedStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
		List<LocationHolder> blueLocs = this.zone.getBlueStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
		TeamsBuilder builder = new TeamsBuilder().setPlayers(getPlayerEventManager().getAllEventPlayers()).setDistribution(getConfig().getType());
		getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.RED).findFirst().map(t -> builder.addTeam(t, redLocs));
		getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.BLUE).findFirst().map(t -> builder.addTeam(t, blueLocs));
		return builder;
	}

	@Override protected void onEventStart() {
		getPlayerEventManager().getAllEventPlayers().forEach(this::addBowAndArrows);
		addSuscription(ListenerType.ON_LOG_IN);
		addSuscription(ListenerType.ON_KILL);
		addSuscription(ListenerType.ON_DEATH);
		addSuscription(ListenerType.ON_PLAYABLE_HIT);
		addSuscription(ListenerType.ON_UNEQUIP_ITEM);
		addSuscription(ListenerType.ON_USE_TELEPORT);

		//		Map<Boolean, List<Player>> redAndBlueTeam = getPlayerEventManager().getAllEventPlayers().stream().collect(Collectors.partitioningBy(player -> player.getTeamType() == TeamType.RED));
		//		getPlayerEventManager().getAllEventPlayers().forEach(player -> {
		//			player.getPcInstance().sendPacket(new ExCubeGameChangeTimeToStart(0));
		//			player.getPcInstance().sendPacket(new ExCubeGameTeamList(redAndBlueTeam.get(true).stream().map(Player::getPcInstance).collect(Collectors.toList()), redAndBlueTeam.get(false).stream().map(Player::getPcInstance).collect(Collectors.toList()), 0));
		//
		//		});
		//		getTeamsManager().getAllTeams().forEach(t -> {
		//			int point = Rnd.get(10);
		//			t.getMembers().forEach(p -> p.increasePoints(ScoreType.KILL, point));
		//			t.getMembers().forEach(p -> p.increasePoints(ScoreType.KILL, point));
		//			t.increasePoints(ScoreType.KILL, point);
		//			t.increasePoints(ScoreType.KILL, point);
		////			showScore(DistributionType.TEAM);
		//		});
		//		showScore(DistributionType.TEAM);
	}

	private void addBowAndArrows(Player p) {

		L2PcInstance player = p.getPcInstance();
		int bowId = bowIds[Rnd.get(bowIds.length)];

		L2ItemInstance bow = new L2ItemInstance(IdFactory.getInstance().getNextId(), bowId);
		int grade = bow.getItem().getItemGrade().ordinal();
		int arrowId = arrows[grade];

		L2ItemInstance arrows = new L2ItemInstance(IdFactory.getInstance().getNextId(), arrowId);
		arrows.changeCount("Event", 200, null, null);

		eventItems.add(bow);
		eventItems.add(arrows);
		player.addItem("Event", arrows, null, false);
		player.addItem("Event", bow, null, false);
		player.useEquippableItem(bow.getObjectId(), true);

	}

	@Override protected void onUnequipItem(OnUnequipItem event) {
		if (eventItems.contains(event.getItem()) || event.getItem().getItemType() == EtcItemType.ARROW || event.getItem().getItemType() == WeaponType.BOW) {
			event.setCancel(true);
		}
	}

	@Override protected void onEventFight() {

	}

	@Override public void onKill(OnKillEvent event) {
		Player ph = getPlayerEventManager().getEventPlayer(event.getAttacker());
		Character target = event.getTarget();

		// We increased the team's points
		getTeamsManager().getPlayerTeam(ph).increasePoints(ScoreType.KILL, 1);
		ph.increasePoints(ScoreType.KILL, 1);
		// Message Kill
		if (BaseConfigLoader.getInstance().getMainConfig().isKillerMessageEnabled()) {
			EventUtil.messageKill(ph, target);
		}
		updateScore(ph);
	}

	@Override public void onDeath(OnDeathEvent event) {
		Player ph = getPlayerEventManager().getEventPlayer(event.getTarget());

		scheduleRevivePlayer(ph, TIME_RES_PLAYER);
		// Incremented by one the number of deaths Character
		ph.increasePoints(ScoreType.DEATH, 1);
	}

	@Override public void onPlayableHit(OnPlayableHitEvent event) {
		L2PcInstance attacker = event.getAttacker().getPcInstance();
		L2PcInstance attacked = event.getAttacked().getPcInstance();
		attacked.doDie(attacker);
	}

	@Override protected void onEventEnd() {
		// RewardManager
		eventItems.stream().filter(i -> L2World.getInstance().getPlayer(i.getOwnerId()) != null).forEach(i -> L2World.getInstance().getPlayer(i.getOwnerId()).destroyItem("Event", i, null, false));

//		boolean isRedWinner = getTeamsManager().getWinner().stream().anyMatch(t -> t.getTeamType() == TeamType.RED);
//		getPlayerEventManager().getAllEventPlayers().forEach(p -> p.getPcInstance().sendPacket(new ExCubeGameEnd(isRedWinner)));
	}
}
