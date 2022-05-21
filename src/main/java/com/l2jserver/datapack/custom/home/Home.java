package com.l2jserver.datapack.custom.home;

import com.l2jserver.gameserver.custom.Activity.ActivityManager;
import com.l2jserver.gameserver.custom.Activity.IActivity;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.handler.VoicedCommandHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Arrays;
import java.util.Optional;

public class Home implements IBypassHandler {
    private String[] COMMANDS = {"home;homepage"};
    //    private final Map<L2PcInstance, Long> lastVoteTime = new ConcurrentHashMap<>();

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String[] splitted = command.split(" ");
        String resultHmtl = "";
        if (splitted.length == 1) {
            resultHmtl = openHomePage(activeChar);
        } else if (splitted.length == 3) {
            if (splitted[2].equals("features")) {
                resultHmtl = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/home/features.html");
            }
            if (splitted[2].equals("vote")) {
                resultHmtl = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/home/vote.html");
            }
            if (splitted[2].equals("getvote")) {

                IVoicedCommandHandler voteHandler = VoicedCommandHandler.getInstance().getHandler("itopz");
                Arrays.stream(voteHandler.getVoicedCommandList()).forEach(s -> voteHandler.useVoicedCommand(s, activeChar, s));
            }
        }
        CommunityBoardHandler.separateAndSend(resultHmtl, activeChar);
        return true;
    }

    private String openHomePage(L2PcInstance pc) {
        String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/home/home.html");
        Optional<IActivity> currentAction = ActivityManager.getInstance().getCurrentAction();
        String actionTableHtml = currentAction.map(a -> {
            String actionTable = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/home/actiontable.html");
            actionTable = actionTable.replaceFirst("%title%", a.getName())
                    .replaceFirst("%image%", a.getImage())
                    .replaceFirst("%bypass%", a.getBypass());
            return actionTable;
        }).orElse("");
        html = html.replaceFirst("%action%", actionTableHtml);
        return html;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
