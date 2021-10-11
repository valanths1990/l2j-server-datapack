package com.l2jserver.datapack.custom.raidboss;

import com.l2jserver.gameserver.custom.images.ImageManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.util.HtmlUtil;
import com.l2jserver.gameserver.util.Util;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static com.l2jserver.gameserver.config.Configuration.hexId;

public class RaidbossHandler implements IBypassHandler {

    private static final String[] COMMANDS = {"raidboss;homepage"};
    private final String fastTeleportButton = "<tr><td width=\"300\" align=\"center\"><button value=\"%name%\" action=\"bypass raidboss;homepage teleport %name%\" width=220 height=40 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"><br></td></tr>";
    private final String itemIcon = " <td width=\"32\" height=\"32\"><img src=\"%itemIcon%\" width=\"32\" height=\"32\"></td>";
    private final String time = "<tr><td width=\"300\" height=\"300\" align=\"center\"><font name=\"hs22\" color=\"fca503\">%days% Days and %hours% Hours</font></td></tr>";
    private final Map<L2PcInstance, Long> teleportTimes = new ConcurrentHashMap<>();

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String resultHtml = "";
        String[] splitted = command.split(" ",3);
        if (splitted.length <= 1) {
            resultHtml = openHomepage(activeChar);
        }
        if (splitted.length >= 2) {
            switch (splitted[1]) {
                case "show" -> showEnterRaidBoss(activeChar);
                case "enter" -> CustomGrandBossManager.getInstance().enterGrandBoss(activeChar);
                case "teleport" -> teleportPlayer(activeChar, splitted[2]);
            }
            return true;
        }
        CommunityBoardHandler.separateAndSend(resultHtml, activeChar);
        return false;
    }

    private void showEnterRaidBoss(L2PcInstance pc) {
        String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "/data/custom/raidboss/enter.html");
        Util.sendHtml(pc, html);
    }

    private String openHomepage(L2PcInstance pc) {
        String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/raidboss/homepage.html");
        Status status = CustomGrandBossManager.getInstance().getStatus();
        String bossName = CustomGrandBossManager.getInstance().getBossName();
        String state = status == Status.UPCOMING ? "Upcoming in" : "Fast Teleport";
        String imgId = String.valueOf(ImageManager.getInstance().getImageId(bossName.toLowerCase()));
        String conqueror = CustomGrandBossManager.getInstance().getConqueror();
        Period p = Period.between(LocalDate.now(), CustomGrandBossManager.getInstance().getSpawningTime().toLocalDate());
        Duration d = Duration.ofSeconds(CustomGrandBossManager.getInstance().getSpawningTime().atZone(ZoneId.systemDefault()).toEpochSecond() - (System.currentTimeMillis() / 1000));
        String leftTime = time.replace("%days%", String.valueOf(p.getDays())).replace("%hours%", String.valueOf(d.toHoursPart()));
        List<L2Item> drops = CustomGrandBossManager.getInstance().getDrops();
        String dropsItem = HtmlUtil.createPage(drops,0,drops.size(),i->"",drop->itemIcon.replace("%itemIcon%",drop.getIcon())).getBodyTemplate().toString();

        if (status == Status.UPCOMING) {
            html = html.replaceFirst("%time%", leftTime);
        } else {
            List<Location> tps = CustomGrandBossManager.getInstance().getSpawns();
            if(tps.size()>0){
            String teleportTable = HtmlUtil.createPage(tps, 0, tps.size(), i -> "", l -> fastTeleportButton.replace("%name%", l.getName())).getBodyTemplate().toString();
            html = html.replaceFirst("%teleportTable%", teleportTable);
            }
        }

        html = html.replaceFirst("%bossName%", bossName)
                .replaceFirst("%state%", state)
                .replaceFirst("%imageId%", imgId)
                .replaceFirst("%conqueror%", conqueror)
                .replaceFirst("%status%",status.name())
                .replaceFirst("%itemTable%",dropsItem);
        return html;
    }

    private void teleportPlayer(L2PcInstance pc, String spawnName) {
//        if (!canUseTeleport(pc)) {
//            return;
//        }
        teleportTimes.put(pc, System.currentTimeMillis());
        Optional<Location> oLocation = CustomGrandBossManager.getInstance().getSpawns().stream().filter(l -> l.getName() != null && l.getName().equals(spawnName)).findFirst();
        oLocation.ifPresent(pc::teleToLocation);

    }

    private boolean canUseTeleport(L2PcInstance pc) {
        return !teleportTimes.containsKey(pc) || teleportTimes.get(pc) >= System.currentTimeMillis() + Configuration.customs().getResetTeleportToRaidBossTime();
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
