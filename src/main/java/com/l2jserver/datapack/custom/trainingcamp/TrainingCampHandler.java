package com.l2jserver.datapack.custom.trainingcamp;

import com.l2jserver.datapack.autobots.AutobotsManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class TrainingCampHandler implements IBypassHandler {
    private static final String[] COMMANDS = {"homepage;trainingcamp",
            "homepage;trainingcamp register",
            "homepage;trainingcamp unregister",
            "homepage;trainingcamp observe"};


    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String [] splitCommand = command.split(" ");
        String htmlResult = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(),"data/custom/trainingcamp/homepage.html");

        if(splitCommand.length>1){
            switch (splitCommand[1]){
                case "register"->{
                    TrainingCampManager.getInstance().register(activeChar, AutobotsManager.getInstance().createRandomFakePlayer(0,0,0,85,false));
                }
            }
        }

            CommunityBoardHandler.separateAndSend(htmlResult,activeChar);
        return false;
    }

    @Override
    public String[] getBypassList() {
        return new String[0];
    }
}
