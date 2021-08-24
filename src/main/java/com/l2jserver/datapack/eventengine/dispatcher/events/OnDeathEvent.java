package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnDeathEvent extends ListenerEvent {

    private final Player mTarget;

    public OnDeathEvent(Player target) {
        mTarget = target;
    }

    public Player getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_DEATH;
    }
}
