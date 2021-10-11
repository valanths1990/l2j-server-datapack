package com.l2jserver.datapack.custom.achievement.exception;
public class MissingAchievementForUnlock extends Exception {

    private String msg;

    public MissingAchievementForUnlock(String msg) {
        super(msg);
        this.msg = msg;

    }

    public String getMsg() {
        return this.msg;
    }

}
