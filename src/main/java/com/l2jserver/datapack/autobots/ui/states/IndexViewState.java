package com.l2jserver.datapack.autobots.ui.states;

import com.l2jserver.datapack.autobots.models.AutobotInfo;
import com.l2jserver.datapack.autobots.ui.IndexBotOrdering;
import com.l2jserver.datapack.autobots.ui.tabs.IndexTab;

import java.util.HashMap;
import java.util.Map;

public class IndexViewState implements ViewState {
    private String nameToSearch;
    private Pagination pagination;
    private final Map<String, AutobotInfo> selectedBots;
    private IndexTab indexTab;
    private IndexBotOrdering botOrdering;
    private boolean isActive = true;
    public IndexViewState() {
        this("", new Pagination(1, 10), new HashMap<>(), IndexTab.General, IndexBotOrdering.None);
    }

    public IndexViewState(String nameToSearch, Pagination pagination, Map<String, AutobotInfo> selectedBots, IndexTab indexTab, IndexBotOrdering botOrdering) {
        this.nameToSearch = nameToSearch;
        this.pagination = pagination;
        this.selectedBots = selectedBots;
        this.indexTab = indexTab;
        this.botOrdering = botOrdering;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
    @Override
    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
    @Override
    public void reset() {
        nameToSearch = "";
        pagination = new Pagination(1, 10);
        selectedBots.clear();
        indexTab = IndexTab.General;
        botOrdering = IndexBotOrdering.None;
    }

    public static class Pagination {
        public int first;
        public int second;

        public Pagination(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    public String getNameToSearch() {
        return nameToSearch;
    }
    public void setNameToSearch(String name){
        this.nameToSearch = name;
    }

    public Pagination getPagination() {
        return pagination;
    }
    public void setPagination(Pagination pagination){
        this.pagination = pagination;
    }

    public Map<String, AutobotInfo> getSelectedBots() {
        return selectedBots;
    }

    public IndexTab getIndexTab() {
        return indexTab;
    }
    public void setIndexTab(IndexTab indexTab){
        this.indexTab= indexTab;
    }

    public IndexBotOrdering getBotOrdering() {
        return botOrdering;
    }
    public void setBotOrdering (IndexBotOrdering botOrdering){
        this.botOrdering = botOrdering;
    }

}

