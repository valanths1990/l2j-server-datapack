package com.l2jserver.datapack.custom.achievement.pojo;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;

public class StatePojo {
    private final Number start;
    private final Number end;
    private final String event;
    private final int id;

    @ConstructorProperties({
            "start",
            "end",
            "event",
            "id"
    })
    public StatePojo(Number start, Number end, String event, int id) {
        this.start = requireNonNull(start);
        this.end = requireNonNull(end);
        this.event = requireNonNull(event);
        this.id = id;
        if (event.isEmpty()) {
            throw new NullPointerException("A state without a valid Event cant exist!");
        }
    }

    public Number getStart() {
        return start;
    }

    public Number getEnd() {
        return end;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "StatePojo [end=" + end + ", event=" + event + ", start=" + start + "]";
    }

    public int getId() {
        return id;
    }

}
