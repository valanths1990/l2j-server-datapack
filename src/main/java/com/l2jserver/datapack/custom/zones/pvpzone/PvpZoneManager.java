package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.custom.Activity.ActivityManager;
import com.l2jserver.gameserver.custom.Activity.IActivity;
import com.l2jserver.gameserver.custom.Activity.Priority;
import com.l2jserver.datapack.custom.reward.RewardManager;
import com.l2jserver.datapack.custom.zones.AbstractTimeZone;
import com.l2jserver.datapack.custom.zones.pvpzone.zonemodes.Deathmatch;
import com.l2jserver.datapack.custom.zones.pvpzone.zonemodes.DominationMode;
import com.l2jserver.datapack.custom.zones.pvpzone.zonemodes.MultiplayerZone;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.Participant;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;
import com.l2jserver.gameserver.network.serverpackets.ExShowPVPMatchRecord;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class PvpZoneManager extends AbstractTimeZone {

    private final Map<L2PcInstance, Integer> currentZoneScore = new ConcurrentHashMap<>();
    private static final List<Class<? extends ZoneMode>> modes = new ArrayList<>();
    private ZoneMode z;

    static {
        modes.add(Deathmatch.class);
        modes.add(DominationMode.class);
        modes.add(MultiplayerZone.class);
        Collections.shuffle(modes);
    }

    private PvpZoneManager() {
        super();
        this.zoneTime = Configuration.customs().getPvpZoneTime();
        zones = new ArrayList<>(ZoneManager.getInstance().getAllZones(L2PvpZone.class));
        zones.forEach(z -> z.setEnabled(false));
        Collections.shuffle(zones);
        handler = new PvpZoneHandler();
        BypassHandler.getInstance().registerHandler(handler);
        scheduleNewZone();
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_PVP_KILL, this::onPvpKill, this));
    }

    @Override
    protected void registerListeners() {
        super.registerListeners();
    }

    @Override
    protected void startNewZone() {
        try {
            super.startNewZone();
            Class<? extends ZoneMode> modeClass = modes.get(Rnd.get(modes.size()));
            z = modeClass.getDeclaredConstructor(L2PvpZone.class).newInstance(currentZone);
            z.onZoneModeStart();
            ActivityManager.getInstance().registerAction(new PvPActivity(), Duration.ofSeconds(remainingTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onPvpKill(IBaseEvent event) {

        OnPlayerPvPKill e = (OnPlayerPvPKill) event;
        if (currentZone != null && !currentZone.isCharacterInZone(e.getActiveChar())) {
            return;
        }
        if (!currentZoneScore.containsKey(e.getActiveChar())) {
            currentZoneScore.put(e.getActiveChar(), 1);
            return;
        }
        currentZoneScore.put(e.getActiveChar(), currentZoneScore.get(e.getActiveChar()) + 1);
    }

    private void showScoreBoardAndReward() {
        List<Participant> participants = new ArrayList<>();
        currentZoneScore.forEach((player, score) -> {
            Participant p = new Participant(player.getName(), score);
            participants.add(p);
        });
        currentZoneScore.keySet().forEach(p -> p.sendPacket(new ExShowPVPMatchRecord(participants)));

        List<L2PcInstance> top3Players = currentZoneScore.entrySet().stream().sorted(Map.Entry.<L2PcInstance, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

        IntStream.range(0, top3Players.size()).forEach(i -> {
            RewardManager.getInstance().rewardPlayer(top3Players.get(i), "pvpzoneTop" + i + 1);
        });

        currentZoneScore.clear();
    }

    @Override
    protected void clearCurrentZone() {
        super.clearCurrentZone();
        showScoreBoardAndReward();
        if (z != null) {
            z.onZoneModeEnd();
        }
    }

    @Override
    protected void teleportPlayersOut() {
        if (currentZone == null) {
            return;
        }
        currentZone.getPlayersInside().forEach(p -> handler.useBypass("pvpzone;homepage leave", p, null));
    }

    public ZoneMode getZoneMode() {
        return z;
    }

    public Integer getRemainingTime() {
        return this.remainingTime;
    }

    private final class PvPActivity implements IActivity {

        @Override
        public String getBypass() {
            return "Bypass -h pvpzone;homepage join";
        }

        @Override
        public String getImage() {
            return "Crest.crest_%serverId%_%imageName:pvp";
        }

        @Override
        public String getName() {
            return currentZone.getName() + ": " + z.getZoneModeName();
        }

        @Override
        public Priority getPriority() {
            return Priority.MEDIUM;
        }
    }

    public static PvpZoneManager getInstance() {
        return PvpZoneManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final PvpZoneManager instance = new PvpZoneManager();
    }

    public static void main(String[] args) {
        PvpZoneManager.getInstance();
    }
}
