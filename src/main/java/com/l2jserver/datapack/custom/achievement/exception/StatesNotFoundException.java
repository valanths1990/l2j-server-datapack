package com.l2jserver.datapack.custom.achievement.exception;
public class StatesNotFoundException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String msg;

    public StatesNotFoundException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
