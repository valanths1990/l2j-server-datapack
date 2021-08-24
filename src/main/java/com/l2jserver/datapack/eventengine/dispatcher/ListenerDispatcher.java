package com.l2jserver.datapack.eventengine.dispatcher;

import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.dispatcher.events.ListenerEvent;
import com.l2jserver.datapack.eventengine.interfaces.IListenerSuscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerDispatcher {

    private static ListenerDispatcher sInstance;

    private final Map<ListenerType, List<IListenerSuscriber>> mSuscribers = new HashMap<>();

    private ListenerDispatcher() {
        for (ListenerType type : ListenerType.values()) {
            mSuscribers.put(type, new ArrayList<>());
        }
    }

    public synchronized void addSuscription(ListenerType type, IListenerSuscriber suscriber) {
        mSuscribers.get(type).add(suscriber);
    }

    public synchronized void removeSuscription(ListenerType type, IListenerSuscriber suscriber) {
        mSuscribers.get(type).remove(suscriber);
    }

    public synchronized void removeSuscriber(IListenerSuscriber suscriber) {
        for (List<IListenerSuscriber> suscribers : mSuscribers.values()) {
            suscribers.remove(suscriber);
        }
    }

    public boolean notifyEvent(ListenerEvent event) {
        publishManager(event);

        for (IListenerSuscriber subscriber : mSuscribers.get(event.getType())) {
            publishEvents(subscriber, event);
            if (event.isCanceled()) return true;
        }

        return false;
    }

    private void publishEvents(IListenerSuscriber suscriber, ListenerEvent event) {
        switch (event.getType()) {
            case ON_LOG_IN -> suscriber.listenerOnLogin((OnLogInEvent) event);
            case ON_LOG_OUT -> suscriber.listenerOnLogout((OnLogOutEvent) event);
            case ON_INTERACT -> suscriber.listenerOnInteract((OnInteractEvent) event);
            case ON_KILL -> suscriber.listenerOnKill((OnKillEvent) event);
            case ON_DEATH -> suscriber.listenerOnDeath((OnDeathEvent) event);
            case ON_ATTACK -> suscriber.listenerOnAttack((OnAttackEvent) event);
            case ON_USE_SKILL -> suscriber.listenerOnUseSkill((OnUseSkillEvent) event);
            case ON_USE_ITEM -> suscriber.listenerOnUseItem((OnUseItemEvent) event);
            case ON_PLAYABLE_HIT -> suscriber.listenerOnPlayableHit((OnPlayableHitEvent)event);
            case ON_UNEQUIP_ITEM -> suscriber.listenerOnUnequipItem((OnUnequipItem) event);
            case ON_USE_TELEPORT -> suscriber.listenerOnUseTeleport((OnUseTeleport) event);
            case ON_TOWER_CAPTURED -> suscriber.listenerOnTowerCaptured((OnTowerCapturedEvent) event);
            case ON_DOOR_ACTION -> suscriber.listenerOnDoorAction((OnDoorActionEvent)event);
        }
    }

    private void publishManager(ListenerEvent event) {
        switch (event.getType()) {
            case ON_LOG_IN:
                EventEngineManager.getInstance().listenerOnLogin((OnLogInEvent) event);
                break;
            case ON_LOG_OUT:
                EventEngineManager.getInstance().listenerOnLogout((OnLogOutEvent) event);
                break;
        }
    }

    public static ListenerDispatcher getInstance() {
        if (sInstance == null) sInstance = new ListenerDispatcher();
        return sInstance;
    }
}
