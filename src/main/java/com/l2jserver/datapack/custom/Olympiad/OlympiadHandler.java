package com.l2jserver.datapack.custom.Olympiad;

import com.l2jserver.datapack.ai.npc.MonumentOfHeroes.MonumentOfHeroes;
import com.l2jserver.datapack.eventengine.ai.NpcManager;
import com.l2jserver.datapack.handlers.bypasshandlers.OlympiadManagerLink;
import com.l2jserver.datapack.handlers.itemhandlers.Bypass;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2OlympiadManagerInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.olympiad.Olympiad;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.network.serverpackets.ExHeroList;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillLaunched;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.scripting.ScriptManager;
import org.w3c.dom.html.HTMLTableCaptionElement;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

public class OlympiadHandler implements IBypassHandler {
    private static final String[] COMMANDS = {"olympiad;homepage"};
    private MonumentOfHeroes monumentOfHeroes;

    public OlympiadHandler() {
        try {
            monumentOfHeroes = MonumentOfHeroes.class.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
        String[] split = command.split(" ");
        String html = "";
        if (split.length == 1) {
            html = showHomepage(activeChar);
        } else if (split.length == 2) {
            if (split[1].equals("heroweapons")) {
                html = showHeroWeapons(activeChar);
            } else if (split[1].equalsIgnoreCase("noblesse") && !activeChar.isNoble()) {
                html = showNobless(activeChar);
            }

        } else if (split.length == 3) {
            if (split[1].equals("get")) {
                assert monumentOfHeroes != null;
                if (split[2].matches("[0-9]+")) {
                    if (monumentOfHeroes.hasAtLeastOneQuestItem(activeChar, MonumentOfHeroes.WEAPONS)) {
                        activeChar.sendMessage("You need to be Hero or have no Hero Weapons inside your Inventory.");
                    } else {
                        monumentOfHeroes.onAdvEvent(split[2], null, activeChar);
                    }
                } else if (split[2].equalsIgnoreCase("HeroCirclet")) {
                    monumentOfHeroes.onAdvEvent(split[2], null, activeChar);
                } else if (split[2].equalsIgnoreCase("noblesse") && !activeChar.isNoble()) {
                    activeChar.setNoble(true);
                    activeChar.reduceAdena("Noblesse", 500000000, null, true);
                    activeChar.broadcastPacket(new MagicSkillUse(activeChar, 5103, 1, 0, 0));
                    activeChar.broadcastPacket(new MagicSkillLaunched(activeChar, 5103, 1));
                } else if (split[2].equalsIgnoreCase("herolist")) {
                    activeChar.sendPacket(new ExHeroList());
                }
            }

        }

        CommunityBoardHandler.separateAndSend(html.isEmpty() ? showHomepage(activeChar) : html, activeChar);
        return false;
    }

    private String showHeroWeapons(L2PcInstance pc) {
        return HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/olympiad/heroweapons.html");
    }

    private String showHomepage(L2PcInstance pc) {
        String resultHtml = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/olympiad/homepage.html");
        int points = Olympiad.getInstance().getNoblePoints(pc.getObjectId());
        int win = Olympiad.getInstance().getCompetitionWon(pc.getObjectId());
        int lose = Olympiad.getInstance().getCompetitionLost(pc.getObjectId());
        int games = Olympiad.getInstance().getCompetitionDone(pc.getObjectId());
        List<String> leaders = Olympiad.getInstance().getClassLeaderBoard(pc.getClassId().getId());
        String name = leaders.isEmpty() ? "" : leaders.get(0);

        resultHtml = resultHtml.
                replace("%points%", String.valueOf(points))
                .replace("%winLose%", win + "/" + lose)
                .replace("%playedGames%", String.valueOf(games))
                .replace("%periodRanking%", name);

        return resultHtml;
    }

    private String showNobless(L2PcInstance pc) {

        return HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/olympiad/noblesse.html");
    }


    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }
}
