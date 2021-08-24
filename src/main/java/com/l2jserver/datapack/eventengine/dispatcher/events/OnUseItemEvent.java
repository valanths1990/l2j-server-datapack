package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.template.ItemTemplate;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnUseItemEvent extends ListenerEvent {

    private final Player mPlayer;
    private final ItemTemplate mItem;

    public OnUseItemEvent(Player player, ItemTemplate item) {
        mPlayer = player;
        mItem = item;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public ItemTemplate getItem() {
        return mItem;
    }

    public ListenerType getType() {
        return ListenerType.ON_USE_ITEM;
    }
}
