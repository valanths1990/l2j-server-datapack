package com.l2jserver.datapack.custom.achievement.pojo;
import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import static java.util.Objects.requireNonNull;

public class NotPojo extends ConditionPojo {
    @JsonAlias({ "using", "player", "target", "not", "and", "game","or" })
    private final List<ConditionPojo> children;

    @ConstructorProperties("children")
    public NotPojo(List<ConditionPojo> children) {
        this.children = requireNonNull(children);
    }

    public List<ConditionPojo> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getConditionType() {
        return "not";
    }

    @Override
    public String toString() {
        return "NotPojo [children=" + children + "]";
    }

}
