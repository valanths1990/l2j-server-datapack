package com.l2jserver.datapack.custom.clan;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.instancemanager.FortManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2VillageMasterInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.Fort;
import com.l2jserver.gameserver.network.serverpackets.ExShowFortressSiegeInfo;
import com.l2jserver.gameserver.network.serverpackets.SiegeInfo;

public class Clan implements IBypassHandler {
    private final String[] COMMANDS = {
            "clan;homepage"
    };
    private final L2VillageMasterInstance highPriet;

    public Clan() {
        highPriet = new L2VillageMasterInstance(NpcData.getInstance().getTemplate(30857));
    }

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String[] split = command.split(" ", 3);
        String html = "";
        if (split.length == 1) {
            html = getHomepage(activeChar);
        } else if (split.length == 2) {
            html = openSubMenu(activeChar, split[1]);
        } else if (split.length == 3) {
            if (split[1].equalsIgnoreCase("showcastle")) {

                Castle castle = CastleManager.getInstance().getCastle(split[2]);
                if (castle != null) {
                    activeChar.sendPacket(new SiegeInfo(castle));
                }

            } else if (split[1].equalsIgnoreCase("showfortress")) {
                Fort fortress = FortManager.getInstance().getFort(split[2]);
                if (fortress != null) {
                    activeChar.sendPacket(new ExShowFortressSiegeInfo(fortress));
                }
            } else {
                highPriet.onBypassFeedback(activeChar, split[2]);
            }
            html = getHomepage(activeChar);
        }
        CommunityBoardHandler.separateAndSend(html, activeChar);
        return false;
    }

    private String openSubMenu(L2PcInstance pc, String submenu) {
        return HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/clan/" + submenu + ".html");
    }

    private String getHomepage(L2PcInstance pc) {
        String html = "";
        if (pc.getClan() == null) {
            return html;
        }
        html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/clan/homepage.html");
        String increaseLevelButton = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/clan/clanmember.html");
        html = html.replace("%clanmember%", increaseLevelButton);

        if (pc.getClan().getLeaderId() == pc.getObjectId()) {
            String leaderButtons = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/clan/clanleaderbuttons.html");
            html = html.replace("%clanleaderbuttons%", leaderButtons);
        }

        html = html.
                replace("%clanName%", pc.getClan().getName())
                .replace("%online%", String.valueOf(pc.getClan().getOnlineMembers(pc.getObjectId()).size()))
                .replace("%leader%", pc.getClan().getLeaderName())
                .replace("%level%", String.valueOf(pc.getClan().getLevel()));
        Castle castle = CastleManager.getInstance().getCastleById(pc.getClan().getCastleId());
        Fort fort = FortManager.getInstance().getFortById(pc.getClan().getFortId());

        if (fort != null) {
            html = html.replace("%base%", "Fortress").replace("%baseName%", fort.getName());
        } else if (castle != null) {
            html = html.replace("%base%", "Castle").replace("%baseName%", castle.getName());
        } else {
            html = html.replace("%base%", "Base").replace("%baseName%", "None");
        }
        String ally = pc.getClan().getAllyName() == null ? "" : pc.getClan().getAllyName();
        html = html.replace("%allyName%", ally);

        String castleFortress = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/clan/castlefortressbuttons.html");
        html = html.replace("%castleFortress%", castleFortress);

        return html;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
