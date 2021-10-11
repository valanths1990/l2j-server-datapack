package com.l2jserver.datapack.custom.achievement.pojo;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.custom.achievement.exception.MissingRewardException;

import static java.util.Objects.requireNonNull;

@JsonClassDescription("reward")
public class RewardPojo {
    private final List<IRewardOperation> rewardOperations;

    @ConstructorProperties("rewards")
    public RewardPojo(List<IRewardOperation> rewardOperations) throws MissingRewardException {
        if (rewardOperations == null || rewardOperations.isEmpty()) {
            throw new MissingRewardException("Reward must be given.");
        }
        this.rewardOperations = rewardOperations;
    }

    @JsonProperty("rewardAction")
    public List<IRewardOperation> getRewardOperations() {
        return Collections.unmodifiableList(rewardOperations);
    }

}
