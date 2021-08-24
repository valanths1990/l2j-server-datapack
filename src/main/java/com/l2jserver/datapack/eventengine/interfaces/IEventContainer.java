package com.l2jserver.datapack.eventengine.interfaces;

import com.l2jserver.datapack.eventengine.model.base.BaseEvent;

public interface IEventContainer {

    Class<? extends BaseEvent> getEventClass();

    String getEventName();

    String getSimpleEventName();

    String getDescription();

    int getMinLevel();

    int getMaxLevel();

    int getMinParticipants();

    int getMaxParticipants();

    int getRunningTime();

    String getRewards();

    BaseEvent newEventInstance();
}
