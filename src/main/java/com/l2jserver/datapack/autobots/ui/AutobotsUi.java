package com.l2jserver.datapack.autobots.ui;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.datapack.autobots.AutobotsManager;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.DuelistSkillPreferences;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.models.AutobotInfo;
import com.l2jserver.datapack.autobots.models.BotChat;
import com.l2jserver.datapack.autobots.models.ChatType;
import com.l2jserver.datapack.autobots.models.CreateBotDetails;
import com.l2jserver.datapack.autobots.ui.html.HtmlAlignment;
import com.l2jserver.datapack.autobots.ui.states.*;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsChatTab;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsCombatEditAction;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsTab;
import com.l2jserver.datapack.autobots.ui.tabs.IndexTab;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.IconsTable;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.skills.Skill;

import java.time.Clock;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AutobotsUi {
    private static final int chatPageSize = 15;
    private final ViewStates viewStates = ViewStates.getInstance();


    public void loadLastActive(L2PcInstance player) {
        ViewState state = viewStates.getActiveState(player);
        if (state instanceof IndexViewState) {
            loadIndex(player);
        } else if (state instanceof BotDetailsViewState) {
            loadBotDetails(player, AutobotsManager.getInstance().getBotFromOnlineOrDb(((BotDetailsViewState) state).getActiveBot().getName()));
        } else if (state instanceof CreateBotViewState) {
            loadCreateBot(player);
        } else if (state instanceof SettingsViewState) {
            loadSettings(player);
        }
    }


    private void loadIndex(L2PcInstance player) {
        IndexViewState state = viewStates.indexViewState(player);
        String nameSearch = state.getNameToSearch();
        int pageNumber = state.getPagination().first;
        int pageSize = state.getPagination().second;
        int totalPages = totalPageCount(nameSearch, pageSize);
        String html = Util.readFileText("html/views/index.htv")
                .replace("{{onord}}", getOrdering(state.getBotOrdering(), IndexBotOrdering.OnAsc, IndexBotOrdering.OnDesc, "ondesc", "onasc", "ondesc"))
                .replace("{{lvlord}}", getOrdering(state.getBotOrdering(), IndexBotOrdering.LevelAsc, IndexBotOrdering.LevelDesc, "lvldesc", "lvlasc", "lvldesc"))
                .replace("{{index_checkbox.ptv}}", indexCheckboxPartialView(state))
                .replace("{{index_filter.ptv}}", indexFilterPartialView(state))
                .replace("{{index_tabs.ptv}}", indexTabsPartialView(state))
                .replace("{{index_tabtable.ptv}}", indexTabContentPartialView(state))
                .replace("{{listbotsrow.ptv}}", partialListBots(state, nameSearch, pageNumber, pageSize))
                .replace("{{selected_options.ptv}}", loadSelectedPartialView(state))
                .replace("{{activebotscount}}", AutobotsManager.getInstance().getActiveBots().size() + "")
                .replace("{{pagination}}", getPagination(pageNumber, pageSize, totalPages, nameSearch));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    private String indexCheckboxPartialView(IndexViewState state) {
        boolean selected = !state.getSelectedBots().isEmpty();
        return Util.readFileText("html/views/partialviews/index_checkbox.ptv")
                .replace("{{cmd}}", selected ? "cls" : "chall")
                .replace("{{checked}}", selected ? "_checked" : "");
    }

    private String indexFilterPartialView(IndexViewState state) {
        if (state.getNameToSearch().isEmpty()) return "";
        return Util.readFileText("html/views/partialviews/index_filter.ptv")
                .replace("{{filter}}", state.getNameToSearch());
    }

    private String indexTabsPartialView(IndexViewState state) {
        return Util.readFileText("html/views/partialviews/index_tabs.ptv")
                .replace("{{generalselected}}", state.getIndexTab() == IndexTab.General ? "siege_tab1" : "siege_tab3")
                .replace("{{clanselected}}", state.getIndexTab() == IndexTab.Clan ? "siege_tab1" : "siege_tab3");
    }

    private String indexTabContentPartialView(IndexViewState state) {
        return switch (state.getIndexTab()) {
            case General -> Util.readFileText("html/views/partialviews/index_tab_table_general.ptv");
            case Clan -> {
                String memberNames = state.getSelectedBots().isEmpty() ? "" : state.getSelectedBots().keySet().stream().limit(10).collect(Collectors.joining(";"));
                yield Util.readFileText("html/views/partialviews/index_tab_table_clan.ptv")
                        .replace("{{membernames}}", memberNames);
            }
            default -> "";
        };
    }

    private String partialListBots(IndexViewState state, String nameSearch, int pageNumber, int pageSize) {
        String rowHtml = Util.readFileText("html/views/partialviews/listbotsrow.ptv");
        List<AutobotInfo> bots = AutobotsDao.getInstance().searchForAutobots(nameSearch, pageNumber, pageSize, state.getBotOrdering());
        StringBuilder sb = new StringBuilder();
        bots.forEach(b -> {
            sb.append(
                    rowHtml.replace("{{listbotsrow_offline.ptv}}", !b.isOnline() ? Util.readFileText("html/views/partialviews/listbotsrow_offline.ptv") : "")
                            .replace("{{listbotsrow_online.ptv}}", b.isOnline() ? Util.readFileText("html/views/partialviews/listbotsrow_online.ptv") : "")
                            .replace("{{listbotsrow_checked.ptv}}", state.getSelectedBots().containsKey(b.getName()) ? Util.readFileText("html/views/partialviews/listbotsrow_checked.ptv") : "")
                            .replace("{{listbotsrow_unchecked.ptv}}", !state.getSelectedBots().containsKey(b.getName()) ? Util.readFileText("html/views/partialviews/listbotsrow_unchecked.ptv") : "")
                            .replace("{{onlineicon}}", b.isOnline() ? "L2UI_CH3.msnicon1" : "L2UI_CH3.msnicon4")
                            .replace("{{level}}", b.getLevel() + "")
                            .replace("{{name}}", b.getName())
                            .replace("{{classname}}", b.getClassId().toString())
                            .replace("{{claninfo}}", getClanInfo(b))
                            .replace("{{allyinfo}}", getAllyInfo(b))
                            .replace("{{pageNumber}}", pageNumber + "")
                            .replace("{{pageSize}}", pageSize + "")
                            .replace("{{behavior}}", "To remove")
                            .replace("{{botId}}", b.getBotId() + "")

            );
        });
        return sb.toString();
    }

    private String getClanInfo(AutobotInfo it) {
        L2Clan clan = ClanTable.getInstance().getClan(it.getClanId());
        return clan == null ? "no clan" : clan.getName();
    }

    private String getAllyInfo(AutobotInfo it) {
        L2Clan clan = ClanTable.getInstance().getClan(it.getClanId());
        if (clan == null) return "no Ally";
        else if (clan.getAllyName() == null || clan.getAllyName().isEmpty()) return "no Ally";
        return clan.getAllyName();
    }

    private String loadSelectedPartialView(IndexViewState state) {
        if (state.getSelectedBots().isEmpty()) return "";

        return Util.readFileText("html/views/partialviews/selected_options.ptv")
                .replace("{{selectedcount}}", state.getSelectedBots().size() + "")
                .replace("{{selected_options_offline.ptv}}", state.getSelectedBots().values().stream().anyMatch(AutobotInfo::isOnline) ? Util.readFileText("html/views/partialviews/selected_options_offline.ptv") : "")
                .replace("{{selected_options_online.ptv}}", state.getSelectedBots().values().stream().anyMatch(AutobotInfo::isOnline) ? Util.readFileText("html/views/partialviews/selected_options_online.ptv") : "");
    }

    private String getPagination(int pageNumber, int pageSize, int totalPages, String filter) {
        int delta = 2;
        int left = pageNumber - delta;
        Integer l = null;
        int right = pageNumber + delta + 1;
        List<Integer> range = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        for (int i = 1; i <= totalPages; i++) {
            if (i == 1 || i == totalPages || (i <= left && i <= right)) {
                range.add(i);
            }
        }

        for (int i : range) {
            if (l != null) {
                if (i - l == 2) {
                    sb.append("<td><a action=\"bypass admin_a b s ").append(l + 1).append(" ").append(pageSize).append(" ").append(filter).append("\">").append(l + 1).append("</a></td>");
                } else if (i - l != 1) {
                    sb.append("<td>...</td>");
                }
            }
            if (i == pageNumber) {
                sb.append("<td>").append(i).append("</td>");
            } else {
                sb.append("<td><a action=\"bypass admin_a b s ").append(i).append(" ").append(pageSize).append(" ").append(filter).append("\">").append(i).append("</a></td>");
            }
            l = i;
        }

        return sb.toString();
    }

    private void loadSettings(L2PcInstance player) {
        SettingsViewState state = viewStates.settingsViewState(player);
        String html = Util.readFileText("html/view/settings.htv")
                .replace("{{iteretiontxt}}", UiComponents.textbotComponent(UiComponents.EditThinkIteration, "Think iteration (ms)", "thinkms", AutobotData.getInstance().getSettings().iterationDelay + "", state.getEditAction() == SettingsViewState.SettingsEditAction.ThinkIteration, HtmlAlignment.Center))
                .replace("{{titletxt}}", UiComponents.textbotComponent(UiComponents.EditDefaultTitle, "Default title", "deftit", AutobotData.getInstance().getSettings().defaultTitle, state.getEditAction() == SettingsViewState.SettingsEditAction.DefaultTitle, HtmlAlignment.Center))
                .replace("{{rangetxt}}", UiComponents.textbotComponent(UiComponents.EditTargetingRange, "Default targeting range", "deftgr", AutobotData.getInstance().getSettings().targetingRange + "", state.getEditAction() == SettingsViewState.SettingsEditAction.TargetingRange, HtmlAlignment.Center));
        CommunityBoardHandler.separateAndSend(html, player);
    }

    private String getOrdering(IndexBotOrdering botOrdering, IndexBotOrdering ascOrdering, IndexBotOrdering descOrdering, String default_, String asc, String desc) {
        return botOrdering == ascOrdering ? desc : (botOrdering == descOrdering ? asc : default_);
    }

    public void loadBotDetails(L2PcInstance player, Autobot autobot) {
        BotDetailsViewState state = viewStates.botDetailsViewState(player, autobot);
        String html = Util.readFileText("html/views/bot_details.htv")
                .replace("{{top_buttons}}", botDetailsTopButtonsPartialView(state))
                .replace("{{activebotscount}}", AutobotsManager.getInstance().getActiveBots().size() + "")
                .replace("{{botdetails_tabs.ptv}}", botDetailsTabsPartialView(state))
                .replace("{{content}}", contentPartialView(player, state))
                .replace("{{name}}", autobot.getName());
        CommunityBoardHandler.separateAndSend(html, player);
    }

    private String botDetailsTabsPartialView(BotDetailsViewState state) {
        return Util.readFileText("html/views/partialviews/botdetails/botdetails_tabs.ptv")
                .replace("{{info_selected}}", state.getActiveTab() == BotDetailsTab.Info ? "On" : "")
                .replace("{{combat_selected}}", state.getActiveTab() == BotDetailsTab.Combat ? "On" : "")
                .replace("{{skills_selected}}", state.getActiveTab() == BotDetailsTab.Skills ? "On" : "")
                .replace("{{social_selected}}", state.getActiveTab() == BotDetailsTab.Social ? "On" : "")
                .replace("{{chattab_selected}}", state.getActiveTab() == BotDetailsTab.Chat ? "On" : "");

    }

    public void loadCreateBot(L2PcInstance player) {
        CreateBotViewState state = viewStates.createBotViewState(player);
        Supplier<String> classTypes = () -> {
            List<String> classes = CreateBotDetails.classesForDropdown(state.getBotDetails().getRace());
            if (classes.contains(state.getBotDetails().getClassType())) {
                classes.remove(state.getBotDetails().getClassType());
                classes.add(0, state.getBotDetails().getClassType());
            }
            return String.join(";", classes);
        };
        Supplier<String> gendersSupplier = () -> {
            List<Sex> genders = CreateBotDetails.gendersForDropdown();
            if (genders.contains(state.getBotDetails().getGender())) {
                genders.remove(state.getBotDetails().getGender());
                genders.add(0, state.getBotDetails().getGender());
            }
            return genders.stream().map(Sex::name).collect(Collectors.joining(";"));
        };
        Supplier<String> facesSupplier = () -> {
            List<String> faces = CreateBotDetails.facesForDropdown();
            if (faces.contains(state.getBotDetails().getFace())) {
                faces.remove(state.getBotDetails().getFace());
                faces.add(0, state.getBotDetails().getFace());
            }
            return String.join(";", faces);
        };
        Supplier<String> hairColorsSupp = () -> {
            List<String> hairColors = CreateBotDetails.hairColorForDropdown();
            if (hairColors.contains(state.getBotDetails().getHairColor())) {
                hairColors.remove(state.getBotDetails().getHairColor());
                hairColors.add(0, state.getBotDetails().getHairColor());
            }
            return String.join(";", hairColors);
        };
        Supplier<String> hairStylesSupp = () -> {
            List<String> hairStyles = CreateBotDetails.hairstyleForDropdown();
            if (hairStyles.contains(state.getBotDetails().getHairStyle())) {
                hairStyles.remove(state.getBotDetails().getHairStyle());
                hairStyles.add(0, state.getBotDetails().getHairStyle());
            }
            return String.join(";", hairStyles);
        };
        Supplier<String> availableClassesSupp = () -> {
            List<String> classes = AutobotHelpers.getSupportedClassesForLevel(state.getBotDetails().getLevel())
                    .stream().filter(c -> c.getRace() == state.getBotDetails().getRace()).map(ClassId::name).collect(Collectors.toList());
            if (classes.contains(state.getBotDetails().getClassId().name())) {
                classes.remove(state.getBotDetails().getClassId().name());
                classes.add(0, state.getBotDetails().getClassId().name());
            }
            return String.join(";", classes);
        };
        var html = Util.readFileText("html/views/create_bot.htv")
                .replace("{{human_selected}}", state.getBotDetails().getRace() == Race.HUMAN ? "2" : "1")
                .replace("{{elf_selected}}", state.getBotDetails().getRace() == Race.ELF ? "2" : "1")
                .replace("{{delf_selected}}", state.getBotDetails().getRace() == Race.DARK_ELF ? "2" : "1")
                .replace("{{orc_selected}}", state.getBotDetails().getRace() == Race.ORC ? "2" : "1")
                .replace("{{dwarf_selected}}", state.getBotDetails().getRace() == Race.DWARF ? "2" : "1")
                .replace("{{botname}}", UiComponents.textbotComponent(UiComponents.CreateBotName, "Bot name", "crbtn", state.getBotDetails().getName(), state.getEditAction() == CreateBotViewState.CreateBotEditAction.EditingName))
                .replace("{{botlevel}}", UiComponents.textbotComponent(UiComponents.CreateBotLevel, "Bot level", "crbtl", state.getBotDetails().getLevel() + "", state.getEditAction() == CreateBotViewState.CreateBotEditAction.EditingLevel))
                .replace("{{weaponench}}", UiComponents.textbotComponent(UiComponents.CreateBotWeaponEnch, "Weapon enchant", "crbtw", state.getBotDetails().getWeaponEnchant() + "", state.getEditAction() == CreateBotViewState.CreateBotEditAction.EditingWeaponEnchant, 40))
                .replace("{{armorench}}", UiComponents.textbotComponent(UiComponents.CreateBotArmorEnch, "Armor enchant", "crbta", state.getBotDetails().getArmorEnchant() + "", state.getEditAction() == CreateBotViewState.CreateBotEditAction.EditingArmorEnchant, 40))
                .replace("{{jewelench}}", UiComponents.textbotComponent(UiComponents.CreateBotJewelEnch, "Jewel enchant", "crbtj", state.getBotDetails().getJewelEnchant() + "", state.getEditAction() == CreateBotViewState.CreateBotEditAction.EditingJewelsEnchant, 40))
                .replace("{{classTypes}}", classTypes.get())
                .replace("{{genders}}", gendersSupplier.get())
                .replace("{{faces}}", facesSupplier.get())
                .replace("{{haircolors}}", hairColorsSupp.get())
                .replace("{{hairstyles}}", hairStylesSupp.get())
                .replace("{{availableClasses}}", availableClassesSupp.get());
        CommunityBoardHandler.separateAndSend(html, player);
    }

    private String botDetailsTopButtonsPartialView(BotDetailsViewState state) {
        if (state.getActiveBot().isInGame()) {
            return Util.readFileText("html/views/partialviews/botdetails/botdetails_online.ptv")
                    .replace("{{spawn_command}}", "des " + state.getActiveBot().getName())
                    .replace("{{spawn_text}}", "Despawn");
        }
        return Util.readFileText("html/views/partialviews/botdetails/botdetails_offline.ptv")
                .replace("{{spawn_command}}", "lm " + state.getActiveBot().getName())
                .replace("{{spawn_text}}", "Spawn on me");
    }

    private String contentPartialView(L2PcInstance player, BotDetailsViewState state) {
        return switch (state.getActiveTab()) {
            case Info -> partialBotDetailsInfo(state);
            case Combat -> partialBotDetailCombatTab(state);
            case Skills -> partialBotDetailsSkillsTab(state);
            case Social -> partialBotDetailsSocialTab(player, state);
            case Chat -> partialBotDetailsChatTab(state);
        };
    }


    private String partialBotDetailsChatTab(BotDetailsViewState state) {
        return Util.readFileText("html/views/partialviews/botdetails/chat/botdetails_chat.ptv")
                .replace("{{chat}}", chatContentPartialView(state))
                .replace("{{chat_input}}", chatMultiEditPartialView(state))
                .replace("{{botdetails_chat_tabs.ptv}}", chatTabsPartialView(state));
    }

    private String chatTabsPartialView(BotDetailsViewState state) {
        return Util.readFileText("html/views/partialviews/botdetails/chat/botdetails_chat_tabs.ptv")
                .replace("{{alltabactive}}", state.getChatTab() == BotDetailsChatTab.All ? "siege_tab1" : "siege_tab3")
                .replace("{{pmtabactive}}", state.getChatTab() == BotDetailsChatTab.Pms ? "siege_tab1" : "siege_tab3");
    }

    private String chatMultiEditPartialView(BotDetailsViewState state) {
        if (!state.getActiveBot().isOnline()) {
            return "";
        }
        return "<tr><td><MultiEdit var=\"Message\" width=540 height=20></td><td><button action=\"bypass admin_a b csend " + state.getActiveBot().getName() + "$Message\" value=\"Send\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>";
    }

    private String chatContentPartialView(BotDetailsViewState state) {
        if (!state.getActiveBot().isInGame()) {
            return "Bot is offline";
        }

        List<BotChat> chat = switch (state.getChatTab()) {
            case All -> state.getActiveBot().getChatMessages();
            case Pms -> state.getActiveBot().getChatMessages().stream().filter(it -> it.getChatType() == ChatType.PmSent || it.getChatType() == ChatType.PmReceived).collect(Collectors.toList());
        };

        String chatText = chat.subList(Math.max(chat.size() - chatPageSize, 0), chat.size()).stream().map(c -> switch (c.getChatType()) {
            case PmReceived -> "<font color=\"FF00FF\">" + c.getSenderName() + ":" + c.getMessage() + "</font>";
            case PmSent -> "<font color=\"FF00FF\">->" + c.getSenderName() + ": " + c.getMessage() + "</font>";
            case Shout -> "<font color=\"FF7000\">" + c.getSenderName() + ": " + c.getMessage() + "</font>";
            default -> c.getSenderName() + ": " + c.getMessage();
        }).collect(Collectors.joining("<br1>"));
        return chatText.isEmpty() ? "no chat" : chatText;
    }

    private String renderStoreList(BotDetailsViewState state, String cmd) {
        if (cmd.equals("s") && state.getSellList().isEmpty()) {
            return "<tr><td></td></tr>";
        }

        if (cmd.equals("b") && state.getBuyList().isEmpty()) {
            return "<tr><td></td></tr>";
        }

        int index = 0;
        StringBuilder sb = new StringBuilder();

        for (BotDetailsViewState.StoreList storeItem : (cmd.equals("s") ? state.getSellList() : state.getBuyList())) {
            L2Item item = ItemTable.getInstance().getTemplate(storeItem.first);
            if (item == null) continue;
            sb.append(Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social_create_store_item.ptv")
                    .replace("{{itemicon}}", IconsTable.getInstance().getItemIcon(storeItem.first))
                    .replace("{{itemname}}", item.getName())
                    .replace("{{itemcount}}", storeItem.second + "")
                    .replace("{{itemcost}}", storeItem.third + "")
                    .replace("{{index}}", index + "")
                    .replace("{{buysell}}", cmd)
            );
            index++;
        }

        return sb.toString();
    }

    private String renderCraftStoreList(BotDetailsViewState state) {
        if (state.getCraftList().isEmpty()) {
            return "<tr><td></td></tr>";
        }

        var index = 0;
        StringBuilder sb = new StringBuilder();
        for (BotDetailsViewState.CraftList craftItem : state.getCraftList()) {

            L2Item item = ItemTable.getInstance().getTemplate(craftItem.recipe.getItemId());
            if (item == null) continue;
            sb.append(Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social_create_craft_item.ptv")
                    .replace("{{itemicon}}", IconsTable.getInstance().getItemIcon(item.getId()))
                    .replace("{{itemname}}", item.getName())
                    .replace("{{itemcost}}", craftItem.price + "")
                    .replace("{{index}}", index + "")
            );
            index++;
        }

        return sb.toString();
    }

    private String renderCreateButtons(String command, BotDetailsViewState state) {
        StringBuilder sb = new StringBuilder("<tr>");

        if ((command.equals("s c") && !state.getSellList().isEmpty()) || (command.equals("b c") && !state.getBuyList().isEmpty()) || (command.equals("c c") && !state.getCraftList().isEmpty()) && state.getActiveBot().getPrivateStoreType() == PrivateStoreType.NONE) {
            sb.append("<td width=\"90\" align=\"center\"><button action=\"bypass admin_a b t bs").append(command).append("\" value=\"Create store\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
        }

        if (state.getActiveBot().getPrivateStoreType() != PrivateStoreType.NONE) {
            sb.append("<td width=\"90\" align=\"center\"><button action=\"bypass admin_a b t bs stop\" value=\"Stop store\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td>");
        }

        sb.append("</tr>");
        return sb.toString();
    }

    public String partialBotDetailsSocialTab(L2PcInstance player, BotDetailsViewState state) {
        Autobot controlledBot = AutobotHelpers.getControllingBot(player);
        return switch (state.getSocialPage()) {
            case Home -> Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social.ptv")
                    .replace("{{ctrlbotcmd}}", (controlledBot != null && controlledBot.getObjectId() == state.getActiveBot().getObjectId()) ? "uncont" : "cont")
                    .replace("{{ctrlbotimg}}", (controlledBot != null && controlledBot.getObjectId() == state.getActiveBot().getObjectId()) ? "_over" : "");
            case CreateBuyStore -> Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social_create_store.ptv")
                    .replace("{{createstore}}", renderCreateButtons("s c", state))
                    .replace("{{additembtn}}", state.getActiveBot().getPrivateStoreType() != PrivateStoreType.NONE ? "<tr></tr>" : "<tr><td width=\"90\" align=\"center\"><button action=\"bypass admin_a b t bs s a $itemid $itemcount $priceperitem\" value=\"Add item\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>")
                    .replace("{{message_textbox}}", UiComponents.textbotComponent(UiComponents.PrivateSellMessage, "Message", "psmsg", state.getSellMessage(), state.getSocialEditAction() == BotDetailsViewState.BotDetailsSocialEditAction.SellMessage, 75, 200, false, true, HtmlAlignment.Right))
                    .replace("{{botdetails_social_create_store_item.ptv}}", renderStoreList(state, "s"));
            case CreateSellStore -> Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social_create_store.ptv")
                    .replace("{{createstore}}", renderCreateButtons("b c", state))
                    .replace("{{additembtn}}", state.getActiveBot().getPrivateStoreType() != PrivateStoreType.NONE ? "<tr></tr>" : "<tr><td width=\"90\" align=\"center\"><button action=\"bypass admin_a b t bs b a $itemid $itemcount  $priceperitem\" value=\"Add item\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>")
                    .replace("{{message_textbox}}", UiComponents.textbotComponent(UiComponents.PrivateBuyMessage, "Message", "pbmsg", state.getBuyMessage(), state.getSocialEditAction() == BotDetailsViewState.BotDetailsSocialEditAction.BuyMessage, 75, 200, false, true, HtmlAlignment.Right))
                    .replace("{{botdetails_social_create_store_item.ptv}}", renderStoreList(state, "b"));
            case CreateCraftStore -> Util.readFileText("html/views/partialviews/botdetails/social/botdetails_social_create_craft.ptv")
                    .replace("{{createstore}}", renderCreateButtons("c c", state))
                    .replace("{{additembtn}}", state.getActiveBot().getPrivateStoreType() != PrivateStoreType.NONE ? "<tr></tr>" : "<tr><td width=\"90\" align=\"center\"><button action=\"bypass admin_a b t bs c a $idtype $id $cost\" value=\"Add item\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"></td></tr>")
                    .replace("{{message_textbox}}", UiComponents.textbotComponent(UiComponents.PrivateCraftMessage, "Message", "pcmsg", state.getCraftMessage(), state.getSocialEditAction() == BotDetailsViewState.BotDetailsSocialEditAction.CraftMessage, 75, 200, false, true, HtmlAlignment.Right))
                    .replace("{{botdetails_social_create_craft_item.ptv}}", renderCraftStoreList(state));
        };
    }

    private String partialBotDetailsSkillsTab(BotDetailsViewState state) {

        List<Integer> remainingSkills = state.getActiveBot().getCombatBehavior().getConditionalSkills().stream().filter(c -> state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.stream().noneMatch(skill -> skill.getSkillId() == c)).collect(Collectors.toList());
        Supplier<String> availableValues = () -> Util.readFileText("html/views/partialviews/botdetails/skills/botdetails_skills_addcondition.ptv")
                .replace("{{availableskills}}", remainingSkills.stream().map(s -> Objects.requireNonNull(SkillData.getInstance().getSkill(s, 1)).getName()).collect(Collectors.joining(";")))
                .replace("{{availabletargets}}", Arrays.stream(TargetCondition.values()).map(TargetCondition::name).collect(Collectors.joining(";")))
                .replace("{{availablestatuses}}", Arrays.stream(StatusCondition.values()).map(StatusCondition::name).collect(Collectors.joining(";")))
                .replace("{{availableconditions}}", Arrays.stream(ComparisonCondition.values()).map(ComparisonCondition::getOperation).collect(Collectors.joining(";")))
                .replace("{{availablecondvaluetypes}}", Arrays.stream(ConditionValueType.values()).map(ConditionValueType::name).collect(Collectors.joining(";")));

        return Util.readFileText("html/views/partialviews/botdetails/skills/botdetails_skills.ptv")
                .replace("{{existingconditions}}", renderExistingConditions(state))
                .replace("{{addcondition}}", remainingSkills.isEmpty() ? "<br><center>No skills left to configure</center>" : availableValues.get());
    }

    private String renderExistingConditions(BotDetailsViewState state) {
        if (!state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.isEmpty() && !state.getActiveBot().getCombatBehavior().getSkillPreferences().togglableSkills.isEmpty()) {
            return "<tr><td>No skill conditions</td></tr>";
        }

        StringBuilder sb = new StringBuilder();
        state.getActiveBot().getCombatBehavior().getSkillPreferences().togglableSkills.forEach((key, value) -> {
            Skill skill = SkillData.getInstance().getSkill(key, 1);
            if (skill != null) {
                sb.append(Util.readFileText("html/views/partialviews/botdetails/skills/botdetails_skills_condition_toggle.ptv")
                        .replace("{{skillname}}", skill.getName())
                        .replace("{{skillid}}", skill.getId() + "")
                        .replace("{{skillicon}}", IconsTable.getInstance().getSkillIcon(skill.getId()))
                        .replace("{{checked}}", value ? "_checked" : ""));
            }

        });
        state.getActiveBot().getCombatBehavior().getSkillPreferences().skillUsageConditions.forEach(s -> {
            Skill skill = SkillData.getInstance().getSkill(s.getSkillId(), 1);
            if (skill != null) {

                if (state.getSkillUnderEdit() == s.getSkillId()) {
                    sb.append(Util.readFileText("html/views/partialviews/botdetails/skills/botdetails_skills_condition_edit.ptv")
                            .replace("{{skillname}}", skill.getName())
                            .replace("{{skillid}}", skill.getId() + "")
                            .replace("{{skillicon}}", IconsTable.getInstance().getSkillIcon(skill.getId()))
                            .replace("{{edittargets}}", Arrays.stream(TargetCondition.values()).map(TargetCondition::name).collect(Collectors.joining(";")))
                            .replace("{{editstatuses}}", Arrays.stream(StatusCondition.values()).map(StatusCondition::name).collect(Collectors.joining(";")))
                            .replace("{{editconditions}}", Arrays.stream(ComparisonCondition.values()).map(ComparisonCondition::getOperation).collect(Collectors.joining(";")))
                            .replace("{{editcondvaluetypes}}", Arrays.stream(ConditionValueType.values()).map(ConditionValueType::name).collect(Collectors.joining(";"))));
                } else {
                    sb.append(Util.readFileText("html/views/partialviews/botdetails/skills/botdetails_skills_condition.ptv")
                            .replace("{{skillname}}", skill.getName())
                            .replace("{{skillid}}", skill.getId() + "")
                            .replace("{{skillicon}}", IconsTable.getInstance().getSkillIcon(skill.getId()))
                            .replace("{{conditiontext}}", s.getConditionText()));
                }
            }
        });
        return sb.toString();
    }

    private String partialBotDetailCombatTab(BotDetailsViewState state) {
        return Util.readFileText("html/views/partialviews/botdetails/combat/botdetails_combat.ptv")
                .replace("{{radiustextbox}}", UiComponents.textbotComponent(UiComponents.TargetRadius, "Target radius", "TargetRadius", state.getActiveBot().getCombatBehavior().getCombatPreferences().getTargetingRadius() + "", state.getCombatEditAction() == BotDetailsCombatEditAction.TargetRadius))
                .replace("{{targprefcombobox}}", UiComponents.comboboxComponent(UiComponents.TargetPref, "Target preference", "TargetPref", state.getActiveBot().getCombatBehavior().getCombatPreferences().getTargetingPreference().name(), Arrays.stream(TargetingPreference.values()).map(Enum::name).collect(Collectors.toList())))
                .replace("{{atkplayertypecombobox}}", UiComponents.comboboxComponent(UiComponents.AttackPlayerTypeUi, "Attack player type", "AtkPlrType", state.getActiveBot().getCombatBehavior().getCombatPreferences().getAttackPlayerType().name(), Arrays.stream(AttackPlayerType.values()).map(Enum::name).collect(Collectors.toList())))
                .replace("{{classspecific}}", classSpecificCombatPartialView(state))
                .replace("{{potions_cp}}", UiComponents.checkboxComponent(UiComponents.UseCpPots, "Use CP pots", state.getActiveBot().getCombatBehavior().getCombatPreferences().getUseGreaterCpPots()))
                .replace("{{potions_qhp}}", UiComponents.checkboxComponent(UiComponents.UseQhPots, "Use QH pots", state.getActiveBot().getCombatBehavior().getCombatPreferences().getUseQuickHealingPots()))
                .replace("{{potions_ghp}}", UiComponents.checkboxComponent(UiComponents.UseGhPots, "Use GH pots", state.getActiveBot().getCombatBehavior().getCombatPreferences().getUseGreaterHealingPots()));

    }

    private String classSpecificCombatPartialView(BotDetailsViewState state) {
        StringBuilder sb = new StringBuilder();
        if (state.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof ArcherCombatPreferences) {
            sb.append(UiComponents.checkboxComponent(UiComponents.IsKiting, "Is kiting", ((ArcherCombatPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).isKiting()));
            sb.append(UiComponents.textbotComponent(UiComponents.KiteRadius, "Kite radius", "KiteRadius", ((ArcherCombatPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).getKiteRadius() + "", state.getCombatEditAction() == BotDetailsCombatEditAction.KiteRadius));
        }

        if (state.getActiveBot().getCombatBehaviorForClass().getCombatPreferences() instanceof PetOwnerPreferences) {
            sb.append(UiComponents.checkboxComponent(UiComponents.SummonsPet, "Summons pet", ((PetOwnerPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).getSummonPet()));
            sb.append(UiComponents.checkboxComponent(UiComponents.PetAssists, "Pet assists", ((PetOwnerPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).getPetAssists()));
            sb.append(UiComponents.checkboxComponent(UiComponents.PetUsesShots, "Pet uses shots", ((PetOwnerPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).getPetUsesShots()));
            sb.append(UiComponents.checkboxComponent(UiComponents.PetHasBuffs, "Pet has buffs", ((PetOwnerPreferences) state.getActiveBot().getCombatBehavior().getCombatPreferences()).getPetHasBuffs()));
        }

        if (state.getActiveBot().getCombatBehavior().getCombatPreferences() instanceof DuelistCombatPreferences) {
            sb.append(UiComponents.checkboxComponent(UiComponents.UseSkillsOnMobs, "Use skills on mobs", ((DuelistSkillPreferences) state.getActiveBot().getCombatBehavior().getSkillPreferences()).isUseSkillsOnMobs()));
        }

        return sb.toString();
    }

    private String partialBotDetailsInfo(BotDetailsViewState state) {
        Supplier<String> isOnline = () -> {
            long millis = System.currentTimeMillis() - state.getActiveBot().getSpawnTime();
            return String.format("%d hour(s) %d min(s), %d sec(s)",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        };
        return Util.readFileText("html/views/partialviews/botdetails/info/botdetails_info.ptv")
                .replace("{{botname}}", state.getActiveBot().getName())
                .replace("{{level}}", state.getActiveBot().getLevel() + "")
                .replace("{{currentcp}}", state.getActiveBot().getCurrentCp() + "")
                .replace("{{maxcp}}", state.getActiveBot().getMaxCp() + "")
                .replace("{{currenthp}}", state.getActiveBot().getCurrentHp() + "")
                .replace("{{maxhp}}", state.getActiveBot().getMaxHp() + "")
                .replace("{{currentmp}}", state.getActiveBot().getCurrentMp() + "")
                .replace("{{maxmp}}", state.getActiveBot().getMaxMp() + "")
                .replace("{{location}}", MapRegionManager.getInstance().getClosestTownName(state.getActiveBot()))
                .replace("{{coordinates}}", "X: " + state.getActiveBot().getX() + "Y: " + state.getActiveBot().getY() + "Z: " + state.getActiveBot().getZ())
                .replace("{{onlinetime}}", state.getActiveBot().isInGame() ? isOnline.get() : "N/A")
                .replace("{{botactivity}}", partialBotDetailsActivity(state));
    }

    private String partialBotDetailsActivity(BotDetailsViewState state) {
        ActivityPreferences activityPreferences = state.getActiveBot().getCombatBehavior().getActivityPreferences();
        Supplier<String> loginHours = () -> {
            List<String> hours = new ArrayList<>(Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"));
            hours.remove(state.getActiveBot().getCombatBehavior().getActivityPreferences().getLoginTime().split(":")[0]);
            hours.add(0, state.getActiveBot().getCombatBehavior().getActivityPreferences().getLoginTime().split(":")[0]);
            return String.join(";", hours);
        };
        Supplier<String> loginMinutes = () -> {
            List<String> minutes = new ArrayList<>(Arrays.asList("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"));
            minutes.remove(state.getActiveBot().getCombatBehavior().getActivityPreferences().getLoginTime().split(":")[1]);
            minutes.add(0, state.getActiveBot().getCombatBehavior().getActivityPreferences().getLoginTime().split(":")[1]);
            return String.join(";", minutes);
        };
        Supplier<String> logoutHours = () -> {
            List<String> hours = new ArrayList<>(Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"));
            hours.remove(state.getActiveBot().getCombatBehavior().getActivityPreferences().getLogoutTime().split(":")[0]);
            hours.add(0, state.getActiveBot().getCombatBehavior().getActivityPreferences().getLogoutTime().split(":")[0]);
            return String.join(";", hours);
        };
        Supplier<String> logutMinutes = () -> {
            List<String> minutes = new ArrayList<>(Arrays.asList("00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"));
            minutes.remove(state.getActiveBot().getCombatBehavior().getActivityPreferences().getLogoutTime().split(":")[1]);
            minutes.add(0, state.getActiveBot().getCombatBehavior().getActivityPreferences().getLogoutTime().split(":")[1]);
            return String.join(";", minutes);
        };
        Supplier<String> activityOptions = () -> switch (state.getActivityEditAction()) {
            case None -> Util.readFileText("html/views/partialviews/botdetails/info/botdetails_activity_none.ptv")
                    .replace("{{isactivechk}}", UiComponents.checkboxComponent(UiComponents.ActivityNoneActive, "Is Active", activityPreferences.getActivityType() == ActivityPreferences.ActivityType.None, 50));
            case Uptime, EditUptime -> Util.readFileText("html/views/partialviews/botdetails/info/botdetails_activity_uptime.ptv")
                    .replace("{{uptimetextbox}}", UiComponents.textbotComponent(UiComponents.EditUptime, "Uptime in minutes", "UptimeMins", state.getActiveBot().getCombatBehavior().getActivityPreferences().getUptimeMinutes() + "", state.getActivityEditAction() == BotDetailsViewState.ActivityEditAction.EditUptime, 50, true))
                    .replace("{{isactivechk}}", UiComponents.checkboxComponent(UiComponents.ActivityUptimeActive, "Is Active", activityPreferences.getActivityType() == ActivityPreferences.ActivityType.Uptime, 50));
            case Schedule -> Util.readFileText("html/views/partialviews/botdetails/info/botdetails_activity_schedule.ptv")
                    .replace("{{currenttime}}", LocalTime.now(Clock.systemUTC()).format(DateTimeFormatter.ofPattern("HH:mm")))
                    .replace("{{isactivechk}}", UiComponents.checkboxComponent(UiComponents.ActivityScheduleActive, "Is Active", activityPreferences.getActivityType() == ActivityPreferences.ActivityType.Schedule, 50))
                    .replace("{{loginhours}}", loginHours.get())
                    .replace("{{loginminutes}}", loginMinutes.get())
                    .replace("{{logouthours}}", logoutHours.get())
                    .replace("{{logoutminutes}}", logutMinutes.get());
        };
        return Util.readFileText("html/views/partialviews/botdetails/info/botdetails_activity.ptv")
                .replace("{{noneselected}}", state.getActivityEditAction() == BotDetailsViewState.ActivityEditAction.None ? "2" : "1")
                .replace("{{uptimeselected}}", (state.getActivityEditAction() == BotDetailsViewState.ActivityEditAction.Uptime || state.getActivityEditAction() == BotDetailsViewState.ActivityEditAction.EditUptime) ? "2" : "1")
                .replace("{{scheduleselected}}", state.getActivityEditAction() == BotDetailsViewState.ActivityEditAction.Schedule ? "2" : "1")
                .replace("{{activityoptions}}", activityOptions.get());
    }

    private int totalPageCount(String nameSearch, int pageSize) {
        return AutobotsDao.getInstance().getTotalBotCount(nameSearch) / pageSize;
    }

    public static AutobotsUi getInstance() {
        return AutobotsUi.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotsUi INSTANCE = new AutobotsUi();
    }
}
