package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.AutobotScheduler;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.LocalDateTime;

public class ActivityPreferences {
    private ActivityType activityType;
    private int uptimeMinutes;
    private String loginTime;
    private String logoutTime;

    public ActivityPreferences(@JsonProperty("activityType") @NotNull ActivityType activityType, @JsonProperty("uptimeMinutes") int uptimeMinutes, @JsonProperty("loginTime") @NotNull String loginTime, @JsonProperty("logoutTime") @NotNull String logoutTime) {
        this.activityType = activityType;
        this.uptimeMinutes = uptimeMinutes;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
    }

    public ActivityPreferences() {
        this.activityType = ActivityType.None;
        this.uptimeMinutes = 60;
        this.loginTime = "09:00";
        this.logoutTime = "20:00";
    }

    public final boolean logoutTimeIsInThePast() {
        LocalDateTime dateTimeNow = LocalDateTime.now(Clock.systemUTC());
        StringBuilder var10000 = new StringBuilder();
        LocalDateTime logoutTime = LocalDateTime.parse(var10000.append(dateTimeNow.getYear())
                .append('-').append(dateTimeNow.getMonthValue() < 10 ? "" + '0' + dateTimeNow.getMonthValue() : dateTimeNow.getMonthValue())
                .append('-').append(dateTimeNow.getDayOfMonth() < 10 ? "" + '0' + dateTimeNow.getDayOfMonth() : dateTimeNow.getDayOfMonth())
                .append(' ').append(this.logoutTime).toString(), AutobotScheduler.getInstance().getFormatter());
        return dateTimeNow.compareTo(logoutTime) > 0;
    }

    public enum ActivityType {
        None,
        Uptime,
        Schedule
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public int getUptimeMinutes() {
        return uptimeMinutes;
    }

    public void setUptimeMinutes(int uptimeMinutes) {
        this.uptimeMinutes = uptimeMinutes;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String logoutTime) {
        this.logoutTime = logoutTime;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
}
