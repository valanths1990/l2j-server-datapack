package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnPlayableHitEvent extends ListenerEvent {
	private Player attacker;
	private Player attacked;
	private double damage;

	public Player getAttacker() {
		return attacker;
	}

	public Player getAttacked() {
		return attacked;
	}

	public double getDamage() {
		return damage;
	}

	public OnPlayableHitEvent(Player attacker, Player attacked, double damage) {
		this.attacker = attacker;
		this.attacked = attacked;
		this.damage = damage;

	}

	@Override public ListenerType getType() {
		return ListenerType.ON_PLAYABLE_HIT;
	}
}
