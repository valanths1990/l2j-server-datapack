package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnLogInEvent extends ListenerEvent {

    private final Player mPlayer;

    public OnLogInEvent(Player player) {
        mPlayer = player;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public ListenerType getType() {
        return ListenerType.ON_LOG_IN;
    }
}
