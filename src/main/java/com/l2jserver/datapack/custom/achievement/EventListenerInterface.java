package com.l2jserver.datapack.custom.achievement;

import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public interface EventListenerInterface <T extends IBaseEvent>{
    void execute(T event);
}
