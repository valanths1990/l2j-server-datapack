package com.l2jserver.datapack.autobots.autofarm;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.ui.AutofarmUi;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class AutofarmCommandHandler implements IBypassHandler {

    private static final String[] COMMANDS = {"autofarm"};

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        if (!command.startsWith("autofarm") || !(activeChar instanceof Autobot)) return false;
        if (command.equals("autofarm")) {
            AutofarmUi.index((Autobot) activeChar);
            return true;
        }

        String[] splitCommand = command.replaceFirst("autofarm", "").split(" ");

        switch (splitCommand[0]) {
            case "start" -> {
                AutofarmManager.getInstance().startFarm((Autobot) activeChar);
                AutofarmUi.index((Autobot) activeChar);
            }
            case "stop" -> {
                AutofarmManager.getInstance().stopFarm((Autobot) activeChar);
                AutofarmUi.index((Autobot) activeChar);
            }
            case "close" -> AutofarmUi.closeWindow((Autobot) activeChar);
            case "todo" -> activeChar.sendMessage("not implemented yet");
        }

        return false;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
