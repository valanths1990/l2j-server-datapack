package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.events.listeners.FunctionEventListener;
import com.l2jserver.gameserver.model.events.returns.AbstractEventReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

import java.util.*;

public abstract class ZoneMode {

    protected List<AbstractEventListener> listeners = new ArrayList<>();
    protected L2PvpZone currentZone;
    protected List<EventType> functionEvents = new ArrayList<>();
    protected List<EventType> consumerEvents = new ArrayList<>();

    protected ZoneMode(L2PvpZone currentZone) {
        this.currentZone = currentZone;
        listeners.add(this.currentZone.addListener(new ConsumerEventListener(currentZone, EventType.ON_CREATURE_ZONE_ENTER, this::onZoneEnter, this)));
        listeners.add(this.currentZone.addListener(new ConsumerEventListener(currentZone, EventType.ON_CREATURE_ZONE_EXIT, this::onZoneExit, this)));
    }

    protected void registerFunction(EventType type) {
        functionEvents.add(type);
        listeners.add(Containers.Global().addListener(new FunctionEventListener(Containers.Global(), type, this::receivedEvent, this)));
    }

    protected void registerConsumer(EventType type) {
        consumerEvents.add(type);
        listeners.add(Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), type, this::receivedEvent, this)));
    }

    protected void onZoneEnter(IBaseEvent event) {
        OnCreatureZoneEnter enterEvent = (OnCreatureZoneEnter) event;
        if (enterEvent.getCreature() instanceof L2PcInstance) {
            ((L2PcInstance) enterEvent.getCreature()).addEventListener(new PvPListener((L2PcInstance) enterEvent.getCreature()));
        }
    }

    protected void onZoneExit(IBaseEvent event) {
        OnCreatureZoneExit enterEvent = (OnCreatureZoneExit) event;
        if (enterEvent.getCreature() instanceof L2PcInstance) {
            ((L2PcInstance) enterEvent.getCreature()).removeEventListener(PvPListener.class);
        }
    }

    protected abstract void onZoneModeEnd();

    public abstract void onZoneModeStart();

    protected abstract AbstractEventReturn receivedEvent(IBaseEvent event);

    public abstract String getZoneModeName();

    protected void removeAllListeners() {
        listeners.forEach(AbstractEventListener::unregisterMe);
        listeners.clear();
    }

}
