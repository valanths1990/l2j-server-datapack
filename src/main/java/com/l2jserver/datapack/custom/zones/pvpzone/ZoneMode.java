package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.events.listeners.FunctionEventListener;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

import java.util.ArrayList;
import java.util.List;

public abstract class ZoneMode {

	protected List<AbstractEventListener> listeners = new ArrayList<>();
	protected L2PvpZone currentZone;

	protected ZoneMode(L2PvpZone currentZone) {
		this.currentZone = currentZone;
	}

	protected void registerEvent(EventType type) {
		listeners.add(Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), type, this::receivedEvent, this)));
	}

	public abstract void onZoneModeEnd();
	public abstract void onZoneModeStart();
	protected abstract TerminateReturn receivedEvent(IBaseEvent event);
	protected void removeAllListeners() {
		listeners.forEach(AbstractEventListener::unregisterMe);
		listeners.clear();
	}

}
