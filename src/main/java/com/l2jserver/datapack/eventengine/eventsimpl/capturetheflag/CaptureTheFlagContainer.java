package com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag;

import com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag.config.CTFEventConfig;
import com.l2jserver.datapack.eventengine.interfaces.IEventConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class CaptureTheFlagContainer extends BaseEventContainer {

    @Override
    protected Class<? extends AbstractEventConfig> getConfigClass() {
        return CTFEventConfig.class;
    }

    public Class<? extends BaseEvent> getEventClass() {
        return CaptureTheFlag.class;
    }

    public String getEventName() {
        return "Capture the flag";
    }

    public String getDescription() {
        return "Two teams fight to steal the flag of the other team";
    }
}
