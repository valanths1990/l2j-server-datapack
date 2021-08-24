package com.l2jserver.datapack.custom.classes;

import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

public class TimerPacket extends L2GameServerPacket {
	@Override protected void writeImpl() {
		writeC(0x3D1040);
		writeD(1);
	}
}
