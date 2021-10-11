package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.enums.TrapAction;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.trap.OnTrapAction;

public class OnTrapActionState extends State<Integer> {

    public OnTrapActionState(Integer start, Integer end, Integer current, EventType eventType) {
        super(start, end, current, eventType);
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }

        if (event instanceof OnTrapAction && !isDone) {
            OnTrapAction onTrapEvent = (OnTrapAction) event;

            if (onTrapEvent.getAction() != TrapAction.TRAP_TRIGGERED) {
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
