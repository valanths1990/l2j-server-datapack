package com.l2jserver.datapack.custom.achievement.pojo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;

@JsonTypeName("player")
public class PlayerPojo extends ConditionPojo {

    private final String attribute;
    private final String value;

    @ConstructorProperties({ "attribute", "value" })
    public PlayerPojo(String attribute, String value) {
        this.attribute = requireNonNull(attribute);
        this.value = requireNonNull(value);
    }

    @Override
    public String getConditionType() {
        return "player";
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValue() {
        return value;
    }

}
