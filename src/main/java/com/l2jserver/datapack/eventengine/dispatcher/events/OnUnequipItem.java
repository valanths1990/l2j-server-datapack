package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

public class OnUnequipItem extends ListenerEvent {
	private Player player;
	private L2ItemInstance item;

	public OnUnequipItem(Player player, L2ItemInstance item) {
		this.player = player;
		this.item = item;
	}

	public Player getPlayer() {
		return player;
	}

	public L2ItemInstance getItem() {
		return item;
	}

	@Override public ListenerType getType() {
		return ListenerType.ON_UNEQUIP_ITEM;
	}
}
