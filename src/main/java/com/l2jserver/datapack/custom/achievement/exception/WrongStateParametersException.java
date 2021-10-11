package com.l2jserver.datapack.custom.achievement.exception;
public class WrongStateParametersException extends Exception {

    private String msg;

    public WrongStateParametersException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

}
