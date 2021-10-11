package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerFameChanged;

public class OnPlayerFameChangedState extends State<Long> {

    public OnPlayerFameChangedState(Long start, Long end, Long current, EventType event) {
        super(start, end, current, event);
    }

    @Override
    public void increaseCurrent(Double current) {
        this.current = current.longValue();
        if (this.current >= end) {
            this.current = this.end;
            isDone = true;
        }
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnPlayerFameChanged && !isDone) {
            OnPlayerFameChanged fameChangedEvent = (OnPlayerFameChanged) event;
            current += (fameChangedEvent.getNewFame() - fameChangedEvent.getOldFame());
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }
        return isDone;
    }

}
