package com.l2jserver.datapack.autobots.models;

import com.l2jserver.gameserver.model.base.ClassId;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AutobotInfo {
    private final String name;
    private final int level;
    private final boolean isOnline;
    private final ClassId classId;
    private final int botId;
    private final int clanId;

    public AutobotInfo(String name, int level, boolean isOnline, ClassId classId, int botId, int clanId) {
        this.name = name;
        this.level = level;
        this.isOnline = isOnline;
        this.classId = classId;
        this.botId = botId;
        this.clanId = clanId;
    }

    @NotNull
    public final AutobotInfo copy(@NotNull String name, int level, boolean isOnline, @NotNull ClassId classId, int botId, int clanId) {
        return new AutobotInfo(name, level, isOnline, classId, botId, clanId);
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public ClassId getClassId() {
        return classId;
    }

    public int getBotId() {
        return botId;
    }

    public int getClanId() {
        return clanId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutobotInfo that = (AutobotInfo) o;
        return level == that.level && isOnline == that.isOnline && botId == that.botId && clanId == that.clanId && Objects.equals(name, that.name) && classId == that.classId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, isOnline, classId, botId, clanId);
    }

    @Override
    public String toString() {
        return "AutobotInfo{" +
                "name='" + name + '\'' +
                ", level=" + level +
                ", isOnline=" + isOnline +
                ", classId=" + classId +
                ", botId=" + botId +
                ", clanId=" + clanId +
                '}';
    }
}
