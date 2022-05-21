package com.l2jserver.datapack.eventengine.dispatcher;

import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.dispatcher.events.ListenerEvent;
import com.l2jserver.datapack.eventengine.interfaces.IListenerSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListenerDispatcher {

    private static ListenerDispatcher sInstance;

    private final Map<ListenerType, List<IListenerSubscriber>> mSuscribers = new HashMap<>();

    private ListenerDispatcher() {
        for (ListenerType type : ListenerType.values()) {
            mSuscribers.put(type, new ArrayList<>());
        }
    }

    public synchronized void addSuscription(ListenerType type, IListenerSubscriber suscriber) {
        mSuscribers.get(type).add(suscriber);
    }

    public synchronized void removeSuscription(ListenerType type, IListenerSubscriber suscriber) {
        mSuscribers.get(type).remove(suscriber);
    }

    public synchronized void removeSuscriber(IListenerSubscriber suscriber) {
        for (List<IListenerSubscriber> suscribers : mSuscribers.values()) {
            suscribers.remove(suscriber);
        }
    }

    public boolean notifyEvent(ListenerEvent event) {
        publishManager(event);

        for (IListenerSubscriber subscriber : mSuscribers.get(event.getType())) {
            publishEvents(subscriber, event);
            if (event.isCanceled()) return true;
        }

        return false;
    }

    private void publishEvents(IListenerSubscriber subscriber, ListenerEvent event) {
        switch (event.getType()) {
            case ON_LOG_IN -> subscriber.listenerOnLogin((OnLogInEvent) event);
            case ON_LOG_OUT -> subscriber.listenerOnLogout((OnLogOutEvent) event);
            case ON_INTERACT -> subscriber.listenerOnInteract((OnInteractEvent) event);
            case ON_KILL -> subscriber.listenerOnKill((OnKillEvent) event);
            case ON_DEATH -> subscriber.listenerOnDeath((OnDeathEvent) event);
            case ON_ATTACK -> subscriber.listenerOnAttack((OnAttackEvent) event);
            case ON_USE_SKILL -> subscriber.listenerOnUseSkill((OnUseSkillEvent) event);
            case ON_USE_ITEM -> subscriber.listenerOnUseItem((OnUseItemEvent) event);
            case ON_PLAYABLE_HIT -> subscriber.listenerOnPlayableHit((OnPlayableHitEvent) event);
            case ON_UNEQUIP_ITEM -> subscriber.listenerOnUnequipItem((OnUnequipItem) event);
            case ON_USE_TELEPORT -> subscriber.listenerOnUseTeleport((OnUseTeleport) event);
            case ON_TOWER_CAPTURED -> subscriber.listenerOnTowerCaptured((OnTowerCapturedEvent) event);
            case ON_DOOR_ACTION -> subscriber.listenerOnDoorAction((OnDoorActionEvent) event);
            case ON_DLG_ANSWER -> subscriber.listenerOnDlgAnswer((OnDlgAnswer) event);
            case ON_PLAYER_MOVE -> subscriber.listenerOnPlayerMove((OnPlayerMoveEvent) event);
        }
    }

    private void publishManager(ListenerEvent event) {
        switch (event.getType()) {
            case ON_LOG_IN -> EventEngineManager.getInstance().listenerOnLogin((OnLogInEvent) event);
            case ON_LOG_OUT -> EventEngineManager.getInstance().listenerOnLogout((OnLogOutEvent) event);
        }
    }

    public static ListenerDispatcher getInstance() {
        if (sInstance == null) sInstance = new ListenerDispatcher();
        return sInstance;
    }
}
