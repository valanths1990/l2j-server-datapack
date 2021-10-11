package com.l2jserver.datapack.custom.achievement.pojo;
import java.beans.ConstructorProperties;
import java.util.List;


public class ConditionsPojo {

    private final List<ConditionPojo> conditions;

    @ConstructorProperties("conditions")
    public ConditionsPojo(List<ConditionPojo> conditions) {
        this.conditions = conditions;
    }

    public List<ConditionPojo> getConditions() {
        return conditions;
    }

}
