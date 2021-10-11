package com.l2jserver.datapack.custom.achievement.exception;

public class AchievementParsingException extends Exception {

    private String failedAttribute;
    private String msg;

    public AchievementParsingException(String failedAttribute, String msg) {
        super(msg);
        this.failedAttribute = failedAttribute;
        this.msg = msg;
    }

    public String getFailedAttribute() {
        return failedAttribute;
    }

    public String getMsg() {
        return msg;
    }

}
