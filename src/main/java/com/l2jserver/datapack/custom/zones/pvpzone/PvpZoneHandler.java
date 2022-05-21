package com.l2jserver.datapack.custom.zones.pvpzone;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.L2Party;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.util.Util;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PvpZoneHandler implements IBypassHandler {
    private final String[] COMMANDS = {"pvpzone;homepage join", "pvpzone;homepage leave", "pvpzone;homepage regroup", "pvpzone;homepage", "pvpzone;homepage npc"};
    private final Location regroupRoom = new Location(-86966, -81809, -8357); // kratei cube resting zone
    private final List<L2PcInstance> pendingConfirms = new CopyOnWriteArrayList<>();
    private final Map<L2Party, Location> partyLocations = new ConcurrentHashMap<>();

    public PvpZoneHandler() {
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_DLG_ANSWER, this::onDialogAnswer, this));
    }

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
        if (command.split(" ").length == 1) {
            openBoard(player);
            return true;
        }

        String[] splitCommand = command.split(" ");
        switch (splitCommand[1]) {
            case "join" -> {
                Location loc = PvpZoneManager.getInstance().getRandomLocation();
                player.teleToLocation(loc, true);
                if (player.isInParty()) {
                    partyLocations.put(player.getParty(), loc);
                    List<L2PcInstance> ptMembers = player.getParty().getMembers().stream()
                            .filter(p -> !PvpZoneManager.getInstance().getCurrentZone().isInsideZone(p) && !p.isInCombat() && p != player)
                            .collect(Collectors.toList());

                    ptMembers.forEach(p -> {
                        ConfirmDlg dlg = new ConfirmDlg("Your party joined PvP zone. Do you want to follow your party ?");
                        dlg.addTime(15000);
                        dlg.addRequesterId(p.getObjectId());
                        pendingConfirms.add(p);
                        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                            pendingConfirms.remove(p);
                            partyLocations.remove(player.getParty());
                        }, 16, TimeUnit.SECONDS);
                        p.sendPacket(dlg);
                    });
                }
            }
            case "leave", "regroup" -> player.teleToLocation(regroupRoom, true);
            case "npc" -> Util.sendHtml(player, HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/pvpzone/html/npc.html"));
        }

        return true;
    }

    private void onDialogAnswer(IBaseEvent event) {
        OnPlayerDlgAnswer dlgAnswer = (OnPlayerDlgAnswer) event;

        L2PcInstance p = dlgAnswer.getActiveChar();
        if (!pendingConfirms.contains(p) || dlgAnswer.getAnswer() <= 0) {
            return;
        }
        if (!p.isInParty() || !partyLocations.containsKey(p.getParty())) {
            return;
        }
        p.teleToLocation(partyLocations.get(p.getParty()), true);
    }


    private void openBoard(L2PcInstance player) {
        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/pvpzone/html/homepage.html");

        Duration d = Duration.ofSeconds(PvpZoneManager.getInstance().getRemainingTime());

        html = html.replaceFirst("%mode%", PvpZoneManager.getInstance().getZoneMode().getZoneModeName())
                .replaceFirst("%zoneName%", PvpZoneManager.getInstance().getCurrentZone().getName())
                .replaceFirst("%time%", String.valueOf(d.toMinutesPart()))
                .replaceFirst("%peopleCount%", String.valueOf(PvpZoneManager.getInstance().getCurrentZone().getPlayersInside().size()));

        CommunityBoardHandler.separateAndSend(html, player);
    }


    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
