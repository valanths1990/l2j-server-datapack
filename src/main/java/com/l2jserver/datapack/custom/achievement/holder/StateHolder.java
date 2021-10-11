package com.l2jserver.datapack.custom.achievement.holder;

public class StateHolder {
    private final int num;
    private final double current;
    private final boolean done;

    public StateHolder(int num, double current, boolean done) {
        this.num = num;
        this.current = current;
        this.done = done;
    }

    public int getNum() {
        return num;
    }

    public Double getCurrent() {
        return current;
    }

    public boolean isDone() {
        return done;
    }
}