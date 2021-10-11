package com.l2jserver.datapack.custom.achievement.pojo;

import java.util.Arrays;
import java.util.List;

public class AchievementPojo {
    private int id;
    private String title;
    private String description;
    private List<StatePojo> state;
    private String time;
    private List<String> classId;
    private boolean repeating = false;
    private RewardPojo reward;
    private ConditionsPojo conditions;
    private Integer[] unlock;
    private Integer[] require;
    private int minLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public RewardPojo getReward() {
        return reward;
    }

    public void setReward(RewardPojo reward) {
        this.reward = reward;
    }

    public ConditionsPojo getCondition() {
        return conditions;
    }

    public void setCondition(ConditionsPojo conditions) {
        this.conditions = conditions;
    }

    public Integer[] getUnlock() {
        return unlock;
    }

    public void setUnlock(Integer[] unlock) {
        this.unlock = unlock;
    }

    public Integer[] getRequire() {
        return require;
    }

    public void setRequire(Integer[] require) {
        this.require = require;
    }

    public List<StatePojo> getState() {
        return state;
    }

    public void setState(List<StatePojo> state) {
        this.state = state;
    }

    public List<String> getClassId() {
        return classId;
    }

    public void setClassId(List<String> ClassId) {
        this.classId = ClassId;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public ConditionsPojo getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsPojo conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "AchievementPojo [classId=" + classId + ", conditions=" + conditions + ", describtion=" + description
                + ", id=" + id + ", minLevel=" + minLevel + ", repeating=" + repeating + ", require="
                + Arrays.toString(require) + ", reward=" + reward + ", state=" + state + ", time=" + time + ", title="
                + title + ", unlock=" + Arrays.toString(unlock) + "]";
    }

}
