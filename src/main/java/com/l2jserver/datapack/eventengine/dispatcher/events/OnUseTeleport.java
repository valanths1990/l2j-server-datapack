package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.Location;

public class OnUseTeleport extends ListenerEvent {
	private final Player player;
	private final Location location;

	public OnUseTeleport(Player player, Location location) {
		this.location = location;
		this.player = player;
	}

	public Location getLocation() {
		return location;
	}

	public Player getPlayer() {
		return player;
	}

	@Override public ListenerType getType() {
		return ListenerType.ON_USE_TELEPORT;
	}
}
