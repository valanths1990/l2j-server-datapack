package com.l2jserver.datapack.custom.store;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class Store implements IBypassHandler {
    private String[] COMMANDS = {
            "store;homepage"
    };

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String resultHtml = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/store/homepage.html");
        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
