package com.l2jserver.datapack.custom.achievement.stateImpl;

import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public interface IState<T extends Number> {

    boolean transit(IBaseEvent event);

    T getCurrent();

    EventType getEventType();

    int getId();

    T getStart();

    T getEnd();

    boolean isDone();

    void increaseCurrent(Double current);

    void reset();
}
