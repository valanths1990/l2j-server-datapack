package com.l2jserver.datapack.autobots.utils.packets;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

public class GMViewBuffs extends L2GameServerPacket {

    private final Autobot autobot;

    public GMViewBuffs(Autobot autobot) {
        this.autobot = autobot;
    }

    @Override
    protected void writeImpl() {
        writeC(0x91);
        writeS(autobot.getName());
        writeD(autobot.getEffectList().getEffects().size());
        for (BuffInfo skill : autobot.getEffectList().getEffects()) {
            writeD(0);
            writeD(skill.getSkill().getLevel());
            writeD(skill.getSkill().getId());
            writeC(0);
        }
    }
}
