package com.l2jserver.datapack.custom.achievement.pojo;
import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonTypeName;
import static java.util.Objects.requireNonNull;

@JsonTypeName("and")
public class AndPojo extends ConditionPojo {
    private final List<ConditionPojo> children;

    @ConstructorProperties("children")
    public AndPojo(List<ConditionPojo> children) {
        this.children = requireNonNull(children);
    }

    public List<ConditionPojo> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getConditionType() {
        return "and";
    }

    @Override
    public String toString() {
        return "AndPojo [children=" + children + "]";
    }

}
