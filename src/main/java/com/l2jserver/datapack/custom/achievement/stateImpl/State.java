package com.l2jserver.datapack.custom.achievement.stateImpl;

import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

import java.io.Serial;
import java.io.Serializable;


public class State<T extends Number> implements IState<T>, Serializable {
    @Serial
    private static final long serialVersionUID = 689053496907397993L;
    protected T start;
    protected T end;
    protected T current;
    protected EventType eventType;
    protected boolean isDone = false;

    public State(T start, T end, T current, EventType eventType) {
        this.start = start;
        this.end = end;
        this.current = current;
        this.eventType = eventType;
    }

    public T getStart() {
        return start;
    }

    public T getEnd() {
        return end;
    }

    @Override
    public T getCurrent() {
        return current;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void increaseProgress(Integer current) {
        this.current = (T) current;
        if (this.current.intValue() >= end.intValue()) {
            this.current = this.end;
            isDone = true;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean transit(IBaseEvent event) {
        if (event == null) {
            return false;
        }
        if (event.getType() == this.eventType && !isDone) {
            if (current instanceof Integer) {
                current = (T) (Integer) (((Integer) current) + 1);
                if (current.intValue() >= end.intValue()) {
                    current = end;
                    isDone = true;
                    return true;
                }
            }
        }
        return isDone;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public int getId() {
        return 0;
    }


    @Override
    public void reset() {
        this.current = this.start;
        this.isDone = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        State<? extends Number> other = (State<? extends Number>) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (eventType != other.eventType)
            return false;
        if (start == null) {
            return other.start == null;
        } else return start.equals(other.start);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((eventType == null) ? 0 : eventType.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

}
