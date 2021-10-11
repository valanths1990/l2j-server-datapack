package com.l2jserver.datapack.custom.achievement.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @Type(value = TargetPojo.class, name = "target"),
        @Type(value = PlayerPojo.class, name = "player"),
        @Type(value = UsingPojo.class, name = "using"),
        @Type(value = AndPojo.class, name = "and"),
        @Type(value = GamePojo.class, name = "game"),
        @Type(value = OrPojo.class, name = "or"),
        @Type(value = NotPojo.class, name = "not")
})
public abstract class ConditionPojo {
    @JsonIgnore
    public abstract String getConditionType();

}
