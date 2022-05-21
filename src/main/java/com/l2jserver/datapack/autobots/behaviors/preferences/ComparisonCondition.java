package com.l2jserver.datapack.autobots.behaviors.preferences;

public enum ComparisonCondition {
    Equals("="),
    LessThan("<"),
    LessOrEqualThan("<="),
    MoreThan(">"),
    MoreOrEqualThan(">=");
    private final String operation;

    ComparisonCondition(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
