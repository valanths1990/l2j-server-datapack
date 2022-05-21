package com.l2jserver.datapack.autobots;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.L2GameClient;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;
import com.l2jserver.mmocore.MMOConnection;
import com.l2jserver.mmocore.ReceivablePacket;

import java.net.Socket;
import java.nio.ByteBuffer;

public class AutobotClient extends L2GameClient {

    private final Autobot autobot;

    public AutobotClient(Autobot autobot) {
        super(null);
        setDetached(true);
        this.autobot = autobot;
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        return true;
    }

    @Override
    public boolean encrypt(ByteBuffer buf, int size) {
        return true;
    }

    @Override
    public byte[] enableCrypt() {
        return new byte[0];
    }

    @Override
    public void setState(GameClientState pState) {
    }

    @Override
    public void sendPacket(L2GameServerPacket gsp) {
    }

    @Override
    public byte markToDeleteChar(int charslot) {
        return 0;
    }

    @Override
    public void markRestoredChar(int charslot) {

    }

    @Override
    public L2PcInstance loadCharFromDisk(int charslot) {
        return autobot;
    }

    @Override
    public void close(L2GameServerPacket gsp) {
    }

    @Override
    public void closeNow() {
    }

    @Override
    public void cleanMe(boolean fast) {
    }

    @Override
    public boolean dropPacket() {
        return false;
    }

    @Override
    public void onBufferUnderflow() {
    }

    @Override
    public void onUnknownPacket() {
    }

    @Override
    public void execute(ReceivablePacket<L2GameClient> packet) {

    }

    @Override
    public boolean isDetached() {
        return false;
    }

    @Override
    public String toString() {
        return "AutobotClient{" +
                "autobot=" + autobot +
                '}';
    }
}
