package com.l2jserver.datapack.eventengine.model.packet;

import com.l2jserver.datapack.eventengine.enums.MessageType;
import com.l2jserver.datapack.eventengine.interfaces.IGamePacket;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;

public class CreatureSayPacket implements IGamePacket {

    private final CreatureSay mPacket;

    public CreatureSayPacket(int objectId, MessageType messageType, String charName, String message) {
        mPacket = new CreatureSay(objectId, messageType.getValue(), charName, message);
    }

    public CreatureSay getL2Packet() {
        return mPacket;
    }
}
