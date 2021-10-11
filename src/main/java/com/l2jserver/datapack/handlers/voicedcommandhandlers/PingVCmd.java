package com.l2jserver.datapack.handlers.voicedcommandhandlers;

import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author vGodFather
 */
public class PingVCmd implements IVoicedCommandHandler
{
    private static final String[] VOICED_COMMANDS =
            {
                    "ping",
            };

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
    {
//       activeChar.getQuic
//        Pinger.getPing(activeChar);
        return true;
    }

    @Override
    public String[] getVoicedCommandList()
    {
        return VOICED_COMMANDS;
    }
}