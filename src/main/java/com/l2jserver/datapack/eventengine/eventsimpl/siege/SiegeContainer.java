package com.l2jserver.datapack.eventengine.eventsimpl.siege;

import com.l2jserver.datapack.eventengine.eventsimpl.siege.config.SiegeConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class SiegeContainer extends BaseEventContainer {
	@Override public Class<? extends BaseEvent<SiegeConfig>> getEventClass() {
		return Siege.class;
	}

	@Override public String getEventName() {
		return "Siege";
	}

	@Override public String getDescription() {
		return "Capture the Towers to gain the control over the Castle.";
	}

	@Override protected Class<? extends AbstractEventConfig> getConfigClass() {
		return SiegeConfig.class;
	}
}
