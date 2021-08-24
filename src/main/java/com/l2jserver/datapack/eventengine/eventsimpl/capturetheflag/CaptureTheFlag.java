package com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag;

/*
 * Copyright (C) 2015-2016 L2J EventEngine
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.config.BaseConfigLoader;
import com.l2jserver.datapack.eventengine.datatables.MessageData;
import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.*;
import com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag.config.CTFEventConfig;
import com.l2jserver.datapack.eventengine.helper.RewardHelper;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;
import com.l2jserver.datapack.eventengine.model.entity.Character;
import com.l2jserver.datapack.eventengine.model.entity.Npc;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.entity.Team;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.util.EventUtil;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CaptureTheFlag extends BaseEvent<CTFEventConfig> {
	// FlagItem
	private static final int FLAG_ITEM = 6718;
	// Time for resurrection
	private static final int TIME_RES_PLAYER = 10;
	// Radius spawn
	private final Map<Integer, TeamType> _flagSpawn = new ConcurrentHashMap<>();
	private final Map<Integer, TeamType> _holderSpawn = new ConcurrentHashMap<>();
	private final Map<Integer, TeamType> _flagHasPlayer = new ConcurrentHashMap<>();
	private final Map<TeamType, LocationHolder> _flagsLoc = new HashMap<>();
	private final Map<Player, L2ItemInstance> playerFlagItem = new HashMap<>();

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
		addSuscription(ListenerType.ON_INTERACT);
		addSuscription(ListenerType.ON_KILL);
		addSuscription(ListenerType.ON_DEATH);
		//		addSuscription(ListenerType.ON_USE_ITEM);
		addSuscription(ListenerType.ON_LOG_OUT);
		addSuscription(ListenerType.ON_LOG_IN);
		addSuscription(ListenerType.ON_USE_TELEPORT);
		addSuscription(ListenerType.ON_UNEQUIP_ITEM);

		spawnFlagsAndHolders();
		for (Player ph : getPlayerEventManager().getAllEventPlayers())
			updateTitle(ph);
	}

	@Override protected void onEventFight() {
		// Nothing
	}

	@Override protected void onEventEnd() {
		clearFlags();
		giveRewardsTeams();
	}

	@Override public void onInteract(OnInteractEvent event) {
		Player ph = getPlayerEventManager().getEventPlayer(event.getPlayer());
		Npc npc = event.getNpc();

		if (npc.getTemplateId() == getConfig().getFlagNpcId()) {
			if (hasFlag(ph))
				return;

			TeamType flagTeam = _flagSpawn.get(npc.getObjectId());
			if (ph.getTeamType() != flagTeam) {
				// Animation
				ph.castSkill(ph, 1034, 1, 1, 1);
				// Delete the flag from the map
				_flagSpawn.remove(npc.getObjectId());
				// Remove the flag from its position
				getSpawnManager().removeNpc(npc);
				// Equip the flag
				createAndEquipFlag(ph, flagTeam);
				// Save the player has the flag
//				_flagHasPlayer.put(event.getPlayer().getObjectId(), flagTeam);
				// Announce the flag was taken
				EventUtil.announceTo(MessageType.BATTLEFIELD, "ctf_captured_the_flag", "%holder%", ph.getTeam().getName(), CollectionTarget.ALL_PLAYERS_IN_EVENT);
			}
		} else if (npc.getTemplateId() == getConfig().getHolderNpcId()) {
			if (ph.getTeamType() == _holderSpawn.get(npc.getObjectId())) {
				if (hasFlag(ph)) {
					// Animation Large FireWork
					ph.castSkill(ph, 2025, 1, 1, 1);
					// Increase the points
					getTeamsManager().getPlayerTeam(ph).increasePoints(ScoreType.POINT, getConfig().getPointsConquerFlag());
					ph.increasePoints(ScoreType.POINT,getConfig().getPointsConquerFlag());
					// Remove the flag from player
					Team th = getTeamsManager().getTeam(_flagHasPlayer.remove(ph.getObjectId()));
					unequipFlagAndDelete(ph);
					ph.getPcInstance().destroyItemByItemId("event", FLAG_ITEM, 1, null, false);
					// Spawn the flag again
					LocationHolder flagLocation = _flagsLoc.get(th.getTeamType());
					_flagSpawn.put(getSpawnManager().addNpc(getConfig().getFlagNpcId(), flagLocation, th.getName(), false, getInstanceWorldManager().getAllInstances().get(0).getInstanceId()).getObjectId(), th.getTeamType());
					// Announce the flag was taken
					EventUtil.announceTo(MessageType.BATTLEFIELD, "ctf_conquered_the_flag", "%holder%", ph.getTeam().getName(), CollectionTarget.ALL_PLAYERS_IN_EVENT);
					// Show team points
					updateScore(ph);
				}
			}
		}
	}

	@Override public void onKill(OnKillEvent event) {
		Player ph = getPlayerEventManager().getEventPlayer(event.getAttacker());
		Character target = event.getTarget();

		Player targetEvent = getPlayerEventManager().getEventPlayer(target);
		if (hasFlag(targetEvent)) {
			dropFlag(targetEvent);
			unequipFlagAndDelete(targetEvent);
		}

//		getTeamsManager().getPlayerTeam(ph).increasePoints(ScoreType.POINT, getConfig().getPointsKill());

		if (getConfig().isRewardKillEnabled())
			ph.giveItems(getConfig().getRewardKill());

		if (getConfig().isRewardPvPKillEnabled()) {
			ph.setPvpKills(ph.getPvpKills() + getConfig().getRewardPvPKill());
			EventUtil.sendEventMessage(ph, MessageData.getInstance().getMsgByLang(ph, "reward_text_pvp", true).replace("%count%", getConfig().getRewardPvPKill() + ""));
		}

		if (getConfig().isRewardFameKillEnabled()) {
			ph.setFame(ph.getFame() + getConfig().getRewardFameKill());
			EventUtil.sendEventMessage(ph, MessageData.getInstance().getMsgByLang(ph, "reward_text_fame", true).replace("%count%", getConfig().getRewardFameKill() + ""));
		}

		if (BaseConfigLoader.getInstance().getMainConfig().isKillerMessageEnabled())
			EventUtil.messageKill(ph, target);
		//		showPoint(ph.getTeam());
	}

	@Override public void onDeath(OnDeathEvent event) {
		scheduleRevivePlayer(event.getTarget(), TIME_RES_PLAYER);
	}

	//	@Override public void onUseItem(OnUseItemEvent event) {
	//		Player ph = event.getPlayer();
	//		ItemTemplate item = event.getItem();
	//
	//		if (item.getTemplateId() == FLAG_ITEM || (hasFlag(ph) && item.isWeapong())){
	//		event.setCancel(true);
	//		}
	//	}
	@Override public void onUnequipItem(OnUnequipItem event) {
		if (hasFlag(event.getPlayer())) {
			event.setCancel(true);
		}
	}

	@Override public void onLogout(OnLogOutEvent event) {
		Player ph = event.getPlayer();

		if (hasFlag(ph)) {
			dropFlag(ph);
			unequipFlagAndDelete(ph);
		}
	}

	// VARIOUS METHODS -------------------------------------------------

	/**
	 * Spawn flags and holders.
	 */
	private void spawnFlagsAndHolders() {
		int instanceId = getInstanceWorldManager().getAllInstances().get(0).getInstanceId();

		Map<TeamType, LocationHolder> mapFlags = new HashMap<>();
		Map<TeamType, LocationHolder> mapHolders = new HashMap<>();

		for (TeamConfig config : getConfig().getTeams()) {
			mapFlags.put(config.getColor(), config.getColor() == TeamType.RED ? new LocationHolder(zone.getRedFlagLoc()) : new LocationHolder(zone.getBlueFlagLoc()));
			mapHolders.put(config.getColor(), config.getColor() == TeamType.RED ? new LocationHolder(zone.getRedFlagLoc()) : new LocationHolder(zone.getBlueFlagLoc()));
		}

		for (Team th : getTeamsManager().getAllTeams()) {
			if (mapFlags.containsKey(th.getTeamType())) {
				LocationHolder flagLocation = mapFlags.get(th.getTeamType());
				_flagsLoc.put(th.getTeamType(), flagLocation);

				LocationHolder holderLocation = mapHolders.get(th.getTeamType());
				_flagSpawn.put(getSpawnManager().addNpc(getConfig().getFlagNpcId(), flagLocation, th.getName(), false, instanceId).getObjectId(), th.getTeamType());
				_holderSpawn.put(getSpawnManager().addNpc(getConfig().getHolderNpcId(), holderLocation, th.getName(), false, instanceId).getObjectId(), th.getTeamType());
			}
		}
	}

	private void giveRewardsTeams() {
		if (getPlayerEventManager().getAllEventPlayers().isEmpty())
			return;

		RewardHelper.newInstance().setParticipants(getTeamsManager().getAllTeams()).setScoreType(ScoreType.POINT).addReward(1, getConfig().getReward()).distribute(AnnounceType.WINNER);
	}

