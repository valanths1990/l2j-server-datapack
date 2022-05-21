package com.l2jserver.datapack.autobots.ui;

import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.datapack.autobots.AutobotNameService;
import com.l2jserver.datapack.autobots.AutobotScheduler;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.DuelistSkillPreferences;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.ui.states.*;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsCombatEditAction;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import static com.l2jserver.datapack.autobots.utils.Util.getOrElse;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

public class AdminUiActions {

    public static void selectRace(List<String> splitCommand, L2PcInstance activeChar) {
        String raceName = splitCommand.get(3);
        CreateBotViewState state = ViewStates.getInstance().createBotViewState(activeChar);
        state.getBotDetails().setRace(raceName);
    }

    public static boolean handleSaveInfo(L2PcInstance activeChar, List<String> splitCommand) {
        ViewState activeState = ViewStates.getInstance().getActiveState(activeChar);
        switch (splitCommand.get(3)) {
            case "bass" -> {
                if (activeState instanceof BotDetailsViewState) {
                    String loginTime = splitCommand.get(4) + ":" + splitCommand.get(5);
                    String logoutTime = splitCommand.get(6) + ":" + splitCommand.get(7);

                    LocalDateTime dateTimeNow = LocalDateTime.now(Clock.systemUTC());
                    LocalDateTime loginDateTime = LocalDateTime.parse(dateTimeNow.getYear() + "-" + (dateTimeNow.getMonthValue() < 10 ? "0" + dateTimeNow.getMonthValue() :
                            dateTimeNow.getMonthValue()) + "-" + (dateTimeNow.getDayOfMonth() < 10 ? "0" + dateTimeNow.getDayOfMonth() : dateTimeNow.getDayOfMonth()) + loginTime, AutobotScheduler.getInstance().getFormatter());
                    LocalDateTime logoutDateTime = LocalDateTime.parse(dateTimeNow.getYear() + "-" + (dateTimeNow.getMonthValue() < 10 ? "0" + dateTimeNow.getMonthValue() :
                            dateTimeNow.getMonthValue()) + "-" + (dateTimeNow.getDayOfMonth() < 10 ? "0" + dateTimeNow.getDayOfMonth() : dateTimeNow.getDayOfMonth()) + logoutTime, AutobotScheduler.getInstance().getFormatter());

                    if (loginDateTime.isAfter(logoutDateTime)) {
                        activeChar.sendMessage("Login time needs to be before the logout time");
                        return true;
                    } else {
                        ((BotDetailsViewState) activeState).getActiveBot().getCombatBehavior().getActivityPreferences().setLoginTime(logoutTime);
                        ((BotDetailsViewState) activeState).getActiveBot().getCombatBehavior().getActivityPreferences().setLogoutTime(logoutTime);
                        AutobotsDao.getInstance().saveActivityPreferences(((BotDetailsViewState) activeState).getActiveBot());
                        if (logoutDateTime.isBefore(dateTimeNow)) {
                            ((BotDetailsViewState) activeState).getActiveBot().despawn();
                            AutobotScheduler.getInstance().removeBot(((BotDetailsViewState) activeState).getActiveBot());
                        } else {
                            AutobotScheduler.getInstance().addBot(((BotDetailsViewState) activeState).getActiveBot());
                        }

                    }
                }
            }
            case UiComponents.TargetRadius -> {
                int radius = getOrElse(splitCommand, 4, Integer.class).orElse(0);
                if (radius < 100 || radius > 20000) {
                    activeChar.sendMessage("Radius needs to be between 100 and 20000");
                    return true;
                }

                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setTargetingRadius(radius);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }

            case UiComponents.CreateBotName -> {
                if (activeState instanceof CreateBotViewState) {
                    CreateBotViewState createBotViewState = (CreateBotViewState) activeState;
                    String name = getOrElse(splitCommand, 4, String.class).orElse(createBotViewState.getBotDetails().getName());
                    if (!AutobotNameService.getInstance().nameIsValid(name)) {
                        activeChar.sendMessage("Name instanceof invalid");
                        return true;
                    }
                    createBotViewState.setEditAction(CreateBotViewState.CreateBotEditAction.None);
                    createBotViewState.getBotDetails().setName(name);
                }
            }
            case UiComponents.CreateBotLevel -> {
                if (activeState instanceof CreateBotViewState) {
                    CreateBotViewState createBotViewState = (CreateBotViewState) activeState;
                    int level = getOrElse(splitCommand, 4, Integer.class).orElse(createBotViewState.getBotDetails().getLevel());
                    if (level < 1 || level > 80) {
                        activeChar.sendMessage("Level needs to be between 1 and 80");
                        return true;
                    }
                    createBotViewState.setEditAction(CreateBotViewState.CreateBotEditAction.None);
                    createBotViewState.getBotDetails().setLevel(level);
                }
            }
            case UiComponents.CreateBotWeaponEnch -> {
                if (activeState instanceof CreateBotViewState) {
                    CreateBotViewState createBotViewState = (CreateBotViewState) activeState;
                    int enchant = getOrElse(splitCommand, 4, Integer.class).orElse(createBotViewState.getBotDetails().getWeaponEnchant());

                    if (enchant < 0 || enchant > 65535) {
                        activeChar.sendMessage("Enchant needs to be between 0 and 65535");
                        return true;
                    }
                    createBotViewState.setEditAction(CreateBotViewState.CreateBotEditAction.None);
                    createBotViewState.getBotDetails().setWeaponEnchant(enchant);
                }
            }
            case UiComponents.CreateBotArmorEnch -> {
                if (activeState instanceof CreateBotViewState) {
                    CreateBotViewState createBotViewState = (CreateBotViewState) activeState;
                    int enchant = getOrElse(splitCommand, 4, Integer.class).orElse(createBotViewState.getBotDetails().getArmorEnchant());

                    if (enchant < 0 || enchant > 65535) {
                        activeChar.sendMessage("Enchant needs to be between 0 and 65535");
                        return true;
                    }
                    createBotViewState.setEditAction(CreateBotViewState.CreateBotEditAction.None);
                    createBotViewState.getBotDetails().setArmorEnchant(enchant);
                }
            }
            case UiComponents.CreateBotJewelEnch -> {
                if (activeState instanceof CreateBotViewState) {
                    CreateBotViewState createBotViewState = (CreateBotViewState) activeState;

                    int enchant = getOrElse(splitCommand, 4, Integer.class).orElse(createBotViewState.getBotDetails().getJewelEnchant());
                    if (enchant < 0 || enchant > 65535) {
                        activeChar.sendMessage("Enchant needs to be between 0 and 65535");
                        return true;
                    }
                    createBotViewState.setEditAction(CreateBotViewState.CreateBotEditAction.None);
                    createBotViewState.getBotDetails().setJewelEnchant(enchant);
                }
            }
            case UiComponents.TargetPref -> {
                TargetingPreference pref = TargetingPreference.valueOf(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setTargetingPreferences(pref);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.AttackPlayerTypeUi -> {
                AttackPlayerType pref = AttackPlayerType.valueOf(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setAttackPlayerType(pref);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.KiteRadius -> {
                int pref = Integer.parseInt(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof ArcherCombatPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((ArcherCombatPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setKiteRadius(pref);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.IsKiting -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof ArcherCombatPreferences) {
                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((ArcherCombatPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setKiting(checked);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.SummonsPet -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof PetOwnerPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((PetOwnerPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setSummonPet(checked);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.PetAssists -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof PetOwnerPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((PetOwnerPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setPetAssists(checked);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.PetUsesShots -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof PetOwnerPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((PetOwnerPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setPetUsesShots(checked);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.PetHasBuffs -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof PetOwnerPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((PetOwnerPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences()).setPetHasBuffs(checked);
                        AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.UseSkillsOnMobs -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof DuelistSkillPreferences) {

                        botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                        ((DuelistSkillPreferences) botDetailsViewState.getActiveBot().getCombatBehavior().getSkillPreferences()).setUseSkillsOnMobs(checked);
                        AutobotsDao.getInstance().saveSkillPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.UseCpPots -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;

                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setUseGreaterCpPots(checked);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }

            case UiComponents.UseGhPots -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setUseGreaterHealingPots(checked);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.UseQhPots -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCombatEditAction(BotDetailsCombatEditAction.None);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getCombatPreferences().setUseQuickHealingPots(checked);
                    AutobotsDao.getInstance().saveCombatPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.ActivityNoneActive -> {
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().getActivityType() != ActivityPreferences.ActivityType.None) {
                        botDetailsViewState.setActivityEditAction(BotDetailsViewState.ActivityEditAction.None);
                        botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.None);
                        AutobotScheduler.getInstance().removeBot(botDetailsViewState.getActiveBot());
                        AutobotsDao.getInstance().saveActivityPreferences(botDetailsViewState.getActiveBot());
                    }
                }
            }
            case UiComponents.ActivityUptimeActive -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (checked) {
                        botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.Uptime);
                    } else {
                        botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.None);
                    }
                    AutobotsDao.getInstance().saveActivityPreferences(botDetailsViewState.getActiveBot());
                    AutobotScheduler.getInstance().removeBot(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.ActivityScheduleActive -> {
                boolean checked = Boolean.parseBoolean(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    if (checked) {
                        botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.Schedule);
                        AutobotScheduler.getInstance().addBot(botDetailsViewState.getActiveBot());
                    } else {
                        botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.None);
                        AutobotScheduler.getInstance().removeBot(botDetailsViewState.getActiveBot());
                    }
                    AutobotsDao.getInstance().saveActivityPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.EditUptime -> {
                int uptime = Integer.parseInt(splitCommand.get(4));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setActivityEditAction(BotDetailsViewState.ActivityEditAction.Uptime);
                    botDetailsViewState.getActiveBot().getCombatBehavior().getActivityPreferences().setUptimeMinutes(uptime);
                    AutobotsDao.getInstance().saveActivityPreferences(botDetailsViewState.getActiveBot());
                }
            }
            case UiComponents.PrivateSellMessage -> {
                String sellMessage = String.join(" ", splitCommand.subList(4, splitCommand.size()));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setSellMessage(sellMessage);
                    botDetailsViewState.setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.None);
                }
            }
            case UiComponents.PrivateBuyMessage -> {
                String buyMessage = String.join(" ", splitCommand.subList(4, splitCommand.size()));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setBuyMessage(buyMessage);
                    botDetailsViewState.setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.None);
                }
            }
            case UiComponents.PrivateCraftMessage -> {
                String craftMessage = String.join(" ", splitCommand.subList(4, splitCommand.size()));
                if (activeState instanceof BotDetailsViewState) {
                    BotDetailsViewState botDetailsViewState = (BotDetailsViewState) activeState;
                    botDetailsViewState.setCraftMessage(craftMessage);
                    botDetailsViewState.setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.None);
                }
            }
            case UiComponents.EditThinkIteration -> {
                if (activeState instanceof SettingsViewState) {
                    SettingsViewState settingsViewState = (SettingsViewState) activeState;
                    long duration = Long.parseLong(splitCommand.get(4));
                    if (duration < 200 || duration > 5000) {
                        activeChar.sendMessage("Value needs to be between 200 and 5000 ms");
                        return false;
                    }

                    AutobotData.getInstance().getSettings().iterationDelay = duration;
                    settingsViewState.setEditAction(SettingsViewState.SettingsEditAction.None);
                    AutobotData.getInstance().getSettings().save();
                }
            }
            case UiComponents.EditDefaultTitle -> {
                if (activeState instanceof SettingsViewState) {
                    SettingsViewState settingsViewState = (SettingsViewState) activeState;
                    AutobotData.getInstance().getSettings().defaultTitle = String.join(" ", splitCommand.subList(4, splitCommand.size()));
                    settingsViewState.setEditAction(SettingsViewState.SettingsEditAction.None);
                    AutobotData.getInstance().getSettings().save();
                }
            }
            case UiComponents.EditTargetingRange -> {
                if (activeState instanceof SettingsViewState) {
                    SettingsViewState settingsViewState = (SettingsViewState) activeState;
                    int range = Integer.parseInt(splitCommand.get(4));
                    if (range < 100 || range > 10000) {
                        activeChar.sendMessage("Value needs to be between 100 and 10000 yards");
                        return false;
                    }

                    AutobotData.getInstance().getSettings().targetingRange = range;
                    settingsViewState.setEditAction(SettingsViewState.SettingsEditAction.None);
                    AutobotData.getInstance().getSettings().save();
                }
            }
        }
        return false;
    }

    public static void handleEditInfo(List<String> splitCommand, L2PcInstance activeChar) {
        ViewState viewState = ViewStates.getInstance().getActiveState(activeChar);
        switch (splitCommand.get(3)) {
            case UiComponents.TargetRadius -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setCombatEditAction(BotDetailsCombatEditAction.TargetRadius);
                }
            }
            case UiComponents.KiteRadius -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setCombatEditAction(BotDetailsCombatEditAction.KiteRadius);
                }
            }
            case UiComponents.CreateBotName -> {
                if (viewState instanceof CreateBotViewState) {
                    ((CreateBotViewState) viewState).setEditAction(CreateBotViewState.CreateBotEditAction.EditingName);
                }
            }
            case UiComponents.CreateBotLevel -> {
                if (viewState instanceof CreateBotViewState) {
                    ((CreateBotViewState) viewState).setEditAction(CreateBotViewState.CreateBotEditAction.EditingLevel);
                }
            }
            case UiComponents.CreateBotWeaponEnch -> {
                if (viewState instanceof CreateBotViewState) {
                    ((CreateBotViewState) viewState).setEditAction(CreateBotViewState.CreateBotEditAction.EditingWeaponEnchant);
                }
            }
            case UiComponents.CreateBotArmorEnch -> {
                if (viewState instanceof CreateBotViewState) {
                    ((CreateBotViewState) viewState).setEditAction(CreateBotViewState.CreateBotEditAction.EditingArmorEnchant);
                }
            }
            case UiComponents.CreateBotJewelEnch -> {
                if (viewState instanceof CreateBotViewState) {
                    ((CreateBotViewState) viewState).setEditAction(CreateBotViewState.CreateBotEditAction.EditingJewelsEnchant);
                }
            }
            case UiComponents.EditUptime -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setActivityEditAction(BotDetailsViewState.ActivityEditAction.EditUptime);
                }
            }
            case UiComponents.PrivateSellMessage -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.SellMessage);
                }
            }
            case UiComponents.PrivateBuyMessage -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.BuyMessage);
                }
            }
            case UiComponents.PrivateCraftMessage -> {
                if (viewState instanceof BotDetailsViewState) {
                    ((BotDetailsViewState) viewState).setSocialEditAction(BotDetailsViewState.BotDetailsSocialEditAction.CraftMessage);
                }
            }
            case UiComponents.EditThinkIteration -> {
                if (viewState instanceof SettingsViewState) {
                    ((SettingsViewState) viewState).setEditAction(SettingsViewState.SettingsEditAction.ThinkIteration);
                }
            }
            case UiComponents.EditDefaultTitle -> {
                if (viewState instanceof SettingsViewState) {
                    ((SettingsViewState) viewState).setEditAction(SettingsViewState.SettingsEditAction.DefaultTitle);
                }
            }
            case UiComponents.EditTargetingRange -> {
                if (viewState instanceof SettingsViewState) {
                    ((SettingsViewState) viewState).setEditAction(SettingsViewState.SettingsEditAction.TargetingRange);
                }
            }
        }
    }
}
