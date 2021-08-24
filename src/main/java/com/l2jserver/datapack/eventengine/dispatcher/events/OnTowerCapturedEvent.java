package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;

public class OnTowerCapturedEvent extends ListenerEvent {
	private Player player;
	private CaptureTower tower;

	public OnTowerCapturedEvent(Player player,CaptureTower tower){
		this.player = player;
		this.tower =tower;
	}
	public Player getPlayer() {
		return player;
	}

	public CaptureTower getTower() {
		return tower;
	}
	@Override public ListenerType getType() {
		return ListenerType.ON_TOWER_CAPTURED;
	}
}
