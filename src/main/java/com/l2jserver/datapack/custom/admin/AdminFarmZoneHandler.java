package com.l2jserver.datapack.custom.admin;

import com.l2jserver.datapack.custom.zones.farmzone.FarmZoneManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class AdminFarmZoneHandler implements IAdminCommandHandler {
    private final String[] COMMANDS = {"admin_farmzone;homepage"};

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        String[] split = command.split(" ");
        String resultHtml;
        resultHtml = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/admin/farmzone.html");
        resultHtml = resultHtml.replaceFirst("%active%", FarmZoneManager.getInstance().isActive() ? "enabled" : "disabled");
        switch (split[1]) {
            case "disable" -> FarmZoneManager.getInstance().disable();
            case "enable" -> FarmZoneManager.getInstance().enable();
        }
        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }


    @Override
    public String[] getAdminCommandList() {
        return COMMANDS;
    }
}
