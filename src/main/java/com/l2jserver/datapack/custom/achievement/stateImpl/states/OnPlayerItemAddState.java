package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.inventory.OnPlayerItemAdd;

public class OnPlayerItemAddState extends State<Integer> {
    private final int id;

    public OnPlayerItemAddState(Integer start, Integer end, Integer current, EventType eventType, int id) {
        super(start, end, current, eventType);
        this.id = id;
    }

    @Override
    public boolean transit(IBaseEvent event) {

        if (event == null) {
            return false;
        }

        if (event instanceof OnPlayerItemAdd && !isDone) {
            OnPlayerItemAdd onPlayerAddItem = (OnPlayerItemAdd) event;
            if (onPlayerAddItem.getItem() == null) {
                return false;
            }
            if (id == 0 || onPlayerAddItem.getItem().getId() == id) {
                current++;
            }
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }

        return isDone;
    }

}
