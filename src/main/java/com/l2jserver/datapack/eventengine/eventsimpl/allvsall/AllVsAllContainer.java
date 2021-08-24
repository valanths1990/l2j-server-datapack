package com.l2jserver.datapack.eventengine.eventsimpl.allvsall;

import com.l2jserver.datapack.eventengine.eventsimpl.allvsall.config.AllVsAllEventConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class AllVsAllContainer extends BaseEventContainer {

	@Override protected Class<? extends AbstractEventConfig> getConfigClass() {
		return AllVsAllEventConfig.class;
	}

	public Class<? extends BaseEvent> getEventClass() {
		return AllVsAll.class;
	}

	public String getEventName() {
		return "All vs All";
	}

	public String getDescription() {
		return "Kill whatever you want, who kills more, wins the event";
	}
}
