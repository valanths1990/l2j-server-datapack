package com.l2jserver.datapack.custom.achievement.stateImpl;

import com.l2jserver.datapack.custom.achievement.pojo.StatePojo;
import com.l2jserver.datapack.custom.achievement.stateImpl.states.*;
import com.l2jserver.gameserver.model.events.EventType;

public class StateFactory {


    public static IState<? extends Number> getState(StatePojo s) {
        return switch (EventType.valueOf(s.getEvent())) {
            case ON_PLAYER_KARMA_CHANGED -> new OnPlayerKarmaChangedState(s.getStart().intValue(), s.getEnd().intValue(), 0, EventType.valueOf(s.getEvent()));
            case ON_PLAYER_FAME_CHANGED -> new OnPlayerFameChangedState(s.getStart().longValue(), s.getEnd().longValue(), 0l,
                    EventType.valueOf(s.getEvent()));
            case ON_PLAYABLE_EXP_CHANGED -> new OnPlayerExpChangedState(s.getStart().longValue(), s.getEnd().longValue(), 0l,
                    EventType.valueOf(s.getEvent()));
            case ON_PLAYER_PVP_CHANGED -> new OnPlayerPvpChangedState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()));
            case ON_TRAP_ACTION -> new OnTrapActionState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()));
            case ON_ATTACKABLE_ATTACK -> new OnAttackableAttackState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()));
            case ON_ATTACKABLE_KILL -> new OnAttackableKillState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()), s.getId());
            case ON_OLYMPIAD_MATCH_RESULT -> new OnOlympiadMatchResultState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()));
            case ON_PLAYER_ITEM_ADD -> new OnPlayerItemAddState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()), s.getId());
            case ON_PLAYER_ITEM_PICKUP -> new OnPlayerItemPickUpState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()), s.getId());
            case ON_PLAYER_TRANSFORM -> new OnPlayerTransformState(s.getStart().intValue(), s.getEnd().intValue(), 0,
                    EventType.valueOf(s.getEvent()), s.getId());
            default -> new State<>(s.getStart().intValue(), s.getEnd().intValue(), 0, EventType.valueOf(s.getEvent()));
        };


    }

    public static <T extends IState<? extends Number>> T getStateCopy(T s) {


        return null;
    }
}
