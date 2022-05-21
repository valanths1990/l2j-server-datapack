package com.l2jserver.datapack.eventengine;

import com.l2jserver.gameserver.custom.Activity.IActivity;
import com.l2jserver.gameserver.custom.Activity.Priority;

public class EventActivity implements IActivity {
    @Override
    public String getBypass() {
        return "Bypass -h eventengine;homepage register";
    }

    @Override
    public String getImage() {
        return "Crest.crest_%serverId%_%imageName:event";
    }

    @Override
    public String getName() {
        return EventEngineManager.getInstance().getNextEvent().getEventName();
    }

    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }
}
