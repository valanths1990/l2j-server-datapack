package com.l2jserver.datapack.autobots.ui.states;

public class SettingsViewState implements ViewState {
    private final SettingsTab activeTab = SettingsTab.Home;
    private SettingsEditAction editAction = SettingsEditAction.None;
    private boolean isActive = true;

    public SettingsViewState() {

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


    }

    public SettingsTab getActiveTab() {
        return activeTab;
    }

    public SettingsEditAction getEditAction() {
        return editAction;
    }

    public void setEditAction(SettingsEditAction settingsEditAction) {
        this.editAction = settingsEditAction;
    }

    public enum SettingsTab {
        Home, Combat
    }

    public enum SettingsEditAction {
        None,
        ThinkIteration,
        DefaultTitle,
        TargetingRange
    }
}
