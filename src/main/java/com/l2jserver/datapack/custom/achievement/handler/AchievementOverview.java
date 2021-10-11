package com.l2jserver.datapack.custom.achievement.handler;

import com.l2jserver.gameserver.custom.images.ImageManager;
import com.l2jserver.datapack.custom.achievement.AchievementManager;
import com.l2jserver.datapack.custom.achievement.stateImpl.Achievement;
import com.l2jserver.datapack.custom.achievement.stateImpl.IState;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.PageResult;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.util.HtmlUtil;
import com.l2jserver.gameserver.util.Util;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AchievementOverview implements IBypassHandler {
    private static final String[] COMMANDS = {"achievement;homepage"};
    private static final String progressTable = "<tr><td align=\"left\" height=\"30\"><table cellspacing=0 cellpadding=2 background=\"Crest.crest_%serverId%_%imageId%\" width=\"256\" height=\"16\"> <tr> <td width=\"256\" height=\"14\"> <img src=\"Crest.crest_%serverId%_%imageId%\" width=\"%progress%\" height=\"8\"> </td> </tr> </table></td>";
    private static final String progressRow = "<td width=\"150\" align=\"center\" fixwidth=\"150\"><font name=\"hs12\" color=\"fca503\">%current%/%end%</font></td></tr>";
    private static final String itemTable = "<td width=\"32\" height=\"32\"> <img src=\"%itemIcon%\" width=\"32\" height=\"32\"> </td> <td width=\"64\"> <font name=\"hs9\" color=\"fca503\">%count%</font> </td>";
    private static final String repeating = "<img src=\"Crest.crest_%serverId%_%imageId%\" width=\"64\" height=\"16\">";
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "kk");
        suffixes.put(1_000_000_000L, "kkk");
        suffixes.put(1_000_000_000_000L, "kkkk");
        suffixes.put(1_000_000_000_000_000L, "kkkkk");
        suffixes.put(1_000_000_000_000_000_000L, "kkkkkk");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10F);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        AchievementManager.getInstance().update();
        String[] split = command.split(" ");
        String html;
        int page = 0;
        if (split.length == 3) {
            page = Integer.parseInt(split[2]);
        }
        html = getHomepage(activeChar, page);
        CommunityBoardHandler.separateAndSend(html, activeChar);
        return false;
    }

    private String getHomepage(L2PcInstance pc, int page) {
        String html = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/achievement/html/homepage.html");
        Set<Achievement> playersAchievements = AchievementManager.getInstance().getAllPlayersAchievements(pc);
        List<String> allAchievements = playersAchievements.stream().map(a -> createAchievementHtml(pc, a)).collect(Collectors.toList());

        PageResult pr = HtmlUtil.createTableWithPages
                (allAchievements, allAchievements.size(), page, 3, 3, 1, 0, 0, 0, 0, 0, "",
                        i -> "<td align=\"center\"><a action=\"bypass achievement;homepage page " + i + "\"><font name=\"hs22\" color=\"fca503\">" + i + " </font></a></td>",
                        f -> f);

        html = html.replaceFirst("%achievements%", pr.getBodyTemplate().toString())
                .replaceFirst("%pages%", pr.getPagerTemplate().toString());

        return html;
    }

    private String createAchievementHtml(L2PcInstance pc, Achievement a) {
        String achievementTemplate = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/achievement/html/achievementtemplate.html");

        String progress = a.getStates().stream().map(this::createProgress).collect(Collectors.joining());

        String itemHtml = a.getRewardOperations().stream().map(i -> itemTable
                .replaceFirst("%itemIcon%", "ItemTable.getInstance().getTemplate(i.getId()).getIcon()")
                .replaceFirst("%count%", "format(i.getCount())")).collect(Collectors.joining());

        achievementTemplate = achievementTemplate
                .replaceFirst("%title%", a.getTitle())
                .replaceFirst("%description%", a.getDesc())
                .replaceFirst("%imageId%", String.valueOf(ImageManager.getInstance().getImageId("trophy3")))
                .replaceFirst("%progress%", progress)
                .replaceFirst("%isRepeating%", a.isRepeating() ?
                        repeating.replaceFirst("%imageId%", String.valueOf(ImageManager.getInstance().getImageId("repeating")))
                        : "")
                .replaceFirst("%items%", itemHtml);


        return achievementTemplate;
    }

    private static String kFormatter(BigDecimal num) {
        return String.valueOf(Math.abs(num.longValue()) > 999 ? Math.signum(num.longValue()) * ((Math.abs(num.longValue()) / 1000F)) + 'k' : Math.signum(num.longValue()) * Math.abs(num.longValue()));
    }

    private String createProgress(IState<? extends Number> s) {
        int currentProgress = Util.map(s.getCurrent().intValue(), s.getStart().intValue(), s.getEnd().intValue(), 0, 256);
        String progressBarColor = s.isDone() ? "progress-bar-gold" : "progress-bar-blue";
        String table = progressTable
                .replaceFirst("%imageId%", String.valueOf(ImageManager.getInstance().getImageId("progress-bar-background")))
                .replaceFirst("%imageId%", String.valueOf(ImageManager.getInstance().getImageId(progressBarColor)))
                .replaceFirst("%progress%", String.valueOf(currentProgress));
        table += progressRow.replaceFirst("%current%", String.valueOf(s.getCurrent().intValue())).replaceFirst("%end%", String.valueOf(s.getEnd().intValue()));
        return table;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
