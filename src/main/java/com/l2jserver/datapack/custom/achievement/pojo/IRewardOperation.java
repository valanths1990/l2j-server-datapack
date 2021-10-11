package com.l2jserver.datapack.custom.achievement.pojo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ItemPojo.class, name = "item"),
        @JsonSubTypes.Type(value = ExpSpPojo.class, name = "expSp"),
        @JsonSubTypes.Type(value = NoblesPojo.class, name = "nobles"),
        @JsonSubTypes.Type(value = BuffPojo.class, name = "buff"),
        @JsonSubTypes.Type(value = NameColorPojo.class, name = "nameColor"),
        @JsonSubTypes.Type(value = SkinPojo.class, name = "skin"),
        @JsonSubTypes.Type(value = TeleportPojo.class, name = "teleport")
})
public interface IRewardOperation {
    void executeOperation(L2PcInstance pc);
    String getRewardIcon();
    Long getCount();
}
