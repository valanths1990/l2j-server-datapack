package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerKarmaChanged;

public class OnPlayerKarmaChangedState extends State<Integer> {

    public OnPlayerKarmaChangedState(Integer start, Integer end, Integer current, EventType event) {
        super(start, end, current, event);
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnPlayerKarmaChanged && !isDone) {
            OnPlayerKarmaChanged karmaEvent = (OnPlayerKarmaChanged) event;
            current += (karmaEvent.getNewKarma() - karmaEvent.getOldKarma());
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }
        return isDone;
    }

}
