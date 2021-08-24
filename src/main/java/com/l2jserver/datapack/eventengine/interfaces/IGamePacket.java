package com.l2jserver.datapack.eventengine.interfaces;

import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

public interface IGamePacket {

    L2GameServerPacket getL2Packet();
}
