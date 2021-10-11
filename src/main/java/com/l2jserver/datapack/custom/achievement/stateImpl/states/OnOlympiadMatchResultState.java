package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;

public class OnOlympiadMatchResultState extends State<Integer> {

    public OnOlympiadMatchResultState(Integer start, Integer end, Integer current, EventType eventType) {
        super(start, end, current, eventType);
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnOlympiadMatchResult && !isDone) {
            OnOlympiadMatchResult onOlympiadMatchResultEvent = (OnOlympiadMatchResult) event;
            if (onOlympiadMatchResultEvent.getWinner() == null) {
                return false;
            }
            current++;
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }
        return isDone;
    }

}
