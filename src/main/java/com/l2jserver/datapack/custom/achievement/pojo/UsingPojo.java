package com.l2jserver.datapack.custom.achievement.pojo;

import com.fasterxml.jackson.annotation.JsonTypeName;

import static java.util.Objects.requireNonNull;

import java.beans.ConstructorProperties;

@JsonTypeName("using")
public class UsingPojo extends ConditionPojo {

    private final String attribute;
    private final String value;
    @ConstructorProperties({ "attribute", "value" })
    public UsingPojo(String attribute, String value) {
        this.attribute = requireNonNull(attribute);
        this.value = requireNonNull(value);
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public String getConditionType() {
        return "using";
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "UsingPojo [attri=" + attribute + ", value=" + value + "]";
    }

}
