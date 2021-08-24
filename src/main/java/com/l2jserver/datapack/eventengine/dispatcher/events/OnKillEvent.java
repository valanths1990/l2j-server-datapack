package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Character;
import com.l2jserver.datapack.eventengine.model.entity.Playable;

public class OnKillEvent extends ListenerEvent {

    private final Playable mAttacker;
    private final Character mTarget;

    public OnKillEvent(Playable attacker, Character target) {
        mAttacker = attacker;
        mTarget = target;
    }

    public Playable getAttacker() {
        return mAttacker;
    }

    public Character getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_KILL;
    }
}
