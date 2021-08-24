package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;


public class OnDoorActionEvent extends ListenerEvent {
	Player player;
	L2DoorInstance door;

	public OnDoorActionEvent(Player player,L2DoorInstance door){
		this.player=player;
		this.door=door;
	}

	public Player getPlayer() {
		return player;
	}

	public L2DoorInstance getDoor() {
		return door;
	}

	@Override public ListenerType getType() {
		return ListenerType.ON_DOOR_ACTION;
	}
}
