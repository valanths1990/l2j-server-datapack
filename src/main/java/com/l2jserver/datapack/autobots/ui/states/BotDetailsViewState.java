package com.l2jserver.datapack.autobots.ui.states;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsChatTab;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsCombatEditAction;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsSocialPage;
import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsTab;
import com.l2jserver.gameserver.model.L2RecipeInstance;

import java.util.ArrayList;
import java.util.List;

public class BotDetailsViewState implements ViewState {
    private Autobot activeBot;
    private BotDetailsTab activeTab = BotDetailsTab.Info;
    private BotDetailsChatTab chatTab = BotDetailsChatTab.All;
    private ActivityEditAction activityEditAction = ActivityEditAction.None;
    private BotDetailsCombatEditAction combatEditAction = BotDetailsCombatEditAction.None;
    private int skillUnderEdit = 0;
    private BotDetailsSocialPage socialPage = BotDetailsSocialPage.Home;
    private List<StoreList> sellList = new ArrayList<>();
    private String sellMessage = "";
    private List<StoreList> buyList = new ArrayList<>();
    private String buyMessage = "";
    private List<CraftList> craftList = new ArrayList<>();
    private String craftMessage = "";
    private BotDetailsSocialEditAction socialEditAction = BotDetailsSocialEditAction.None;
    private boolean isActive = false;

    public BotDetailsViewState(Autobot activeBot,boolean isActive,ActivityEditAction activityEditAction){
        this.activeBot = activeBot;
        this.isActive = isActive;
        this.activityEditAction = activityEditAction;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public void reset() {
        combatEditAction = BotDetailsCombatEditAction.None;
        activityEditAction = ActivityEditAction.None;
        socialPage = BotDetailsSocialPage.Home;
        skillUnderEdit = 0;
        sellList.clear();
        sellMessage = "";
        buyList.clear();
        buyMessage = "";
        craftList.clear();
        craftMessage = "";
    }

    public Autobot getActiveBot() {
        return activeBot;
    }

    public BotDetailsTab getActiveTab() {
        return activeTab;
    }
    public BotDetailsChatTab getChatTab() {
        return chatTab;
    }

    public ActivityEditAction getActivityEditAction() {
        return activityEditAction;
    }

    public BotDetailsCombatEditAction getCombatEditAction() {
        return combatEditAction;
    }

    public int getSkillUnderEdit() {
        return skillUnderEdit;
    }

    public BotDetailsSocialPage getSocialPage() {
        return socialPage;
    }

    public List<StoreList> getSellList() {
        return sellList;
    }

    public String getSellMessage() {
        return sellMessage;
    }

    public List<StoreList> getBuyList() {
        return buyList;
    }

    public String getBuyMessage() {
        return buyMessage;
    }

    public List<CraftList> getCraftList() {
        return craftList;
    }

    public String getCraftMessage() {
        return craftMessage;
    }

    public BotDetailsSocialEditAction getSocialEditAction() {
        return socialEditAction;
    }

    public void setActiveBot(Autobot activeBot) {
        this.activeBot = activeBot;
    }

    public void setActiveTab(BotDetailsTab activeTab) {
        this.activeTab = activeTab;
    }

    public void setChatTab(BotDetailsChatTab chatTab) {
        this.chatTab = chatTab;
    }

    public void setActivityEditAction(ActivityEditAction activityEditAction) {
        this.activityEditAction = activityEditAction;
    }

    public void setCombatEditAction(BotDetailsCombatEditAction combatEditAction) {
        this.combatEditAction = combatEditAction;
    }

    public void setSkillUnderEdit(int skillUnderEdit) {
        this.skillUnderEdit = skillUnderEdit;
    }

    public void setSocialPage(BotDetailsSocialPage socialPage) {
        this.socialPage = socialPage;
    }

    public void setSellList(List<StoreList> sellList) {
        this.sellList = sellList;
    }

    public void setSellMessage(String sellMessage) {
        this.sellMessage = sellMessage;
    }

    public void setBuyList(List<StoreList> buyList) {
        this.buyList = buyList;
    }

    public void setBuyMessage(String buyMessage) {
        this.buyMessage = buyMessage;
    }

    public void setCraftList(List<CraftList> craftList) {
        this.craftList = craftList;
    }

    public void setCraftMessage(String craftMessage) {
        this.craftMessage = craftMessage;
    }

    public void setSocialEditAction(BotDetailsSocialEditAction socialEditAction) {
        this.socialEditAction = socialEditAction;
    }

    public enum ActivityEditAction {
        None,
        Uptime,
        EditUptime,
        Schedule
    }

    public enum BotDetailsSocialEditAction {
        None,
        SellMessage,
        BuyMessage,
        CraftMessage
    }

    public static class StoreList {
       public int first;
       public int second;
       public int third;

        public StoreList(int first, int second, int third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }
    }

    public static class CraftList {
       public L2RecipeInstance recipe;
       public int price;

        public CraftList(L2RecipeInstance recipe, int price) {
            this.recipe = recipe;
            this.price = price;
        }
    }
}
