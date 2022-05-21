package com.l2jserver.datapack.autobots;

import com.l2jserver.datapack.autobots.behaviors.preferences.ActivityPreferences;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.models.ScheduledSpawnInfo;
import com.l2jserver.gameserver.ThreadPoolManager;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutobotScheduler {

    private final Map<String, ScheduledSpawnInfo> scheduledBots = AutobotsDao.getInstance().loadScheduleSpawns()
            .stream()
            .collect(Collectors.toConcurrentMap(ScheduledSpawnInfo::getBotName, v -> v, (oldValue, newValue) -> oldValue, () -> new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER)));
   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private AutobotScheduler() {

        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
            LocalDateTime dateTimeNow = LocalDateTime.now(Clock.systemUTC());
            scheduledBots.values().forEach(b -> handleScheduleBotSpawn(dateTimeNow, b));
        }, 1000, 30000, TimeUnit.MILLISECONDS);
    }

    private void handleScheduleBotSpawn(LocalDateTime dateTimeNow, ScheduledSpawnInfo scheduledSpawnInfo) {
        LocalDateTime loginDateTime = LocalDateTime.parse(dateTimeNow.getYear() + "-" + (dateTimeNow.getMonthValue() < 10 ? "0" + dateTimeNow.getMonthValue() : dateTimeNow.getMonthValue()) + "-" + (dateTimeNow.getDayOfMonth() < 10 ? "0" + dateTimeNow.getDayOfMonth() : dateTimeNow.getDayOfMonth()), formatter);
        LocalDateTime logoutDateTime = LocalDateTime.parse(dateTimeNow.getYear() + "-" + (dateTimeNow.getMonthValue() < 10 ? "0" + dateTimeNow.getMonthValue() : dateTimeNow.getMonthValue()) + "-" + (dateTimeNow.getDayOfMonth() < 10 ? "0" + dateTimeNow.getDayOfMonth() : dateTimeNow.getDayOfMonth()), formatter);
        if ((dateTimeNow.isAfter(loginDateTime) || dateTimeNow.isEqual(loginDateTime)) && dateTimeNow.isBefore(logoutDateTime) && !AutobotsManager.getInstance().getActiveBots().containsKey(scheduledSpawnInfo.getBotName())) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                Autobot player = AutobotsDao.getInstance().loadByName(scheduledSpawnInfo.getBotName());
                if (player == null) return;
                AutobotsManager.getInstance().spawnAutobot(player);

            }, 0);
        }
        if ((dateTimeNow.isAfter(logoutDateTime) || dateTimeNow.isEqual(logoutDateTime)) && AutobotsManager.getInstance().getActiveBots().containsKey(scheduledSpawnInfo.getBotName())) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                AutobotsManager.getInstance().getActiveBots().get(scheduledSpawnInfo.getBotName()).despawn();
            }, 0);
        }

    }

    public void addBot(Autobot autobot) {
        if (autobot.getCombatBehavior().getActivityPreferences().getActivityType() == ActivityPreferences.ActivityType.Schedule) {
            ScheduledSpawnInfo spawnInfo = new ScheduledSpawnInfo(autobot.getName(), autobot.getCombatBehavior().getActivityPreferences().getLoginTime(), autobot.getCombatBehavior().getActivityPreferences().getLogoutTime());
            scheduledBots.put(autobot.getName(), spawnInfo);
            LocalDateTime dateTimeNow = LocalDateTime.now(Clock.systemUTC());
            handleScheduleBotSpawn(dateTimeNow, spawnInfo);
        }

    }
    public DateTimeFormatter getFormatter(){
        return this.formatter;
    }
    public void removeBot(Autobot autobot) {
        scheduledBots.remove(autobot.getName());
    }
    public static AutobotScheduler getInstance() {
        return AutobotScheduler.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotScheduler INSTANCE = new AutobotScheduler();
    }
}
