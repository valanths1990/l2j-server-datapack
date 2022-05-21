package com.l2jserver.datapack.custom.admin;

import com.l2jserver.datapack.custom.zones.pvpzone.PvpZoneManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

public class AdminPvpZoneHandler implements IAdminCommandHandler {

    private final String[] COMMANDS = {"admin_pvpzone;homepage"};//, "admin_farmzone", "admin_balance", "admin_bounty", "admin_raidboss", "admin_skin"};

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {

        String[] split = command.split(" ");
        String resultHtml;
        resultHtml = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/admin/pvpzone.html");
        resultHtml = resultHtml.replaceFirst("%active%", PvpZoneManager.getInstance().isActive() ? "enabled" : "disabled");
        switch (split[1]) {
            case "disable" -> PvpZoneManager.getInstance().disable();
            case "enable" -> PvpZoneManager.getInstance().enable();
        }
        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }

    @Override
    public String[] getAdminCommandList() {
        return COMMANDS;
    }
}
