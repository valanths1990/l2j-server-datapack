package com.l2jserver.datapack.eventengine.eventsimpl.siege.config;

import com.google.gson.annotations.SerializedName;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;

import java.util.List;

public class SiegeConfig extends AbstractEventConfig {
	@SerializedName("teams") private List<TeamConfig> teams;
	@SerializedName("towerId")private Integer towerId;

	public Integer getTowerId() {
		return towerId;
	}
	public List<TeamConfig> getTeams() {
		return teams;
	}
}
