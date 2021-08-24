package com.l2jserver.datapack.custom.reward.pojo;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Rewards {
	private Map<String, Reward> rewards;

	@ConstructorProperties("rewards") public Rewards(List<Reward> bounties) {
		Objects.requireNonNull(bounties);
		this.rewards = new HashMap<>();
		bounties.stream().forEach(b -> {
			this.rewards.put(b.type, b);
		});
	}

	public Map<String, Reward> getReward() {
		return rewards;
	}
}
