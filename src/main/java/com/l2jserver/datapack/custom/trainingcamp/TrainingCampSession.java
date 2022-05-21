package com.l2jserver.datapack.custom.trainingcamp;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Instance;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTrainingEnd;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTrainingStart;
import com.l2jserver.gameserver.model.zone.type.L2TrainingZone;

import java.util.Timer;
import java.util.TimerTask;


public class TrainingCampSession implements Runnable {

    private final L2PcInstance player;
    private final Autobot bot;
    private final L2TrainingZone zone;
    private final Timer timerTask;
    private int remainingTime = 60;

    public TrainingCampSession(L2PcInstance player, Autobot bot, L2TrainingZone zone) {
        this.player = player;
        this.bot = bot;
        this.zone = zone;
        timerTask = new Timer();
    }

    @Override
    public void run() {
        EventDispatcher.getInstance().notifyEvent(new OnPlayerTrainingStart(player), player);
        start();

        timerTask.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                remainingTime--;
                if (remainingTime <= 0) {
                    timerTask.cancel();
                    end();
                }
            }
        }, 1000, 1000);

        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                Instance instance = InstanceManager.getInstance().getInstance(zone.getInstanceId());
                instance.getDoors().stream()
                        .filter(d -> d != null && !d.getOpen())
                        .forEach(L2DoorInstance::openMe);
            }
        }, 10000);

    }

    public void start() {

        Location botLocation = zone.getSpawns().get(0);
        botLocation.setInstanceId(zone.getInstanceId());

        Location playerLocation = zone.getSpawns().get(zone.getSpawns().size() / 2);
        playerLocation.setInstanceId(zone.getInstanceId());
        bot.setLastLocation();
        bot.teleToLocation(botLocation);

        player.setLastLocation();
        player.teleToLocation(playerLocation);


    }

    public void end() {
        EventDispatcher.getInstance().notifyEvent(new OnPlayerTrainingEnd(player), player);
        bot.deleteMe();
        AutobotsDao.getInstance().deleteBot(bot.getObjectId());

        player.teleToLocation(player.getLastLocation());

    }

    public L2PcInstance getPlayer() {
        return player;
    }

    public Autobot getBot() {
        return bot;
    }

    public L2TrainingZone getZone() {
        return zone;
    }

    public Timer getTimerTask() {
        return timerTask;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
