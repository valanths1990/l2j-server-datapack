package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Npc;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnInteractEvent extends ListenerEvent {

    private final Player mPlayer;
    private final Npc mNpc;

    public OnInteractEvent(Player player, Npc npc) {
        mPlayer = player;
        mNpc = npc;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Npc getNpc() {
        return mNpc;
    }

    public ListenerType getType() {
        return ListenerType.ON_INTERACT;
    }
}
