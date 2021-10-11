package com.l2jserver.datapack.custom.achievement.holder;

import java.util.List;

public class AchievementHolder {
    private final int id;
    private final int charId;
    private final List<StateHolder> states;

    public AchievementHolder(int id, int charId, List<StateHolder> states) {
        this.id = id;
        this.charId = charId;
        this.states = states;
    }

    public int getId() {
        return id;
    }

    public int getCharId() {
        return charId;
    }

    public List<StateHolder> getStates() {
        return states;
    }
}

