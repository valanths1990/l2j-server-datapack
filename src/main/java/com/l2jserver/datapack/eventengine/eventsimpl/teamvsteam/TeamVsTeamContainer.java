package com.l2jserver.datapack.eventengine.eventsimpl.teamvsteam;

import com.l2jserver.datapack.eventengine.eventsimpl.teamvsteam.config.TvTEventConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.base.BaseEventContainer;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;

public class TeamVsTeamContainer extends BaseEventContainer {

	@Override protected Class<? extends AbstractEventConfig> getConfigClass() {
		return TvTEventConfig.class;
	}

	public Class<? extends BaseEvent<TvTEventConfig>> getEventClass() {
		return TeamVsTeam.class;
	}

	public String getEventName() {
		return "Team vs Team";
	}

	public String getDescription() {
		return "Two teams fight to death";
	}
}
