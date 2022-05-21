package com.l2jserver.datapack.autobots.ui.states;

import com.l2jserver.datapack.autobots.ui.tabs.BotDetailsTab;

public interface ViewState {
    boolean isActive();
    void setIsActive(boolean isActive);
    void reset();

}
