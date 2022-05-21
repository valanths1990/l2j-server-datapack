package com.l2jserver.datapack.eventengine.handler;

import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.managers.CacheManager;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.Util;
import okhttp3.Cache;

import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;

public class EventEngineHandler implements IBypassHandler {
    private final String[] COMMANDS = {"eventengine;homepage"};
    private final String button = "<button value=\"%action%\" action=\"bypass eventengine;homepage %action%\" width=180 height=40 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
    private final String registeredPlayersLabel = "<font name=\"hs16\" color=\"fca503\">Registered Players: </font><font name=\"hs16\" color=\"ffffff\">%registeredPlayers%</font>";

    public EventEngineHandler() {

    }

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {

        String[] action = command.split(" ");
        if (action.length == 1) {
            openHomepage(activeChar);
            return true;
        }
        Player player = CacheManager.getInstance().getPlayer(activeChar.getObjectId());
        if (action[1].equals("register")) {
            if (EventEngineManager.getInstance().isOpenRegister() && !EventEngineManager.getInstance().isRegistered(player)) {
                EventEngineManager.getInstance().registerPlayer(player);
            }
        }
        if (action[1].equals("unregister")) {
            EventEngineManager.getInstance().unRegisterPlayer(player);
        }
        openHomepage(activeChar);
        return false;
    }

    private void openHomepage(L2PcInstance activeChar) {
        String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/eventengine/html/homepage.html");
        html = html.replace("%eventName%", EventEngineManager.getInstance().getNextEvent().getEventName());
        html = html.replace("%eventState%", Util.capitalizeFirst(EventEngineManager.getInstance().getEventEngineState().name().toLowerCase()));
        Duration duration = Duration.ofSeconds(EventEngineManager.getInstance().getTime());
        html = html.replace("%time%", duration.toMinutesPart() + ":" + duration.toSecondsPart());
        Player player = CacheManager.getInstance().getPlayer(activeChar.getObjectId());
        String htmlButton = button.replaceAll("%action%", EventEngineManager.getInstance().isRegistered(player) ? "unregister" : "register");

        String htmlRegisteredPlayersLabel = registeredPlayersLabel.replace("%registeredPlayers%", EventEngineManager.getInstance().isOpenRegister() ? String.valueOf(EventEngineManager.getInstance().getAllRegisteredPlayers().size()) : "");

        if (EventEngineManager.getInstance().isOpenRegister()) {

            html = html.replaceFirst("%registeredPlayers%", htmlRegisteredPlayersLabel).replaceFirst("%button%", htmlButton);
        } else {
            html = html.replaceFirst("%registeredPlayers%", "").replaceFirst("%button%", "");
        }

        CommunityBoardHandler.separateAndSend(html, activeChar);
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
