/*
 * Copyright (C) 2015-2016 L2J EventEngine
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.eventengine.task;

import com.l2jserver.gameserver.custom.Activity.ActivityManager;
import com.l2jserver.datapack.eventengine.EventActivity;
import com.l2jserver.datapack.eventengine.config.BaseConfigLoader;
import com.l2jserver.datapack.eventengine.enums.MessageType;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.model.config.MainEventConfig;
import com.l2jserver.datapack.eventengine.datatables.EventLoader;
import com.l2jserver.datapack.eventengine.enums.CollectionTarget;
import com.l2jserver.datapack.eventengine.enums.EventEngineState;
import com.l2jserver.datapack.eventengine.util.EventUtil;

import java.time.Duration;

/**
 * It handles the different state's behavior of EventEngineManager.
 *
 * @author fissban, Zephyr
 */
public class EventEngineTask implements Runnable {
    // Type Message System
    private final CollectionTarget _type = getConfig().getGlobalMessage() ? CollectionTarget.ALL_PLAYERS : CollectionTarget.ALL_NEAR_PLAYERS;

    private static MainEventConfig getConfig() {
        return BaseConfigLoader.getInstance().getMainConfig();
    }

    @Override
    public void run() {
        try {

            EventEngineState state = EventEngineManager.getInstance().getEventEngineState();
            if (state == EventEngineState.WAITING) {
                EventEngineManager.getInstance().setEventEngineState(EventEngineState.REGISTER);
//							EventEngineManager.getInstance().setNextEvent(EventLoader.getInstance().getRandomEventType());
            }
            switch (state) {
                case WAITING: {
                    if (EventEngineManager.getInstance().getTime() <= 0) {
                        EventEngineManager.getInstance().setNextEvent(EventLoader.getInstance().getRandomEventType());
                        EventEngineManager.getInstance().setTime(getConfig().getRegisterTime() * 60);
                        String eventName = EventEngineManager.getInstance().getNextEvent().getEventName();
                        EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "event_register_started", "%event%", eventName, _type);
                        EventEngineManager.getInstance().setEventEngineState(EventEngineState.REGISTER);
                        ActivityManager.getInstance().registerAction(new EventActivity(), Duration.ofSeconds(EventEngineManager.getInstance().getTime()));
                    }
                    break;
                }
                case REGISTER: {
//					L2World.getInstance().getPlayers().forEach(p -> EventEngineManager.getInstance().registerPlayer(CacheManager.getInstance().getPlayer(p.getObjectId())));
                    if (EventEngineManager.getInstance().getAllRegisteredPlayers().size() > 1) {
                        EventEngineManager.getInstance().setEventEngineState(EventEngineState.RUN_EVENT);
                    }
                    if (EventEngineManager.getInstance().getTime() > 0) {
                        int time = EventEngineManager.getInstance().getTime();
                        String eventName = EventEngineManager.getInstance().getNextEvent().getEventName();
                        EventUtil.announceTime(time, "event_register_state", MessageType.CRITICAL_ANNOUNCE, "%event%", eventName, _type);
                    } else {
                        if (EventEngineManager.getInstance().getAllRegisteredPlayers().size() < getConfig().getMinPlayers()) {
                            EventEngineManager.getInstance().cleanUp();
                            EventEngineManager.getInstance().setTime(getConfig().getInterval() * 60);
                            EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "event_aborted", _type);
                            EventUtil.announceTime(EventEngineManager.getInstance().getTime(), "event_next", MessageType.CRITICAL_ANNOUNCE, _type);
                            EventEngineManager.getInstance().setEventEngineState(EventEngineState.WAITING);
                        } else {
                            EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "event_register_ended", _type);
                            EventEngineManager.getInstance().setEventEngineState(EventEngineState.RUN_EVENT);
                        }
                    }
                    break;
                }
                case RUN_EVENT: {
                    BaseEvent<?> event = EventEngineManager.getInstance().getNextEvent().newEventInstance();
                    if (event == null) {
                        EventEngineManager.getInstance().cleanUp();
                        EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "wrong_run", _type);
                        EventUtil.announceTime(EventEngineManager.getInstance().getTime(), "event_next", MessageType.CRITICAL_ANNOUNCE, _type);
                        EventEngineManager.getInstance().setEventEngineState(EventEngineState.WAITING);
                        return;
                    }
                    EventEngineManager.getInstance().setCurrentEvent(event);
                    EventEngineManager.getInstance().setEventEngineState(EventEngineState.RUNNING_EVENT);
                    EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "event_started", _type);
                    EventEngineManager.getInstance().setTime(getConfig().getRunningTime() * 60);
                    break;
                }
                case RUNNING_EVENT: {
                    // Nothing
                    break;
                }
                case EVENT_ENDED: {
                    EventEngineManager.getInstance().cleanUp();
                    EventEngineManager.getInstance().setTime(getConfig().getInterval() * 60);
                    EventUtil.announceTo(MessageType.CRITICAL_ANNOUNCE, "event_end", _type);
                    EventUtil.announceTime(EventEngineManager.getInstance().getTime(), "event_next", MessageType.CRITICAL_ANNOUNCE, _type);
                    EventEngineManager.getInstance().setEventEngineState(EventEngineState.WAITING);
                    break;
                }
            }

//			if (state != EventEngineState.RUNNING_EVENT) {
            EventEngineManager.getInstance().decreaseTime();
            if (EventEngineManager.getInstance().getTime() < -30) {
                EventEngineManager.getInstance().setEventEngineState(EventEngineState.EVENT_ENDED);
            }
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}