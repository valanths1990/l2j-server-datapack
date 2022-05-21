package com.l2jserver.datapack.custom.trainingcamp;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.custom.PvPAnnounceManager;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.zone.type.L2TrainingZone;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;

public class TrainingCampManager {

    private final IBypassHandler trainingCampHandler;
    private final List<L2TrainingZone> zones;
    private final Map<L2PcInstance, TrainingCampSession> onGoingFights = new ConcurrentHashMap<>();
    private final Queue<L2PcInstance> queue = new ConcurrentLinkedQueue<>();
    private final Map<L2PcInstance, Autobot> registered = new ConcurrentHashMap<>();
    private final Map<L2PcInstance, ScheduledFuture<?>> cancelRegistration = new ConcurrentHashMap<>();

    //    private final Map<L2PcInstance, InstanceWorld>
    private TrainingCampManager() {
        trainingCampHandler = new TrainingCampHandler();
        BypassHandler.getInstance().registerHandler(trainingCampHandler);

        zones = new ArrayList<>(ZoneManager.getInstance().getAllZones(L2TrainingZone.class));

        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::checkForRegisteredPlayers, 1000, 1000);
    }

    public void checkForRegisteredPlayers() {
        if (queue.isEmpty()) return;

        L2TrainingZone zone = getFreeTrainingZone();
        if (zone == null) return;

        L2PcInstance player = queue.remove();
        Autobot bot = registered.remove(player);

        TrainingCampSession session = new TrainingCampSession(player, bot, zone);
        player.sendMessage("In few moments you will be teleported to Arena.");
        ScheduledFuture<?> future = ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            cancelRegistration.remove(player);
            session.run();
        }, 10000);
        cancelRegistration.put(player, future);
    }

    public void register(L2PcInstance player, Autobot autobot) {
        if (!onGoingFights.containsKey(player)) {
            return;
        }
        registered.put(player, autobot);
        queue.add(player);
    }

    public void cancelRegistration(L2PcInstance player) {
        if (cancelRegistration.containsKey(player)) {
            cancelRegistration.remove(player).cancel(true);
        }
        registered.remove(player);
        queue.remove(player);
    }

    public L2TrainingZone getFreeTrainingZone() {
        return zones.stream()
                .filter(z -> z.getPlayersInside().stream().filter(p -> !p.inObserverMode()).count() == 0)
                .findFirst()
                .orElse(null);
    }

    public static TrainingCampManager getInstance() {
        return TrainingCampManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final TrainingCampManager instance = new TrainingCampManager();
    }

    public static void main(String[] args) {
        TrainingCampManager.getInstance();
    }

}
