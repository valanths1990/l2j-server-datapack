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
package com.l2jserver.datapack.eventengine.managers;

import java.util.*;

import com.l2jserver.datapack.eventengine.enums.ScoreType;
import com.l2jserver.datapack.eventengine.model.entity.Team;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.model.entity.Player;

/**
 * @author fissban
 */
public class TeamsManagers
{
	private final Map<TeamType, Team> _teams = new HashMap<>();
	
	public void createTeams(TeamsBuilder builder, int instanceId)
	{
		// TODO: do something if the teams object is null
		List<Team> teams = builder.build();
		for (Team team : teams)
		{
			_teams.put(team.getTeamType(), team);
			team.addInstanceIdToSpawns(instanceId);
		}
		teams.clear();
	}
	
	/**
	 * Get the collection of created teams.
	 * @return
	 */
	public Collection<Team> getAllTeams()
	{
		return _teams.values();
	}
	
	/**
	 * Get a team by type.
	 * @param type
	 * @return
	 */
	public Team getTeam(TeamType type)
	{
		return _teams.get(type);
	}
	
	/**
	 * Get the team of player.
	 * @param player
	 * @return
	 */
	public Team getPlayerTeam(Player player)
	{
		return _teams.get(player.getTeamType());
	}
	
	/**
	 * Get the team spawn.
	 * @param team
	 * @return LocationHolder
	 */
	public LocationHolder getTeamSpawn(TeamType team)
	{
		return _teams.get(team).getRndSpawn();
	}

}