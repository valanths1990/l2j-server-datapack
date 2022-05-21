package com.l2jserver.datapack.autobots.admincommands;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.AutobotsManager;
import com.l2jserver.datapack.autobots.autofarm.AutofarmManager;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.behaviors.sequences.TeleportToLocationSequence;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.models.AutobotInfo;
import com.l2jserver.datapack.autobots.models.BotChat;
import com.l2jserver.datapack.autobots.models.BotDebugAction;
import com.l2jserver.datapack.autobots.models.ChatType;
import com.l2jserver.datapack.autobots.ui.*;
import com.l2jserver.datapack.autobots.ui.AdminActions;
import com.l2jserver.datapack.autobots.ui.states.*;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsChatTab;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsSocialPage;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsTab;
import com.l2jserver.datapack.autobots.ui.tabs.IndexTab;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.BotZoneService;
import com.l2jserver.datapack.autobots.utils.CrestService;
import com.l2jserver.datapack.autobots.utils.packets.GMViewBuffs;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.data.xml.impl.RecipeData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.handler.ChatHandler;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.model.*;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.*;

import static com.l2jserver.datapack.autobots.utils.Util.getOrNull;
import static com.l2jserver.datapack.autobots.utils.Util.getOrElse;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminAutobots implements IAdminCommandHandler {
    private static final String[] COMMANDS = {"admin_a"};

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {

        String[] splitCommand = command.split(" ");
        if (splitCommand.length == 1) {
//            String html = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(),"data/autobots/html/views/homepage.html");
//            CommunityBoardHandler.separateAndSend(html,activeChar);
            AutobotsUi.getInstance().loadCreateBot(activeChar);
            return false;
        }
        switch (splitCommand[1]) {
            case "random" -> AdminActions.getInstance().createAndSpawnRandomBots(Arrays.asList(splitCommand), activeChar);
            case "load" -> {
                String name = splitCommand[2];
                boolean onMe;
                if (splitCommand.length > 3) onMe = Boolean.parseBoolean(splitCommand[3]);
                else onMe = false;

                Autobot autobot = AutobotsDao.getInstance().loadByName(name);
                if (autobot == null) return false;
                if (onMe) {
                    autobot.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getClientZ());
                }
                ThreadPoolManager.getInstance().scheduleGeneral(() -> AutobotsManager.getInstance().spawnAutobot(autobot), 0);
            }
            case "count" -> activeChar.sendMessage("Current bot count: " + AutobotsManager.getInstance().getActiveBots().size());
            case "delete" -> {
                String radius;
                if (splitCommand.length == 2) return false;
                radius = splitCommand[2];
                if (radius != null) {
                    if (!(activeChar.getTarget() instanceof Autobot)) return false;
                    AdminActions.getInstance().despawnAutobot((Autobot) activeChar.getTarget());
                    return true;
                }

                AdminActions.getInstance().despawnBotsInRadius(activeChar, radius);
            }
            case "sm" -> {
//                String mobName = splitCommand[2];
//                int count = Integer.parseInt(getOrElse(splitCommand,3).orElse("1"));
//
//                IntStream.range(1,count).forEach(i->{
//                    ThreadPoolManager.getInstance().scheduleGeneral(()->AutobotHelpers.spawn(activeChar,mobName),0);
//                });
//                CoScopes.generalScope.launch {
//                    for (i in; ; 1..count ;){
//                        CoScopes.massSpawnerScope.launch {
//                            spawn(activeChar, mobName);
//                        }
//                    }
//                }
            }
            case "debug" -> {
                switch (splitCommand[2]) {
                    case "coffee" -> activeChar.sendMessage("is this what you wanted?");
                    case "trans" -> {
                    }
                    case "tp" -> {
                        if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof Autobot)) return true;

                        int x = Integer.parseInt(splitCommand[3]);
                        int y = Integer.parseInt(splitCommand[4]);
                        int z = Integer.parseInt(splitCommand[5]);

                        ThreadPoolManager.getInstance().scheduleGeneral(() -> new TeleportToLocationSequence((Autobot) activeChar.getTarget(), new Location(x, y, z)), 0);

                    }
                    case "trade" -> {
                        if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof Autobot))
                            return true;
                    }
                    case "bot" -> {
                        switch (splitCommand[3]) {
                            case "save" -> AutofarmManager.getInstance().savePreferences(activeChar);
//                            case "on" -> AutofarmManager.getInstance().startFarm(activeChar);
//                            case "off" -> AutofarmManager.getInstance().stopFarm(activeChar);
                            case "buffs" -> {
                                if (AutofarmManager.getInstance().getCombatBehaviors().containsKey(activeChar.getObjectId())) {
                                    AutofarmManager.getInstance().getCombatBehaviors().get(activeChar.getObjectId()).applyBuffs();
                                }
                            }
                        }
                    }
                    case "z" -> {
                        switch (splitCommand[3]) {
                            case "on" -> BotZoneService.player = activeChar;
                            case "off" -> BotZoneService.player = null;
                            case "clear" -> {
                                BotZoneService.graph.points.clear();
                                ExServerPrimitive packet = new ExServerPrimitive(activeChar.getName() + "_", activeChar.getX(), activeChar.getY(), -65535);
                                packet.addPoint(Color.WHITE, 0, 0, 0);
                                activeChar.sendPacket(packet);
                            }
                        }
                    }
                }
            }
            case "f" -> {
                if (activeChar.getTarget() != null && activeChar.getTarget() instanceof Autobot)
                    ((Autobot) activeChar.getTarget()).startPvPFlag();
            }
            case "ar" -> {
                if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof Autobot))
                    return false;

                boolean toggle = splitCommand[2].equals("on");

                Autobot bot = (Autobot) activeChar.getTarget();
                if (bot.getCombatBehavior() == null) return false;

                if (!toggle) {
                    if (bot.hasDevAction(activeChar, BotDebugAction.VisualizeVision)) {
                        bot.removeDevAction(activeChar, BotDebugAction.VisualizeVision);
                    }
                    AutobotHelpers.clearCircle(activeChar, activeChar.getTarget().getName() + BotDebugAction.VisualizeVision);
                    return false;
                }

                bot.addDevAction(activeChar, BotDebugAction.VisualizeVision, (player, bott) -> {

                    ExServerPrimitive packet = AutobotHelpers.createCirclePacket(bott.getName() + BotDebugAction.VisualizeVision, bott.getX(), bott.getY(), bott.getZ() + 50, bott.getCombatBehavior().getCombatPreferences().getTargetingRadius(), Color.BLUE,
                            player.getX(), player.getY());
                    player.sendPacket(packet);
                });
            }
            case "b" -> { // board related actions
                String next = getOrNull(splitCommand, 2);
                if (next == null) return false;

                switch (next) {
                    case "reset" -> {
                        String state = getOrNull(splitCommand, 3);
                        if (state == null || state.isEmpty()) {
                            ViewStates.getInstance().indexViewState(activeChar).reset();
                            return false;
                        }
                        if (state.equals("index")) {
                            ViewStates.getInstance().indexViewState(activeChar).reset();
                        }
                    }
                    case "navh" -> ViewStates.getInstance().indexViewState(activeChar);
                    case "sr" -> { //create and spawn random
                        int count = getOrElse(splitCommand, 3, Integer.class).orElse(1);
                        AdminActions.getInstance().createAndSpawnRandomAutobots(count, activeChar);
                    }
                    case "sett" -> {
                        if (splitCommand.length == 3) {
                            ViewStates.getInstance().settingsViewState(activeChar);
                        }
                    }
                    case "s" -> { //search
                        ViewStates.getInstance().indexViewState(activeChar).setNameToSearch(getOrElse(splitCommand, 5, String.class).orElse(""));
                        ViewStates.getInstance().indexViewState(activeChar).setPagination(AdminActions.getInstance().setCurrentPagination(Arrays.asList(splitCommand), 3, 4));
                    }
                    case "ord" -> { //order by
                        switch (splitCommand[3]) {
                            case "cl" -> ViewStates.getInstance().indexViewState(activeChar).setBotOrdering(IndexBotOrdering.None);
                            case "lvlasc" -> ViewStates.getInstance().indexViewState(activeChar).setBotOrdering(IndexBotOrdering.LevelAsc);
                            case "lvldesc" -> ViewStates.getInstance().indexViewState(activeChar).setBotOrdering(IndexBotOrdering.LevelDesc);
                            case "onasc" -> ViewStates.getInstance().indexViewState(activeChar).setBotOrdering(IndexBotOrdering.OnAsc);
                            case "ondesc" -> ViewStates.getInstance().indexViewState(activeChar).setBotOrdering(IndexBotOrdering.OnDesc);
                        }
                    }
                    case "l" -> { //load and spawn
                        String name = splitCommand[3];
                        Autobot autobot = AutobotsDao.getInstance().loadByName(name);
                        if (autobot == null) return false;
                        AutobotsManager.getInstance().spawnAutobot(autobot);
                    }
                    case "ch" -> { //selected bot
                        AutobotInfo autobot = AutobotsManager.getInstance().getBotInfoFromOnlineOrDb(splitCommand[3]);
                        if (autobot == null) return false;
                        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().put(autobot.getName(), autobot);
                    }
                    case "uch" -> //unselected bot
                            ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().remove(splitCommand[3]);
                    case "sk" -> {
                        ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                        if (state instanceof BotDetailsViewState) {
                            BotDetailsViewState botDetailsViewState = (BotDetailsViewState) state;
                            switch (splitCommand[3]) {
                                case "a", "s" -> {
                                    int value;
                                    try {
                                        value = Integer.parseInt(splitCommand[8]);
                                    } catch (NumberFormatException e) {
                                        activeChar.sendMessage("Value needs to be a number");
                                        return returnFail(activeChar);
                                    }
                                    TargetCondition targetCondition = TargetCondition.valueOf(splitCommand[4]);
                                    StatusCondition statusCondition = StatusCondition.valueOf(splitCommand[5]);
                                    ComparisonCondition comparisonCondition = Arrays.stream(ComparisonCondition.values())
                                            .filter(c -> c.getOperation().equals(splitCommand[6])).findFirst().orElse(null);
                                    if (comparisonCondition == null) return false;
                                    ConditionValueType conditionValueType = ConditionValueType.valueOf(splitCommand[7]);

                                    if (statusCondition == StatusCondition.Distance && conditionValueType != ConditionValueType.Amount && targetCondition != TargetCondition.Target) {
                                        activeChar.sendMessage("Distance requires value type of \"Amount\" and target type of \"Target\"");
                                        return returnFail(activeChar);
                                    }

                                    if (statusCondition == StatusCondition.Level && conditionValueType != ConditionValueType.Amount) {
                                        activeChar.sendMessage("Level requires value type of \"Amount\"");
                                        return returnFail(activeChar);
                                    }

                                    if (conditionValueType == ConditionValueType.Percentage && value > 100) {
                                        activeChar.sendMessage("Percentage needs to be between 0-100");
                                        return returnFail(activeChar);
                                    }

                                    AdminActions.getInstance().saveSkillUsageCondition(Arrays.asList(splitCommand), botDetailsViewState, statusCondition, comparisonCondition, conditionValueType, targetCondition, value);
                                }
                                case "t" -> {
                                    int skillId = Integer.parseInt(splitCommand[4]);
                                    botDetailsViewState.getActiveBot().getCombatBehavior().getSkillPreferences().togglableSkills.put(skillId, !botDetailsViewState.getActiveBot().getCombatBehavior().getSkillPreferences().togglableSkills.get(skillId));
                                    AutobotsDao.getInstance().saveSkillPreferences(botDetailsViewState.getActiveBot());
                                }
                                case "e" -> botDetailsViewState.setSkillUnderEdit(Integer.parseInt(splitCommand[4]));
                                case "c" -> botDetailsViewState.setSkillUnderEdit(0);
                                case "r" -> AdminActions.getInstance().removeSkillPreference(Arrays.asList(splitCommand), botDetailsViewState);
                            }
                        }
                    }
                    case "ctrl" -> { // control

                        if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof Autobot)) {
                            activeChar.sendMessage("You can only control bots");
                            return false;
                        }

                        switch (splitCommand[3]) {
                            case "on" -> ((Autobot) activeChar.getTarget()).controlBot(activeChar);
                            case "off" -> ((Autobot) activeChar.getTarget()).uncontrollBot();
                        }
                    }
                    case "crtbot" -> {
                        ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                        if (state instanceof CreateBotViewState) {
                            CreateBotViewState createBotViewState = (CreateBotViewState) state;
                            boolean doubleWordClass = splitCommand[5].equals("Male") || splitCommand[5].equals("Female");
                            createBotViewState.getBotDetails().setClassId(doubleWordClass ? splitCommand[3] + " " + splitCommand[4] : splitCommand[3]);

                            createBotViewState.getBotDetails().setGender(doubleWordClass ? splitCommand[5] : splitCommand[4]);
                            createBotViewState.getBotDetails().setHairStyle(doubleWordClass ? splitCommand[6] + " " + splitCommand[7] : splitCommand[5] + " " + splitCommand[6]);
                            createBotViewState.getBotDetails().setHairColor(doubleWordClass ? splitCommand[8] + " " + splitCommand[9] : splitCommand[7] + " " + splitCommand[8]);
                            createBotViewState.getBotDetails().setFace(doubleWordClass ? splitCommand[10] + " " + splitCommand[11] : splitCommand[9] + " " + splitCommand[10]);

                            Autobot bot = AutobotsManager.getInstance().createAutobot(activeChar, createBotViewState.getBotDetails().getName(),
                                    createBotViewState.getBotDetails().getLevel(), createBotViewState.getBotDetails().getClassId(), createBotViewState.getBotDetails().getAppearance(),
                                    activeChar.getX(), activeChar.getY(), activeChar.getZ(),
                                    createBotViewState.getBotDetails().getWeaponEnchant(), createBotViewState.getBotDetails().getArmorEnchant(), createBotViewState.getBotDetails().getJewelEnchant());

                            if (bot != null) {
                                ViewStates.getInstance().indexViewState(activeChar).reset();
                            }
                        }
                    }
                    case "visar" -> {
                        String name = splitCommand[3];
                        Autobot bot = AutobotsManager.getInstance().getActiveBots().get(name);
                        if (bot == null) return false;

                        if (bot.hasDevAction(activeChar, BotDebugAction.VisualizeVision)) {
                            bot.removeDevAction(activeChar, BotDebugAction.VisualizeVision);
                            AutobotHelpers.clearCircle(activeChar, bot.getName() + BotDebugAction.VisualizeVision);
                            return false;
                        }

                        bot.addDevAction(activeChar, BotDebugAction.VisualizeVision, (player, autobot) -> {
                            ExServerPrimitive packet = AutobotHelpers.createCirclePacket(autobot.getName() + BotDebugAction.VisualizeVision,
                                    autobot.getX(), autobot.getY(), autobot.getZ() + 50,
                                    autobot.getCombatBehavior().getCombatPreferences().getTargetingRadius(), Color.BLUE,
                                    player.getX(), player.getY());
                            player.sendPacket(packet);
                        });
                    }
                    case "rndbot" -> {
                        ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                        if (state instanceof CreateBotViewState) {
                            CreateBotViewState createBotViewState = (CreateBotViewState) state;
                            createBotViewState.getBotDetails().randomize();
                        }
                    }
                    case "lm" -> { //load and spawn by name on me
                        String name = splitCommand[3];
                        Autobot autobot = AutobotsDao.getInstance().loadByName(name);

                        if (autobot.getCombatBehavior().getActivityPreferences().getActivityType() == ActivityPreferences.ActivityType.Schedule) {
                            autobot.getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.None);
                        }

                        autobot.setXYZ(activeChar.getX() + Rnd.get(-100, 100), activeChar.getY() + Rnd.get(-100, 100), activeChar.getZ());
                        AutobotsManager.getInstance().spawnAutobot(autobot);
                    }
                    case "ls" -> { //load and spawn selected

                        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().forEach((key, value) -> ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                            Autobot bot = AutobotsDao.getInstance().loadByName(value.getName());
                            if (bot == null) return;
                            if (!bot.isOnline()) AutobotsManager.getInstance().spawnAutobot(bot);
                        }, 0));

                        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().clear();
                    }
                    case "clf" -> { //clear filter
                        ViewStates.getInstance().indexViewState(activeChar).setNameToSearch("");
                        ViewStates.getInstance().indexViewState(activeChar).setPagination(new IndexViewState.Pagination(1, 10));
                    }
                    case "edb" -> {
                        Autobot bot = AutobotsManager.getInstance().getBotFromOnlineOrDb(splitCommand[4]);
                        if (bot == null) {
                            AutobotsUi.getInstance().loadLastActive(activeChar);
                            return false;
                        }
                        switch (splitCommand[3]) {
                            case "st" -> {
                                activeChar.sendPacket(new GMViewCharacterInfo(bot));
                                activeChar.sendPacket(new GMHennaInfo(bot));
                                return true;
                            }
                            case "in" -> {
                                activeChar.sendPacket(new GMViewItemList(bot));
                                activeChar.sendPacket(new GMHennaInfo(bot));
                                return true;
                            }
                            case "sk" -> {
                                activeChar.sendPacket(new GMViewSkillInfo(bot));
                                return true;
                            }
                            case "bf" -> {
                                activeChar.sendPacket(new GMViewBuffs(bot));
                                return true;
                            }
                            case "rc" -> {
                                if (!bot.isInGame()) {
                                    activeChar.sendMessage("You cannot recall a bot that instanceof offline");
                                    return true;
                                }
                                bot.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                                bot.teleToLocation(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getHeading(), activeChar.getInstanceId());
                                return true;
                            }
                            case "gt" -> {
                                if (!bot.isInGame()) {
                                    activeChar.sendMessage("You cannot goto a bot that instanceof offline");
                                    return true;
                                }
                                activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                                activeChar.teleToLocation(bot.getX(), bot.getY(), bot.getZ(), bot.getHeading(), bot.getInstanceId());
                                return true;
                            }
                            case "un" -> {
                                if (!bot.isInGame()) {
                                    activeChar.sendMessage("Cannot unstuck a bot that instanceof offline");
                                    return true;
                                }

                                bot.doCast(SkillData.getInstance().getSkill(2099, 1));
                                return true;
                            }
                            case "dl" -> {
                                if (AutobotsManager.getInstance().deleteAutobot(bot)) {
                                    activeChar.sendMessage("Cannot delete a bot that instanceof clan leader");
                                } else {
                                    ViewStates.getInstance().indexViewState(activeChar).reset();
                                    AutobotsUi.getInstance().loadLastActive(activeChar);
                                }
                                return true;
                            }
                        }
                    }
                    case "ed" -> //edit info
                            AdminUiActions.handleEditInfo(Arrays.asList(splitCommand), activeChar);
                    case "sv" -> { //save info
                        if (AdminUiActions.handleSaveInfo(activeChar, Arrays.asList(splitCommand)))
                            return false;

                        return returnFail(activeChar);
                    }
                    case "slcs" -> AdminUiActions.selectRace(Arrays.asList(splitCommand), activeChar);
                    case "t" -> { // tabs
                        switch (splitCommand[3]) {
                            case "g" -> // general index tab
                                    ViewStates.getInstance().indexViewState(activeChar).setIndexTab(IndexTab.General);
                            case "c" -> // clan index tab
                                    ViewStates.getInstance().indexViewState(activeChar).setIndexTab(IndexTab.Clan);
                            case "bi" -> // bot details info tab
                                    AdminActions.getInstance().selectBotDetailsTab(activeChar, BotDetailsTab.Info);
                            case "bc" -> // bot details combat tab
                                    AdminActions.getInstance().selectBotDetailsTab(activeChar, BotDetailsTab.Combat);
                            case "bs", "bsh" -> { // bot details social tab
                                ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                                if (state instanceof BotDetailsViewState) {
                                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) state;
                                    if (splitCommand.length == 4) {
                                        botDetailsViewState.setActiveTab(BotDetailsTab.Social);
                                        if (splitCommand[3].equals("bsh")) {
                                            botDetailsViewState.setSocialPage(BotDetailsSocialPage.Home);
                                        }
                                    } else {
                                        switch (splitCommand[4]) {
                                            case "sell" -> botDetailsViewState.setSocialPage(BotDetailsSocialPage.CreateSellStore);
                                            case "buy" -> botDetailsViewState.setSocialPage(BotDetailsSocialPage.CreateBuyStore);
                                            case "craft" -> botDetailsViewState.setSocialPage(BotDetailsSocialPage.CreateCraftStore);
                                            case "stop" -> {
                                                if (botDetailsViewState.getActiveBot().isInGame()) {
                                                    botDetailsViewState.getActiveBot().standUp();
                                                }
                                            }
                                            case "a" -> {
                                                switch (splitCommand[5]) {
                                                    case "st" -> { //stand toggle
                                                        Autobot bot = botDetailsViewState.getActiveBot();
                                                        if (!bot.isInGame()) {
                                                            activeChar.sendMessage("Bot instanceof not online");
                                                            AutobotsUi.getInstance().loadLastActive(activeChar);
                                                            return false;
                                                        }

                                                        AdminActions.getInstance().toggleSitting(bot);
                                                    }
                                                    case "ru" -> { //run toggle
                                                        Autobot bot = botDetailsViewState.getActiveBot();
                                                        if (!bot.isInGame()) {
                                                            activeChar.sendMessage("Bot instanceof not online");
                                                            AutobotsUi.getInstance().loadLastActive(activeChar);
                                                            return false;
                                                        }
                                                        bot.setIsRunning(!bot.isRunning());
                                                    }
                                                    case "cont" -> { //control
                                                        Autobot bot = botDetailsViewState.getActiveBot();
                                                        if (!bot.isInGame()) {
                                                            activeChar.sendMessage("Bot instanceof not online");
                                                            AutobotsUi.getInstance().loadLastActive(activeChar);
                                                            return false;
                                                        }

                                                        if (activeChar.calculateDistance(bot, false, false) >= 2000) {
                                                            activeChar.sendMessage("You need to be close to the bot to control it");
                                                            AutobotsUi.getInstance().loadLastActive(activeChar);
                                                            return false;
                                                        }

                                                        if (AutobotHelpers.isControllingBot(activeChar)) {
                                                            Autobot controlledBot = AutobotHelpers.getControllingBot(activeChar);
                                                            if (controlledBot != null) {
                                                                controlledBot.uncontrollBot();
                                                            }
                                                        }
                                                        activeChar.setTarget(bot);
                                                        bot.setController(activeChar);
                                                    }
                                                    case "uncont" -> { //uncontrol
                                                        if (AutobotHelpers.isControllingBot(activeChar)) {
                                                            Autobot controlledBot = AutobotHelpers.getControllingBot(activeChar);
                                                            if (controlledBot != null) controlledBot.uncontrollBot();
                                                        }
                                                    }
                                                }
                                            }
                                            case "s" -> {
                                                switch (splitCommand[5]) {
                                                    case "a" -> {
                                                        Integer itemId = getOrNull(splitCommand, 6, Integer.class);
                                                        if (itemId == null) return false;
                                                        Integer itemcount = getOrNull(splitCommand, 7, Integer.class);
                                                        if (itemcount == null) return false;
                                                        Integer priceperitem = getOrNull(splitCommand, 8, Integer.class);
                                                        if (priceperitem == null) return false;

                                                        L2Item item = ItemTable.getInstance().getTemplate(itemId);
                                                        if (item == null) {
                                                            activeChar.sendMessage("There instanceof no item with that id");
                                                            return false;
                                                        }

                                                        if (!item.isStackable() && itemcount > 1) {
                                                            activeChar.sendMessage("Cannot add more than 1 count for non stackable items");
                                                            return false;
                                                        }

                                                        if (botDetailsViewState.getSellList().size() > botDetailsViewState.getActiveBot().getPrivateSellStoreLimit()) {
                                                            activeChar.sendMessage("You cannot have more than " + botDetailsViewState.getActiveBot().getPrivateSellStoreLimit() + " items");
                                                            return false;
                                                        }

                                                        botDetailsViewState.getSellList().add(new BotDetailsViewState.StoreList(itemId, itemcount, priceperitem));
                                                    }
                                                    case "r" -> {
                                                        int index = Integer.parseInt(splitCommand[6]);
                                                        botDetailsViewState.getSellList().remove(index);
                                                    }
                                                    case "c" -> {
                                                        Autobot bot = botDetailsViewState.getActiveBot();

                                                        if (bot.isInGame()) {
                                                            bot.createPrivateSellStore(botDetailsViewState.getSellList(), botDetailsViewState.getSellMessage(), false);
                                                        } else {
                                                            bot.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                                                            AutobotsManager.getInstance().spawnAutobot(bot);
                                                            bot.createPrivateSellStore(botDetailsViewState.getSellList(), botDetailsViewState.getSellMessage(), false);
                                                        }
                                                    }
                                                }
                                            }
                                            case "b" -> {
                                                switch (splitCommand[5]) {
                                                    case "a" -> {
                                                        Integer itemId = getOrNull(splitCommand, 6, Integer.class);
                                                        if (itemId == null) return false;
                                                        Integer itemcount = getOrNull(splitCommand, 7, Integer.class);
                                                        if (itemcount == null) return false;
                                                        Integer priceperitem = getOrNull(splitCommand, 8, Integer.class);
                                                        if (priceperitem == null) return false;

                                                        L2Item item = ItemTable.getInstance().getTemplate(itemId);
                                                        if (item == null) {
                                                            activeChar.sendMessage("There instanceof no item with that id");
                                                            return false;
                                                        }

                                                        if (!item.isStackable() && itemcount > 1) {
                                                            activeChar.sendMessage("Cannot add more than 1 count for non stackable items");
                                                            return false;
                                                        }

                                                        if (botDetailsViewState.getBuyList().size() > botDetailsViewState.getActiveBot().getPrivateBuyStoreLimit()) {
                                                            activeChar.sendMessage("You cannot have more than " + botDetailsViewState.getActiveBot().getPrivateBuyStoreLimit() + " items");
                                                            return false;
                                                        }
                                                        botDetailsViewState.getBuyList().add(new BotDetailsViewState.StoreList(itemId, itemcount, priceperitem));
                                                    }
                                                    case "r" -> {
                                                        int index = Integer.parseInt(splitCommand[6]);
                                                        botDetailsViewState.getBuyList().remove(index);
                                                    }
                                                    case "c" -> {
                                                        Autobot bot = botDetailsViewState.getActiveBot();

                                                        if (!bot.isInGame()) {
                                                            bot.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                                                            AutobotsManager.getInstance().spawnAutobot(bot);
                                                        }
                                                        bot.createPrivateBuyStore(botDetailsViewState.getBuyList(), botDetailsViewState.getBuyMessage());
                                                    }
                                                }
                                            }
                                            case "c" -> {
                                                switch (splitCommand[5]) {
                                                    case "a" -> {
                                                        String idType = splitCommand[6];
                                                        int id = Integer.parseInt(splitCommand[7]);
                                                        int cost = Integer.parseInt(splitCommand[8]);

                                                        L2RecipeList recipe = idType.equals("RecipeId") ? RecipeData.getInstance().getRecipeList(id) : RecipeData.getInstance().getRecipeByItemId(id);

                                                        if (recipe == null) {
                                                            activeChar.sendMessage("There instanceof no such recipe");
                                                            return returnFail(activeChar);
                                                        }

                                                        if (!recipe.isDwarvenRecipe() && !botDetailsViewState.getActiveBot().hasCommonCraft()) {
                                                            activeChar.sendMessage("This recipe can only be used on Common craft");
                                                            return returnFail(activeChar);
                                                        }

                                                        if (recipe.isDwarvenRecipe() && !botDetailsViewState.getActiveBot().hasDwarvenCraft()) {
                                                            activeChar.sendMessage("This recipe can only be used on Dwarven craft");
                                                            return returnFail(activeChar);
                                                        }

                                                        boolean isDwarven = botDetailsViewState.getCraftList().stream().anyMatch(r -> RecipeData.getInstance().getRecipeList(recipe.getId()).isDwarvenRecipe());
                                                        if (RecipeData.getInstance().getRecipeList(recipe.getId()).isDwarvenRecipe() && isDwarven) {
                                                            activeChar.sendMessage("Cannot mix common recipes with dwarven ones");
                                                            return returnFail(activeChar);
                                                        }

                                                        if (!isDwarven && RecipeData.getInstance().getRecipeList(recipe.getId()).isDwarvenRecipe()) {
                                                            activeChar.sendMessage("Cannot add common recipes with dwarven ones");
                                                            return returnFail(activeChar);
                                                        }

                                                        if (botDetailsViewState.getCraftList().stream()
                                                                .filter(r -> RecipeData.getInstance().getRecipeList(recipe.getId()).isDwarvenRecipe())
                                                                .count() >= botDetailsViewState.getActiveBot().getDwarfRecipeLimit()) {
                                                            activeChar.sendMessage("Dwarven craft limit reached");
                                                            return returnFail(activeChar);
                                                        }

                                                        if (botDetailsViewState.getCraftList().stream()
                                                                .filter(r -> RecipeData.getInstance().getRecipeList(recipe.getId()).isDwarvenRecipe())
                                                                .count() >= botDetailsViewState.getActiveBot().getCommonRecipeLimit()) {
                                                            activeChar.sendMessage("Common craft limit reached");
                                                            return returnFail(activeChar);
                                                        }
//                                                        botDetailsViewState.getCraftList().add(new BotDetailsViewState.CraftList(recipe, cost)); //TODO
                                                    }
                                                    case "r" -> {
                                                        int index = Integer.parseInt(splitCommand[6]);
                                                        botDetailsViewState.getCraftList().remove(index);
                                                    }
                                                    case "c" -> {
                                                        Autobot bot = botDetailsViewState.getActiveBot();

                                                        if (bot.isInGame()) {
//                                                            bot.createPrivateCraftStore(botDetailsViewState.getCraftList(), botDetailsViewState.getCraftMessage(), false); //TODO
                                                        } else {
                                                            bot.setXYZ(activeChar.getX(), activeChar.getY(), activeChar.getZ());
                                                            AutobotsManager.getInstance().spawnAutobot(bot);
//                                                            bot.createPrivateCraftStore(botDetailsViewState.craftList, botDetailsViewState.craftMessage, activeChar); //TODO
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            case "bsk" -> // bot details skills tab
                                    AdminActions.getInstance().selectBotDetailsTab(activeChar, BotDetailsTab.Skills);
                            case "bch" -> // bot details chat tab
                                    AdminActions.getInstance().selectBotDetailsTab(activeChar, BotDetailsTab.Chat);
                            case "chall" -> { // bot details chat all tab
                                ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                                if (state instanceof BotDetailsViewState) {
                                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) state;
                                    botDetailsViewState.setActiveTab(BotDetailsTab.Chat);
                                    botDetailsViewState.setChatTab(BotDetailsChatTab.All);
                                }
                            }
                            case "chapm" -> { // bot details chat pm tab
                                ViewState state = ViewStates.getInstance().getActiveState(activeChar);
                                if (state instanceof BotDetailsViewState) {
                                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) state;
                                    botDetailsViewState.setActiveTab(BotDetailsTab.Chat);
                                    botDetailsViewState.setChatTab(BotDetailsChatTab.Pms);
                                }
                            }
                        }
                    }
                    case "c" -> {
                        ViewState activeState = ViewStates.getInstance().getActiveState(activeChar);
                        switch (splitCommand[3]) {
                            case "ban" -> {
                                if (activeState instanceof BotDetailsViewState && ((BotDetailsViewState) activeState).getActiveTab() == BotDetailsTab.Info) {
                                    ((BotDetailsViewState) activeState).setActivityEditAction(BotDetailsViewState.ActivityEditAction.None);
                                }
                            }
                            case "bau" -> {
                                if (activeState instanceof BotDetailsViewState && ((BotDetailsViewState) activeState).getActiveTab() == BotDetailsTab.Info) {
                                    ((BotDetailsViewState) activeState).setActivityEditAction(BotDetailsViewState.ActivityEditAction.Uptime);
                                }
                            }
                            case "bas" -> {
                                if (activeState instanceof BotDetailsViewState && ((BotDetailsViewState) activeState).getActiveTab() == BotDetailsTab.Info) {
                                    ((BotDetailsViewState) activeState).setActivityEditAction(BotDetailsViewState.ActivityEditAction.Schedule);
                                }
                            }
                        }
                    }
                    case "lsme" -> { //load and spawn selected on me
                        List<AutobotInfo> bots = ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().values().stream().filter(a -> a != null && !a.isOnline()).collect(Collectors.toList());

                        bots.forEach(b -> {
                            Autobot autobot = AutobotsDao.getInstance().loadByName(b.getName());
                            if (autobot != null) {
                                autobot.setXYZ(activeChar.getX() + Rnd.get(-150, 150), activeChar.getY() + Rnd.get(-150, 150), activeChar.getZ());
                                AutobotsManager.getInstance().spawnAutobot(autobot);
                            }
                        });
                        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().clear();
                    }
                    case "dess" -> { //despawn selected

                        List<AutobotInfo> bots = ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().values().stream().filter(AutobotInfo::isOnline).collect(Collectors.toList());

                        bots.forEach(b -> {
                            Autobot autobot = AutobotsManager.getInstance().getActiveBots().getOrDefault(b.getName(), null);
                            if (autobot != null) {
                                AdminActions.getInstance().despawnAutobot(autobot);
                            }
                        });
                        ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().clear();
                    }
                    case "chall" -> // select all bots
                            AutobotsDao.getInstance().getAllInfo().forEach(info -> ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().put(info.getName(), info));
                    case "cls" -> //clear selected
                            ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().clear();
                    case "des" -> { //despawn by name
                        String name = splitCommand[3];
                        Autobot bot = AutobotsManager.getInstance().getActiveBots().get(name);
                        if (bot == null) return false;
                        AdminActions.getInstance().despawnAutobot(bot);
                    }
                    case "det" -> { //despawn target
                        if (activeChar.getTarget() == null || !(activeChar.getTarget() instanceof Autobot))
                            return false;
                        AdminActions.getInstance().despawnAutobot((Autobot) activeChar.getTarget());
                    }
                    case "der" -> { //despawn in radius
                        String radius = getOrNull(splitCommand, 3, String.class);
                        if (radius == null) return false;
                        AdminActions.getInstance().despawnBotsInRadius(activeChar, radius);
                    }
                    case "eb" -> { //edit bot
                        String name = splitCommand[3];
                        Autobot bot = AutobotsManager.getInstance().getBotFromOnlineOrDb(name);
                        if (bot == null) return false;
                        AutobotsUi.getInstance().loadBotDetails(activeChar, bot);
                        return true;
                    }
                    case "crb" -> { //create bot
                        ViewStates.getInstance().createBotViewState(activeChar).reset();
                        AutobotsUi.getInstance().loadCreateBot(activeChar);
                        return true;
                    }
                    case "csend" -> { // chat send
                        String botName = splitCommand[3];
                        Autobot autobot = AutobotsManager.getInstance().getActiveBots().getOrDefault(botName, null);
                        if (autobot == null) return false;
                        String message = String.join(" ", Arrays.asList(splitCommand).subList(4, splitCommand.length));
                        var chatType = 0;
                        String target = null;
                        message = message.replace("\r\n", " ").replaceAll("\\n", "");
                        if (message.isEmpty()) {
                            activeChar.sendMessage("You have to say something");
                            return false;
                        }

                        switch (message.charAt(0)) {
                            case '!' -> { // shout chat
                                chatType = 1;
                                message = message.replaceFirst("!", "");
                                autobot.addChat(new BotChat(ChatType.Shout, autobot.getName(), message));
                            }
                            case '+' -> { // shout chat
                                chatType = 8;
                                message = message.replaceFirst("\\+", "");
                                autobot.addChat(new BotChat(ChatType.Trade, autobot.getName(), message));
                            }
                            case '"' -> { // whisper chat
                                chatType = 2;
                                message = message.replaceFirst("\"", "");
                                String[] splitOnSpace = message.split(" ");

                                if (splitOnSpace.length == 0 || splitOnSpace.length == 1) {
                                    activeChar.sendMessage("You have to say something");
                                    return returnFail(activeChar);
                                }

                                target = splitOnSpace[0];

                                if (L2World.getInstance().getPlayer(target) == null) {
                                    activeChar.sendMessage("Target player instanceof not online");
                                    return returnFail(activeChar);
                                }
                                String messageJoined = Arrays.stream(splitOnSpace).skip(1).collect(Collectors.joining(" "));
                                autobot.addChat(new BotChat(ChatType.PmSent, autobot.getName(), messageJoined));
                            }
                            case '/' -> { // command
                                activeChar.sendMessage("Commands are not supported (yet)");
                                return returnFail(activeChar);
                            }
                            default -> autobot.addChat(new BotChat(ChatType.All, autobot.getName(), message));
                        }

                        IChatHandler handler = ChatHandler.getInstance().getHandler(chatType);
                        handler.handleChat(chatType, autobot, target, message);
                    }
                    case "cln" -> { //clan functions
                        switch (splitCommand[3]) {
                            case "rmc" -> { //remove clan
                                String clanName = getOrNull(splitCommand, 4);
                                if (clanName == null || clanName.isEmpty()) {
                                    activeChar.sendMessage("There instanceof no clan with that name.");
                                    return returnFail(activeChar);
                                }
                                L2Clan clan = ClanTable.getInstance().getClans().stream().filter(c -> c.getName().equals(clanName)).findFirst().orElse(null);

                                if (clan == null) {
                                    activeChar.sendMessage("There instanceof no clan with that name.");
                                    return returnFail(activeChar);
                                }

                                if (clan.getAllyId() != 0) {
                                    activeChar.sendMessage("You cannot delete a clan in an ally. Delete the ally first.");
                                    return returnFail(activeChar);
                                }

                                ClanTable.getInstance().destroyClan(clan.getId());
                            }
                            case "rmm" -> //remove selected from clan
                                    AdminActions.getInstance().removeSelectedBotsFromClan(activeChar);
                        }
                    }
                    case "clc" -> { //clan create
                        if (ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().isEmpty()) {
                            activeChar.sendMessage("You need to select at least one bot");
                            return true;
                        }

                        String clanName = splitCommand[3];
                        int clanLevel = getOrElse(splitCommand, 4, Integer.class).orElse(1);
                        String clanLeaderName = splitCommand[5];
                        String crestUrl = getOrElse(splitCommand, 6, String.class).orElse("");
                        if (clanName.isEmpty()) {
                            activeChar.sendMessage("You need a clan name to create a clan.");
                            return returnFail(activeChar);
                        }

                        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                            Autobot leaderBot = AutobotsManager.getInstance().getBotFromOnlineOrDb(clanLeaderName);
                            leaderBot.setClanCreateExpiryTime(0);
                            L2Clan clan = ClanTable.getInstance().createClan(leaderBot, clanName);
                            if (clan != null) {
                                if (!leaderBot.isOnline()) {
                                    AutobotsDao.getInstance().saveAutobot(leaderBot);
                                }
                                if (clanLevel != 0) {
                                    clan.changeLevel(clanLevel);
                                }
                                if (clanLevel >= 3 && !crestUrl.isEmpty()) {
                                    int crestId = CrestService.uploadCret(crestUrl, L2Crest.CrestType.PLEDGE);
                                    clan.changeClanCrest(crestId);
                                }
                                activeChar.sendMessage("Clan "+clanName+" have been created. Clan leader is" + leaderBot.getName() + ".");
                            } else {
                                activeChar.sendMessage("There was a problem while creating the clan.");
                            }
                            ViewStates.getInstance().indexViewState(activeChar).getSelectedBots().values().stream()
                                    .filter(b -> !b.getName().equals(clanLeaderName))
                                    .forEach(b -> {
                                        Autobot bot = AutobotsManager.getInstance().getBotFromOnlineOrDb(b.getName());
                                        if (bot != null && clan != null) {
                                            clan.addClanMember(bot);
                                            bot.setClanPrivileges(clan.getRankPrivs(bot.getPowerGrade()));
                                            clan.broadcastToOtherOnlineMembers(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_JOINED_CLAN).addCharName(activeChar), activeChar);
                                            clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(activeChar), activeChar);
                                            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                                            bot.setClanJoinExpiryTime(0);
                                            if (bot.isOnline()) {
                                                bot.broadcastUserInfo();
                                            } else {
                                                AutobotsDao.getInstance().saveAutobot(bot);
                                            }
                                        }
                                    });
                        }, 0);
                    }
                    case "todo" -> activeChar.sendMessage("Not Implemented yet");
                    default -> AutobotsUi.getInstance().loadLastActive(activeChar);
                }
            }
        }
        AutobotsUi.getInstance().
                loadLastActive(activeChar);
        return false;
    }

    private boolean returnFail(L2PcInstance player) {
        AutobotsUi.getInstance().loadLastActive(player);
        return false;
    }

    @Override
    public String[] getAdminCommandList() {
        return COMMANDS;
    }


}