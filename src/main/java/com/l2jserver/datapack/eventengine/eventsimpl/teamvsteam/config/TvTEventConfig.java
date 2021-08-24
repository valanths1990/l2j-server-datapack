package com.l2jserver.datapack.eventengine.eventsimpl.teamvsteam.config;

import com.google.gson.annotations.SerializedName;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;

import java.util.List;

public class TvTEventConfig extends AbstractEventConfig {

	@SerializedName("teams") private List<TeamConfig> teams;

	public List<TeamConfig> getTeams() {
		return teams;
	}

}

