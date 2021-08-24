package com.l2jserver.datapack.eventengine.eventsimpl.huntingground;

import com.l2jserver.datapack.eventengine.eventsimpl.huntingground.config.HuntingGroundConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class HuntingGroundContainer extends BaseEventContainer {
	@Override public Class<? extends BaseEvent<HuntingGroundConfig>> getEventClass() {
		return HuntingGround.class;
	}

	@Override public String getEventName() {
		return "Hunting Ground";
	}

	@Override public String getDescription() {
		return "Two Teams hunt each other with Powerful oneshot Bows";
	}

	@Override protected Class<? extends AbstractEventConfig> getConfigClass() {
		return HuntingGroundConfig.class;
	}
}
