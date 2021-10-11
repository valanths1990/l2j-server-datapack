package com.l2jserver.datapack.custom.achievement.stateImpl.states;

import com.l2jserver.datapack.custom.achievement.stateImpl.State;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.OnAttackableKill;

public class OnAttackableKillState extends State<Integer> {
    private final int id;

    public OnAttackableKillState(Integer start, Integer end, Integer current, EventType eventType, int id) {
        super(start, end, current, eventType);
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event instanceof OnAttackableKill && !isDone) {
            OnAttackableKill onAttackableKillEvent = (OnAttackableKill) event;
            if (onAttackableKillEvent.getAttacker() == null || onAttackableKillEvent.getTarget() == null) {
                return false;
            }
            if (id == 0 || (onAttackableKillEvent.getTarget().getId() == id
                    && onAttackableKillEvent.getTarget() instanceof L2MonsterInstance)) {
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
