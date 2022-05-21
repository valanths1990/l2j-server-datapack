package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.interfaces.IListenerSubscriber;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;

public class OnPlayerMoveEvent extends ListenerEvent {
    private Player player;
    private LocationHolder destination;

    public OnPlayerMoveEvent(Player player, LocationHolder destination) {
        this.player = player;
        this.destination = destination;
    }

    public Player getPlayer() {
        return player;
    }

    public LocationHolder getDestination() {
        return destination;
    }

    @Override
    public ListenerType getType() {
        return ListenerType.ON_PLAYER_MOVE;
    }
}
