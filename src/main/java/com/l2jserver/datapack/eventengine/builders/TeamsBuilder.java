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
package com.l2jserver.datapack.eventengine.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;
import com.l2jserver.datapack.eventengine.enums.DistributionType;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.model.entity.Team;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.Location;

public class TeamsBuilder {
	private static final Logger LOGGER = Logger.getLogger(TeamsBuilder.class.getName());

	private DistributionType mDistribution = DistributionType.TEAM;
	private final Collection<Player> mPlayers = new ArrayList<>();

	private final List<Team> mTeams = new ArrayList<>();

	public TeamsBuilder addTeam(List<LocationHolder> locations) {
		mTeams.add(new Team("", TeamType.WHITE, locations));
		return this;
	}

	public TeamsBuilder addTeams(Collection<? extends TeamConfig> teamsConfig) {
		teamsConfig.forEach(t -> {
			mTeams.add(new Team(t.getName(), t.getColor()));
		});
		return this;
	}

	public TeamsBuilder addTeam(TeamConfig team, List<LocationHolder> locs) {
		mTeams.add(new Team(team.getName(), team.getColor(), locs));
		return this;
	}

	public TeamsBuilder setPlayers(Collection<Player> list) {
		mPlayers.addAll(list);
		return this;
	}

	public TeamsBuilder setDistribution(DistributionType type) {
		mDistribution = type;
		return this;
	}

	private List<Team> distributePlayers(List<Team> teams) {
		switch (mDistribution) {
			case SINGLE -> mPlayers.forEach(p -> {
				Team t = teams.get(0);
				t.addMember(p);
				p.setTeam(t);
			});
			case TEAM -> {
				int i = 0;
				for (Player player : mPlayers) {
					player.setTeam(teams.get(i));
					teams.get(i).addMember(player);

					if (teams.size() <= (i + 1))
						i = 0;
					else
						i++;
				}
			}
			default -> {

			}
		}
		return teams;
	}

	public List<Team> build() {
		if (mTeams.size() <= 0) {
			LOGGER.warning(TeamsBuilder.class.getSimpleName() + ": The count of teams can't be zero!");
			return null;
		}

		return distributePlayers(mTeams);
	}
}