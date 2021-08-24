package com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag.config;

import com.google.gson.annotations.SerializedName;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;
import com.l2jserver.datapack.eventengine.model.config.TeamConfig;
import com.l2jserver.datapack.eventengine.model.holder.EItemHolder;

import java.util.List;

public class CTFEventConfig extends AbstractEventConfig {

	@SerializedName("flagNpcId") private int mFlagNpcId;
	@SerializedName("holderNpcId") private int mHolderNpcId;
	@SerializedName("pointsConquerFlag") private int mPointsConquerFlag;
	@SerializedName("pointsKill") private int mPointsKill;
	@SerializedName("countTeam") private int mCountTeam;
	@SerializedName("teams") private List<TeamConfig> mTeams;

	public int getFlagNpcId() {
		return mFlagNpcId;
	}

	public int getHolderNpcId() {
		return mHolderNpcId;
	}

	public int getPointsConquerFlag() {
		return mPointsConquerFlag;
	}

	public int getPointsKill() {
		return mPointsKill;
	}

	public int getCountTeam() {
		return mCountTeam;
	}

	public List<TeamConfig> getTeams() {
		return mTeams;
	}

	public List<Integer> getZoneIds() {
		return zoneIds;
	}
}

