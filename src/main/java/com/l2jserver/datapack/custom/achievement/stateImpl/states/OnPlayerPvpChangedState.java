package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPChanged;

public class OnPlayerPvpChangedState extends State<Integer> {

    public OnPlayerPvpChangedState(Integer start, Integer end, Integer current, EventType eventType) {
        super(start, end, current, eventType);
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnPlayerPvPChanged && !isDone) {
            OnPlayerPvPChanged onPlayerPvpChangedEvent = (OnPlayerPvPChanged) event;
            current += (onPlayerPvpChangedEvent.getNewPoints() - onPlayerPvpChangedEvent.getOldPoints());
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }
        return isDone;
    }
}
