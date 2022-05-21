package com.l2jserver.datapack.custom.settings;

import com.l2jserver.datapack.votesystem.global.Global;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.custom.skin.SkinManager;
import com.l2jserver.gameserver.custom.skin.Visibility;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerVisibilityChange;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;

import java.util.Arrays;

public class Settings implements IBypassHandler {
    private String[] COMMANDS = {
            "settings;homepage"
    };

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String resultHtml = "";
        String[] splitted = command.split(" ");
        if (splitted.length == 1) {
            resultHtml = getHomepage(activeChar);
        } else if (splitted.length == 3) {

            if (splitted[1].equals("visibility")) {
                Visibility v = Visibility.valueOf(splitted[2]);

                changeVisibility(activeChar, v);
                resultHtml = getHomepage(activeChar);
            }

        }


        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }

    private void changeVisibility(L2PcInstance pc, Visibility newVisibility) {
        TerminateReturn terminate = EventDispatcher.getInstance().notifyEvent(new OnPlayerVisibilityChange(pc, newVisibility), pc, TerminateReturn.class);
        if (terminate != null && terminate.abort()) {
            pc.sendMessage("You are not allowed to change the Skin Visibility right now.");
            return;
        }
        SkinManager.getInstance().setVisibility(pc, newVisibility);
        pc.broadcastUserInfo();
    }

    private String getHomepage(L2PcInstance pc) {
        String resultHtml = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/settings/homepage.html");
        Visibility currentV = SkinManager.getInstance().isEnabled(pc);
        resultHtml = resultHtml.replace("%current%", currentV.name());
        for (Visibility v : Visibility.values()) {
            resultHtml = resultHtml.replace("%" + v.name() + "%", v.name());
        }
        return resultHtml;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
