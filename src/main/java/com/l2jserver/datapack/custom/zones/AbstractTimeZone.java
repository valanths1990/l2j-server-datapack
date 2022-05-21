package com.l2jserver.datapack.custom.zones;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.AbstractScript;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.zone.L2ZoneRespawn;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.type.L2FarmZone;
import com.l2jserver.gameserver.network.serverpackets.ExSendUIEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTimeZone {
    protected IBypassHandler handler = null;
    protected boolean active = false;
    protected L2ZoneType currentZone;
    protected List<L2ZoneType> zones = null;
    protected List<Location> zoneLocations;
    protected Random rand = new Random();
    protected Integer remainingTime;
    protected Integer zoneTime;
    protected ScheduledFuture<?> future;
    protected List<AbstractEventListener> registeredListeners = new CopyOnWriteArrayList<>();

    protected AbstractTimeZone() {
        ZoneManager.getInstance().parseDirectory(Configuration.server().getDatapackRoot() + "/data/custom", true);
    }

    protected void registerListeners() {
        registeredListeners.add(currentZone.addListener(new ConsumerEventListener(currentZone, EventType.ON_CREATURE_ZONE_EXIT, this::onExitZone, this)));
        registeredListeners.add(currentZone.addListener(new ConsumerEventListener(currentZone, EventType.ON_CREATURE_ZONE_ENTER, this::onEnterZone, this)));
    }


    protected void unregisterListeners() {
        registeredListeners.forEach(l -> currentZone.removeListener(l));
        registeredListeners.clear();
    }

    protected void onExitZone(IBaseEvent event) {
        OnCreatureZoneExit e = (OnCreatureZoneExit) event;
        if (e.getCreature() instanceof L2PcInstance) {
            if (zones.contains(e.getZone())) {
                e.getCreature().sendPacket(new ExSendUIEvent((L2PcInstance) e.getCreature(), true, false, 0, 0, null));
            }
        }
    }

    protected void onEnterZone(IBaseEvent event) {
        OnCreatureZoneEnter e = (OnCreatureZoneEnter) event;
        if (e.getCreature() instanceof L2PcInstance) {
            if (zones.contains(e.getZone())) {
                e.getCreature().sendPacket(new ExSendUIEvent((L2PcInstance) e.getCreature(), false, false, remainingTime, 0, null));
            }
        }
    }

    protected void clearCurrentZone() {
        if (currentZone == null) {
            return;
        }
        unregisterListeners();
        active = false;
        currentZone.setEnabled(false);
        teleportPlayersOut();
    }

    protected abstract void teleportPlayersOut();

    public void disable() {
        if (!active) {
            return;
        }
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
        clearCurrentZone();
        active = false;
    }

    public void enable() {
        if (active) {
            return;
        }
        scheduleNewZone();
        active = true;
    }

    protected void startNewZone() {
        clearCurrentZone();
        currentZone = zones.get(rand.nextInt(zones.size()));
        currentZone.setEnabled(true);
        registerListeners();
        zoneLocations = ((L2ZoneRespawn) currentZone).getSpawns();
        active = true;
        remainingTime = zoneTime;
        startCountdown();
    }

    protected void startCountdown() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                remainingTime--;
                if (remainingTime <= 0) {
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    public L2ZoneType getCurrentZone() {
        return currentZone;
    }

    public void scheduleNewZone() {
        future = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::startNewZone, 0, zoneTime, TimeUnit.SECONDS);
    }

    public List<Location> getSpawnLocations() {
        return Collections.unmodifiableList(this.zoneLocations);
    }

    public Location getRandomLocation() {
        return this.zoneLocations.get(rand.nextInt(this.zoneLocations.size()));
    }

    public boolean isActive() {
        return active;
    }

}
