package com.l2jserver.datapack.custom.achievement.exception;

public class WrongEventTypeException extends Exception {

    private String msg;

    public WrongEventTypeException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

}
