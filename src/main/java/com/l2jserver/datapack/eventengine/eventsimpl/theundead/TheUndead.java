package com.l2jserver.datapack.eventengine.eventsimpl.theundead;

import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.eventsimpl.theundead.config.TheUndeadConfig;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;

import java.security.Permission;

public class TheUndead extends BaseEvent<TheUndeadConfig> {
    @Override
    protected String getInstanceFile() {

        return null;
    }

    @Override
    protected TeamsBuilder onCreateTeams() {
        return null;
    }

    @Override
    protected void onEventStart() {

    }

    @Override
    protected void onEventFight() {

    }

    @Override
    protected void onEventEnd() {

    }
}
