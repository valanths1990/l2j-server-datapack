package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.inventory.OnPlayerItemPickup;

public class OnPlayerItemPickUpState extends State<Integer> {
    private final int id;

    public OnPlayerItemPickUpState(Integer start, Integer end, Integer current, EventType eventType, int id) {
        super(start, end, current, eventType);
        this.id = id;
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnPlayerItemPickup && !isDone) {
            OnPlayerItemPickup onPlayeItemPickUp = (OnPlayerItemPickup) event;
            if (onPlayeItemPickUp.getItem() == null) {
                return false;
            }
            if (id == 0 || onPlayeItemPickUp.getItem().getId() == id) {
                current++;
            }
            if (current >= end) {
                current = end;
                isDone = true;
            }
        }
        return isDone;
    }

    @Override
    public int getId() {
        return id;
    }

}
