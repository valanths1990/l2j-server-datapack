package com.l2jserver.datapack.custom.achievement.pojo;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AchievementsListPojo {
    private final List<AchievementPojo> achievementsList;

    @ConstructorProperties("achievements")
    public AchievementsListPojo(List<AchievementPojo> achievementsList) {
        this.achievementsList = achievementsList == null ? Collections.emptyList() : achievementsList;
    }

    @JsonProperty("achievements")
    public List<AchievementPojo> getAchievementsList() {
        return Collections.unmodifiableList(this.achievementsList);
    }

}
