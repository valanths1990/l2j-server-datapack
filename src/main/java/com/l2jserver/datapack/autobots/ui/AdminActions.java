package com.l2jserver.datapack.autobots.ui;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.AutobotsManager;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.ui.states.BotDetailsViewState;
import com.l2jserver.datapack.autobots.ui.states.IndexViewState;
import com.l2jserver.datapack.autobots.ui.states.ViewState;
import com.l2jserver.datapack.autobots.ui.states.ViewStates;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsTab;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AdminActions {

    public void createAndSpawnRandomBots(List<String> splitCommand, L2PcInstance activeChar) {
        int count;
        if (splitCommand.size() > 1) {
            count = Integer.parseInt(splitCommand.get(2));
        } else count = 1;
        createAndSpawnRandomAutobots(count, activeChar);
    }

    public void toggleSitting(Autobot bot) {
        if (bot.isSitting()) {
            bot.standUp();
        } else {
            bot.sitDown();
        }
    }

    public void selectBotDetailsTab(L2PcInstance activeChar, BotDetailsTab tab) {
        ViewState state = ViewStates.getInstance().getActiveState(activeChar);
        if (state instanceof BotDetailsViewState) {
            ((BotDetailsViewState) state).setActiveTab(tab);
        }
    }

    public void removeSkillPreference(List<String> splitCommand, BotDetailsViewState state) {
        int skillId = Integer.parseInt(splitCommand.get(4));
        state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.removeIf(s -> s.getSkillId() == skillId);
        AutobotsDao.getInstance().saveSkillPreferences(state.getActiveBot());
    }

    public void removeSelectedBotsFromClan(L2PcInstance activeChar) {
        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().forEach((key, value) -> {
            Autobot bot = AutobotsManager.getInstance().getBotFromOnlineOrDb(key);
            if (bot != null) {

                if (bot.getClan() != null) {

                    if (bot.isClanLeader()) {
                        if (bot.getClan().getAllyId() == 0) {
                            ClanTable.getInstance().destroyClan(bot.getClanId());
                        } else {
                            activeChar.sendMessage("You cannot delete a clan in an ally. Delete the ally first.");
                        }
                    } else {
                        AutobotsDao.getInstance().removeClanMember(bot, bot.getClan());
                        if (!bot.isOnline()) {
                            AutobotsDao.getInstance().saveAutobot(bot);
                        }
                    }
                }
            }
        });
    }

    public void saveSkillUsageCondition(List<String> splitCommand, BotDetailsViewState state, StatusCondition statusCondition, ComparisonCondition comparisonCondition, ConditionValueType conditionValueType, TargetCondition targetCondition, int value) {
        String skillName = String.join(" ", splitCommand.subList(9, splitCommand.size()));
        Skill skill = state.getActiveBot().getCombatBehavior().getConditionalSkills()
                .stream()
                .filter(s -> SkillData.getInstance().getSkill(s, 1) != null && Objects.requireNonNull(SkillData.getInstance().getSkill(s, 1)).getName().equals(skillName))
                .map(s -> SkillData.getInstance().getSkill(s, 1)).findFirst().orElse(null);
        if (skill == null) return;

        SkillUsageCondition skillUsageCondition = new SkillUsageCondition(skill.getId(), statusCondition, comparisonCondition, conditionValueType, targetCondition, value);

        if (splitCommand.get(3).equals("s")) {
            state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.removeIf(s -> s.getSkillId() == skill.getId());
        }

        state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.add(skillUsageCondition);
        AutobotsDao.getInstance().saveSkillPreferences(state.getActiveBot());
        state.setSkillUnderEdit(0);
    }

    public IndexViewState.Pagination setCurrentPagination(List<String> splitCommand, int pageNumberIndex, int pageSizeIndex) {
        int pageNumber = Integer.parseInt(splitCommand.get(pageNumberIndex));
        int pageSize = Integer.parseInt(splitCommand.get(pageSizeIndex));
        return new IndexViewState.Pagination(pageNumber, pageSize);
    }

    public void despawnBotsInRadius(L2PcInstance activeChar, String radius) {
        List<Autobot> botsInRadius = activeChar.getKnownList().getKnownPlayersInRadius(Integer.parseInt(radius)).stream().filter(p -> p instanceof Autobot).map(p -> (Autobot) p).collect(Collectors.toList());
        ThreadPoolManager.getInstance().scheduleGeneral(() -> botsInRadius.forEach(this::despawnAutobot), 0);
    }

    public void createAndSpawnRandomAutobots(int count, L2PcInstance activeChar) {
        IntStream.range(0, count).forEach(i -> ThreadPoolManager.getInstance().scheduleGeneral(() -> AutobotsManager.getInstance().createAndSpawnAutobot(activeChar.getX() + Rnd.get(-150, 150), activeChar.getY() + Rnd.get(-150, 150), activeChar.getZ(), true), 0));
    }

    public void despawnAutobot(Autobot autobot) {
        if (autobot == null) return;
        autobot.despawn();
    }

    public static AdminActions getInstance() {
        return AdminActions.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AdminActions INSTANCE = new AdminActions();
    }
}
