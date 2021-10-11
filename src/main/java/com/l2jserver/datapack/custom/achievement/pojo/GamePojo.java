package com.l2jserver.datapack.custom.achievement.pojo;
import com.fasterxml.jackson.annotation.JsonTypeName;


import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;

@JsonTypeName("game")
public class GamePojo extends ConditionPojo {
    private final String attribute;
    private final String value;

    @ConstructorProperties({ "attribute", "value" })
    public GamePojo(String attribute, String value) {
        this.attribute = requireNonNull(attribute);
        this.value = requireNonNull(value);
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "GamePojo [attri=" + attribute + ", value=" + value + "]";
    }

    @Override
    public String getConditionType() {
        return "game";
    }
}
