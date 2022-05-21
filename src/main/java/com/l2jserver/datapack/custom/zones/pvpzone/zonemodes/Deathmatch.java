package com.l2jserver.datapack.custom.zones.pvpzone.zonemodes;

import com.l2jserver.datapack.custom.zones.pvpzone.ZoneMode;
import com.l2jserver.gameserver.custom.skin.BodyPart;
import com.l2jserver.gameserver.custom.skin.SkinManager;
import com.l2jserver.gameserver.custom.skin.Visibility;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.impl.character.player.*;
import com.l2jserver.gameserver.model.events.returns.AbstractEventReturn;
import com.l2jserver.gameserver.model.events.returns.ChatFilterReturn;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.zone.type.L2PvpZone;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CharInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Deathmatch extends ZoneMode {

    private final Map<L2PcInstance, Visibility> playersVisibility = new ConcurrentHashMap<>();

    public Deathmatch(L2PvpZone zone) {
        super(zone);
    }

    @Override
    protected AbstractEventReturn receivedEvent(IBaseEvent event) {
        switch (event.getType()) {
            case ON_PLAYER_PARTY_REQUEST -> {
                OnPlayerPartyRequest ptRequest = (OnPlayerPartyRequest) event;
                if (currentZone.isInsideZone(ptRequest.getRequestor()) || currentZone.isInsideZone(ptRequest.getPlayer())) {
                    new TerminateReturn(true, false, true);
                }
            }
            case ON_PLAYER_CHAT -> {
                OnPlayerChat chatEvent = (OnPlayerChat) event;
                if (!currentZone.isInsideZone(chatEvent.getActiveChar())) {
                    return null;
                }
                return new ChatFilterReturn(((OnPlayerChat) event).getText(), true, !isAllowedToTalk(((OnPlayerChat) event).getChatType()));
            }
            case ON_CREATURE_ZONE_ENTER -> {
                OnCreatureZoneEnter zoneEnter = ((OnCreatureZoneEnter) event);
                if (zoneEnter.getZone() != currentZone) {
                    return null;
                }
                if (!(zoneEnter.getCreature() instanceof L2PcInstance)) {
                    return null;
                }
                L2PcInstance pc = (L2PcInstance) zoneEnter.getCreature();
                pc.leaveParty();
                playersVisibility.put(pc, SkinManager.getInstance().isEnabled(pc));
                SkinManager.getInstance().setVisibility(pc, Visibility.ALL);
                SkinManager.getInstance().assignRandomAllDressSkinTemporary(pc, Integer.MAX_VALUE);

                ((L2PcInstance) zoneEnter.getCreature()).getAppearance().setVisibleName(((L2PcInstance) zoneEnter.getCreature()).getClassId().name());
                zoneEnter.getCreature().broadcastPacket(new CharInfo((L2PcInstance) zoneEnter.getCreature()));
            }
            case ON_CREATURE_ZONE_EXIT -> {
                OnCreatureZoneExit zoneExit = ((OnCreatureZoneExit) event);
                if (zoneExit.getZone() != currentZone) {
                    return null;
                }
                if (!(zoneExit.getCreature() instanceof L2PcInstance)) {
                    return null;
                }
                L2PcInstance pc = (L2PcInstance) zoneExit.getCreature();
                SkinManager.getInstance().setVisibility(pc, playersVisibility.get(pc));
                SkinManager.getInstance().restorePlayersSkins(pc);
                ((L2PcInstance) zoneExit.getCreature()).getAppearance().setVisibleName(null);
                zoneExit.getCreature().broadcastPacket(new CharInfo((L2PcInstance) zoneExit.getCreature()));
            }
            case ON_PLAYER_SKIN_USE -> {
                OnPlayerSkinUse skinEvent = (OnPlayerSkinUse) event;
                if (!currentZone.isInsideZone(skinEvent.getPlayer())) {
                    return null;
                }
                if (skinEvent.getBodypart() != BodyPart.ALLDRESS) {
                    return new TerminateReturn(true, true, true);
                }
            }
            case ON_PLAYER_VISIBILITY_CHANGE -> {
                OnPlayerVisibilityChange visibilityEvent = (OnPlayerVisibilityChange) event;
                if (!currentZone.isInsideZone(visibilityEvent.getPc())) {
                    return null;
                }
                return new TerminateReturn(true, true, true);
            }
            case ON_PLAYER_LOGOUT -> {
                OnPlayerLogout logout = (OnPlayerLogout) event;
                if (!currentZone.isInsideZone(logout.getActiveChar())) {
                    return null;
                }
                logout.getActiveChar().getAppearance().setVisibleName(null);
                SkinManager.getInstance().restorePlayersSkins(logout.getActiveChar());
                SkinManager.getInstance().setVisibility(logout.getActiveChar(), playersVisibility.get(logout.getActiveChar()));
            }
        }
        return null;
    }

    @Override
    public String getZoneModeName() {
        return "Deathmatch";
    }

    private boolean isAllowedToTalk(int type) {
        switch (type) {
            case Say2.ALL, Say2.SHOUT, Say2.HERO_VOICE, Say2.TRADE -> {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onZoneModeEnd() {
        this.currentZone.getPlayersInside().forEach(p -> {
            p.getAppearance().setVisibleName(null);
            SkinManager.getInstance().restorePlayersSkins(p);
            SkinManager.getInstance().setVisibility(p, playersVisibility.get(p));
        });
        this.removeAllListeners();
    }

    @Override
    public void onZoneModeStart() {
        registerConsumer(EventType.ON_CREATURE_ZONE_ENTER);
        registerConsumer(EventType.ON_CREATURE_ZONE_EXIT);
        registerConsumer(EventType.ON_PLAYER_LOGOUT);
        registerFunction(EventType.ON_PLAYER_PARTY_REQUEST);
        registerFunction(EventType.ON_PLAYER_CHAT);
        registerFunction(EventType.ON_PLAYER_SKIN_USE);
        registerFunction(EventType.ON_PLAYER_VISIBILITY_CHANGE);
    }
}
