package com.l2jserver.datapack.autobots.ui.states;

import com.l2jserver.datapack.autobots.models.CreateBotDetails;

public class CreateBotViewState implements ViewState {


    private CreateBotDetails botDetails = new CreateBotDetails();
    private boolean isActive = false;
    private CreateBotEditAction editAction = CreateBotEditAction.None;

    public CreateBotViewState() {

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
        this.botDetails = new CreateBotDetails();
    }

    public CreateBotDetails getBotDetails() {
        return botDetails;
    }

    public CreateBotEditAction getEditAction() {
        return editAction;
    }

    public void setEditAction(CreateBotEditAction createBotEditAction) {
        this.editAction = createBotEditAction;
    }

    public enum CreateBotEditAction {
        None,
        EditingName,
        EditingLevel,
        EditingWeaponEnchant,
        EditingArmorEnchant,
        EditingJewelsEnchant
    }
}
