package com.l2jserver.datapack.custom.achievement.exception;
public class DescribtionNotFoundException extends Exception {

    private String msg;

    public DescribtionNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }
}