//	private void showPoint(Team team) {
//		ScreenMessageHelper.newInstance().setTime(10000).setMessage(" | %teamName% %points% | ").replaceHolder("%teamName%", team.getName()).replaceHolder("%points%", String.valueOf(team.getPoints(ScoreType.POINT))).show(getPlayerEventManager().getAllEventPlayers());
//	}

	private boolean hasFlag(Player ph) {
		return _flagHasPlayer.containsKey(ph.getObjectId());
	}

	private void createAndEquipFlag(Player ph, TeamType flagTeam) {
		L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), FLAG_ITEM);
		ph.getPcInstance().addItem("Event", item, null, false);
		ph.equipItem(item);
		_flagHasPlayer.put(ph.getObjectId(), flagTeam);
		playerFlagItem.put(ph, item);
	}

	private void unequipFlagAndDelete(Player ph) {
		ph.unequipItem(InventoryItemType.PAPERDOLL_RHAND);
		L2ItemInstance flag = playerFlagItem.remove(ph);
		ph.getPcInstance().destroyItem("event", flag, null, false);
	}

	private void dropFlag(Player ph) {
		Team th = getTeamsManager().getTeam(_flagHasPlayer.remove(ph.getObjectId()));
		_flagSpawn.put(getSpawnManager().addNpc(getConfig().getFlagNpcId(), ph.getLocation(), th.getName(), false, ph.getWorldInstanceId()).getObjectId(), th.getTeamType());
		Map<String, String> map = new HashMap<>();
		// We announced that a flag was taken
		map.put("%holder%", ph.getName());
		map.put("%flag%", th.getName());
		EventUtil.announceTo(MessageType.BATTLEFIELD, "player_dropped_flag", map, CollectionTarget.ALL_PLAYERS_IN_EVENT);
	}

	private void clearFlags() {
		for (int playerId : _flagHasPlayer.keySet()) {
			unequipFlagAndDelete(getPlayerEventManager().getEventPlayer(playerId));
		}
		_flagHasPlayer.clear();
	}

	private void updateTitle(Player ph) {
		ph.setTitle("[ " + ph.getTeam().getName() + " ]");
	}
}