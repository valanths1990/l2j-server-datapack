package com.l2jserver.datapack.eventengine.model.packet;

import com.l2jserver.datapack.eventengine.interfaces.IGamePacket;
import com.l2jserver.gameserver.network.serverpackets.ExShowScreenMessage;

public class ShowScreenMessagePacket implements IGamePacket {

    private final ExShowScreenMessage mPacket;

    public ShowScreenMessagePacket(String text, int time) {
        mPacket = new ExShowScreenMessage(text, time);
    }

    public ExShowScreenMessage getL2Packet() {
        return mPacket;
    }
}
