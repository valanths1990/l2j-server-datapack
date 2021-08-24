package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

public class MultiplayerZone extends L2PvpZone {
	public MultiplayerZone(int id) {
		super(id);
	}

	@Override protected void onEnter(L2Character character) {
		super.onEnter(character);
		System.out.println("worked");
	}
}
