package com.l2jserver.datapack.eventengine.model.config;

import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.google.gson.annotations.SerializedName;
import com.l2jserver.gameserver.GeoData;

import java.util.List;

public class TeamConfig {

	@SerializedName("name") private String mName;
	@SerializedName("color") private String mColor;

	//	private List<LocationHolder> mLocations;
	public TeamConfig() {
	}

	public TeamConfig(String mName, String mColor) {
		this.mName = mName;
		this.mColor = mColor;
	}

	public String getName() {
		return mName;
	}

	public TeamType getColor() {
		return TeamType.getType(mColor);
	}


}
