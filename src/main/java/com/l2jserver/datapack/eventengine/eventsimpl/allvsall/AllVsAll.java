package com.l2jserver.datapack.eventengine.eventsimpl.allvsall;

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

import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.config.BaseConfigLoader;
import com.l2jserver.datapack.eventengine.datatables.MessageData;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnDeathEvent;
import com.l2jserver.datapack.eventengine.dispatcher.events.OnKillEvent;
import com.l2jserver.datapack.eventengine.enums.*;
import com.l2jserver.datapack.eventengine.eventsimpl.allvsall.config.AllVsAllEventConfig;
import com.l2jserver.datapack.eventengine.helper.RewardHelper;
import com.l2jserver.datapack.eventengine.interfaces.IParticipant;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.entity.Summon;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.util.EventUtil;
import com.l2jserver.datapack.eventengine.util.SortUtils;
import com.l2jserver.gameserver.util.Util;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class AllVsAll extends BaseEvent<AllVsAllEventConfig> {
	// Time for resurrection
	private static final int TIME_RES_PLAYER = 10;

	@Override protected String getInstanceFile() {
		return getConfig().getInstanceFile();
	}

	@Override protected TeamsBuilder onCreateTeams() {
		List<LocationHolder> locations = Util.getRandomLocationsInsideZone(this.zone, 10, this.zone.getBlueStartLoc()).stream().map(LocationHolder::new).collect(Collectors.toList());
		TeamsBuilder buider = new TeamsBuilder().setPlayers(getPlayerEventManager().getAllEventPlayers()).setDistribution(DistributionType.SINGLE);
		this.getPlayerEventManager().getAllEventPlayers().forEach(p -> buider.addTeam(new TeamConfig("L2Angel", TeamType.WHITE.name()), locations));
		return buider;

	}

	@Override protected void onEventStart() {
		addSuscription(ListenerType.ON_LOG_IN);
		addSuscription(ListenerType.ON_KILL);
		addSuscription(ListenerType.ON_DEATH);
		addSuscription(ListenerType.ON_USE_TELEPORT);
		showTime();
	}

	@Override protected void onEventFight() {
		// Nothing
	}

	@Override protected void onEventEnd() {
		giveRewardsTeams();
	}

	// LISTENERS -----------------------------------------------------------------------
	@Override public void onKill(OnKillEvent event) {
		if (event.getTarget() instanceof Summon)
			return;

		Player player = getPlayerEventManager().getEventPlayer(event.getAttacker());

		// Increase the amount of one character kills
		player.increasePoints(ScoreType.KILL, 1);
		updateTitle(player);

		// Reward for kills
		if (getConfig().isRewardKillEnabled()) {
			player.giveItems(getConfig().getRewardKill());
		}
		// Reward PvP for kills
		if (getConfig().isRewardPvPKillEnabled()) {
			player.setPvpKills(player.getPvpKills() + getConfig().getRewardPvPKill());
			EventUtil.sendEventMessage(player, MessageData.getInstance().getMsgByLang(player, "reward_text_pvp", true).replace("%count%", getConfig().getRewardPvPKill() + ""));
		}
		// Reward fame for kills
		if (getConfig().isRewardFameKillEnabled()) {
			player.setFame(player.getFame() + getConfig().getRewardFameKill());
			EventUtil.sendEventMessage(player, MessageData.getInstance().getMsgByLang(player, "reward_text_fame", true).replace("%count%", getConfig().getRewardFameKill() + ""));
		}
		// Message Kill
		if (BaseConfigLoader.getInstance().getMainConfig().isKillerMessageEnabled()) {
			EventUtil.messageKill(player, event.getTarget());
		}
//		updateScore(player);

	}

	@Override public void onDeath(OnDeathEvent event) {
		scheduleRevivePlayer(event.getTarget(), TIME_RES_PLAYER, _radius);
		event.getTarget().increasePoints(ScoreType.DEATH, 1);
		updateTitle(event.getTarget());
	}

	// VARIOUS METHODS ------------------------------------------------------------------
	private void updateTitle(Player player) {
		// Adjust the title character
		player.setTitle("Kills " + player.getPoints(ScoreType.KILL) + " | " + player.getPoints(ScoreType.DEATH) + " Death");
	}

	private void giveRewardsTeams() {
		if (getPlayerEventManager().getAllEventPlayers().isEmpty())
			return;

		List<IParticipant> listOrdered = SortUtils.getOrdered(getPlayerEventManager().getAllEventParticipants(), ScoreType.KILL).get(0);

		RewardHelper.newInstance().addReward(1, getConfig().getReward()).setScoreType(ScoreType.DEATH).setParticipants(listOrdered).setOrder(SortUtils.Order.ASCENDENT).distribute(AnnounceType.WINNER);
	}

	private void showTime() {
		Duration d = Duration.ofSeconds(EventEngineManager.getInstance().getTime());
		getPlayerEventManager().getAllEventPlayers().forEach(p -> EventUtil.sendEventScreenMessage(p, d.toMinutesPart() + ":" + d.toSecondsPart(),(int)d.getSeconds()));
	}
}