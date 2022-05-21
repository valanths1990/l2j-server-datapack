package com.l2jserver.datapack.autobots.models;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ScheduledSpawnInfo {
    @NotNull
    private final String botName;
    @NotNull
    private final String loginTime;
    @NotNull
    private final String logoutTime;


    public ScheduledSpawnInfo(@NotNull String botName, @NotNull String loginTime, @NotNull String logoutTime) {
        this.botName = botName;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    @NotNull
    public final String getBotName() {
        return this.botName;
    }

    @NotNull
    public final String getLoginTime() {
        return this.loginTime;
    }

    @NotNull
    public final String getLogoutTime() {
        return this.logoutTime;
    }

    @NotNull
    public final ScheduledSpawnInfo copy(@NotNull String botName, @NotNull String loginTime, @NotNull String logoutTime) {
        return new ScheduledSpawnInfo(botName, loginTime, logoutTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledSpawnInfo that = (ScheduledSpawnInfo) o;
        return botName.equals(that.botName) && loginTime.equals(that.loginTime) && logoutTime.equals(that.logoutTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(botName, loginTime, logoutTime);
    }

    @NotNull
    public String toString() {
        return "ScheduledSpawnInfo(botName=" + this.botName + ", loginTime=" + this.loginTime + ", logoutTime=" + this.logoutTime + ")";
    }

}
