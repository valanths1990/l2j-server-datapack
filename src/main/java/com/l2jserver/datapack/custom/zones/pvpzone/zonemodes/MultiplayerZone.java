package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.datapack.custom.zones.pvpzone.ZoneMode;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.returns.AbstractEventReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;

public class MultiplayerZone extends ZoneMode {
    public MultiplayerZone(L2PvpZone currentZone) {
        super(currentZone);
    }


    @Override
    protected void onZoneModeEnd() {

    }

    @Override
    public void onZoneModeStart() {

    }

    @Override
    protected AbstractEventReturn receivedEvent(IBaseEvent event) {
        return null;
    }

    @Override
    public String getZoneModeName() {
        return "Multiplayer";
    }
}
