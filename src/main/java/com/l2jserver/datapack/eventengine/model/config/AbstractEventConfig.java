package com.l2jserver.datapack.eventengine.model.config;

import com.google.gson.annotations.SerializedName;
import com.l2jserver.datapack.eventengine.enums.DistributionType;
import com.l2jserver.datapack.eventengine.enums.ScoreType;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.interfaces.IEventConfig;
import com.l2jserver.datapack.eventengine.model.holder.EItemHolder;

import java.util.List;

public class AbstractEventConfig implements IEventConfig {

	@SerializedName("enabled") protected boolean enabled;
	@SerializedName("instanceFile") protected String instanceFile;
	@SerializedName("reward") protected List<EItemHolder> reward;
	@SerializedName("rewardKillEnabled") protected boolean rewardKillEnabled;
	@SerializedName("rewardKill") protected List<EItemHolder> rewardKill;
	@SerializedName("rewardPvPKillEnabled") protected boolean rewardPvPKillEnabled;
	@SerializedName("rewardPvPKill") protected int rewardPvPKill;
	@SerializedName("rewardFameKillEnabled") protected boolean rewardFameKillEnabled;
	@SerializedName("rewardFameKill") protected int rewardFameKill;
	@SerializedName("zoneIds") protected List<Integer> zoneIds;
	@SerializedName("distributionType")protected DistributionType type;
	@SerializedName("ScoreType") protected ScoreType scoreType;

	public ScoreType getScoreType() {
		return scoreType;
	}

	public DistributionType getType() {
		return type;
	}
	public boolean isEnabled() {
		return enabled;
	}

	public String getInstanceFile() {
		return instanceFile;
	}

	public List<EItemHolder> getReward() {
		return reward;
	}

	public boolean isRewardKillEnabled() {
		return rewardKillEnabled;
	}

	public List<EItemHolder> getRewardKill() {
		return rewardKill;
	}

	public boolean isRewardPvPKillEnabled() {
		return rewardPvPKillEnabled;
	}

	public int getRewardPvPKill() {
		return rewardPvPKill;
	}

	public boolean isRewardFameKillEnabled() {
		return rewardFameKillEnabled;
	}

	public int getRewardFameKill() {
		return rewardFameKill;
	}

	public List<Integer> getZoneIds() {
		return zoneIds;
	}
}
