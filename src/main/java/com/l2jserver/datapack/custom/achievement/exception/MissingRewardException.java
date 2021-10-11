package com.l2jserver.datapack.custom.achievement.exception;
public class MissingRewardException extends Exception {

    private String msg;

    public MissingRewardException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }
}
