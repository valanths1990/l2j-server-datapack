package com.l2jserver.datapack.custom.admin;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.model.PageResult;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassLevel;
import com.l2jserver.gameserver.model.base.PlayerClass;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.util.HtmlUtil;

import java.util.*;
import java.util.stream.Collectors;

public class AdminBalanceHandler implements IAdminCommandHandler {
    private final String[] COMMANDS = {"admin_balance;homepage"};

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        String resultHtml = "";
        if (command.split(" ").length == 1) {
            resultHtml = getHomepage(activeChar);
        }


        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }

    private String getHomepage(L2PcInstance activeChar) {
        String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/admin/balance/homepage.html");
        List<PlayerClass> fourthClasses = Arrays.stream(Race.values()).flatMap(r -> PlayerClass.getSet(r, ClassLevel.Fourth).stream()).collect(Collectors.toList());
        int rowCount = Math.round(fourthClasses.size() / 6.0f);

        PageResult pr = HtmlUtil.createTableWithPages(fourthClasses, fourthClasses.size(), 0, fourthClasses.size()
                , rowCount, 6, p -> "",
                b -> "<td width=\"100\" align=\"center\"><button value=\"%playerClass%\" action=\"bypass admin_balance;homepage open %playerClass%\" width=100 height=30 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>"
                        .replaceAll("%playerClass%", b.name()));
        return html = html.replaceFirst("%list%", pr.getBodyTemplate().toString());
    }

    private String getBalanceStatsForClass(PlayerClass playerClass, L2PcInstance activeChar) {
        String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/admin/balance/stats.html");

        List<String> allProps = new ArrayList<>(Configuration.balance().propertyNames());
        int rowCount = Math.round(allProps.size() / 3.0f);
        PageResult pr = HtmlUtil.createTableWithPages(allProps, allProps.size(), 0, allProps.size(), rowCount, 3, p -> "", b -> {
            return "<td width=\"200\" align=\"center\"><button value=\"%stat%\" action=\"bypass admin_balance;" + playerClass.name() + " open %stat%\" width=\"200\" height=\"30\" back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>";
        });
        html = html.replaceFirst("%list%", pr.getBodyTemplate().toString());
        return html;
    }

    @Override
    public String[] getAdminCommandList() {
        return COMMANDS;
    }
}
