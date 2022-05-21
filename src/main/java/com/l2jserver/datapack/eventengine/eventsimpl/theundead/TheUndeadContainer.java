package com.l2jserver.datapack.eventengine.eventsimpl.theundead;

import com.l2jserver.datapack.eventengine.eventsimpl.theundead.config.TheUndeadConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class TheUndeadContainer extends BaseEventContainer {
    @Override
    public Class<? extends BaseEvent<TheUndeadConfig>> getEventClass() {
        return TheUndead.class;
    }

    @Override
    public String getEventName() {
        return "The Undead";
    }

    @Override
    public String getDescription() {
        return "Fight the Undead.";
    }

    @Override
    protected Class<? extends AbstractEventConfig> getConfigClass() {
        return TheUndeadConfig.class;
    }
}
