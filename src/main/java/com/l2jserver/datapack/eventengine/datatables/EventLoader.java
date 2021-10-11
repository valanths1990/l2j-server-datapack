package com.l2jserver.datapack.eventengine.datatables;

import com.l2jserver.datapack.eventengine.eventsimpl.allvsall.AllVsAllContainer;
import com.l2jserver.datapack.eventengine.eventsimpl.capturetheflag.CaptureTheFlagContainer;
import com.l2jserver.datapack.eventengine.eventsimpl.huntingground.HuntingGroundContainer;
import com.l2jserver.datapack.eventengine.eventsimpl.siege.SiegeContainer;
import com.l2jserver.datapack.eventengine.eventsimpl.teamvsteam.TeamVsTeamContainer;
import com.l2jserver.datapack.eventengine.interfaces.IEventContainer;
import com.l2jserver.commons.util.Rnd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventLoader {


	private final ArrayList<IEventContainer> _eventList = new ArrayList<>();
	private final Map<String, IEventContainer> _eventMap = new HashMap<>();

	private EventLoader() {
		loadEvents();
	}

	public IEventContainer getEvent(String name) {
		return _eventMap.get(name);
	}

	public IEventContainer getRandomEventType() {
		return _eventList.get(Rnd.get(_eventList.size()));
	}

	public ArrayList<IEventContainer> getEnabledEvents() {
		return _eventList;
	}

	private void loadEvents() {

		IEventContainer teamVsTeam = new TeamVsTeamContainer();
		_eventList.add(teamVsTeam);
		_eventMap.put(teamVsTeam.getSimpleEventName(), teamVsTeam);

		IEventContainer allVsAll = new AllVsAllContainer();
		_eventList.add(allVsAll);
		_eventMap.put(allVsAll.getSimpleEventName(), allVsAll);

		IEventContainer captureTheFlag = new CaptureTheFlagContainer();
		_eventList.add(captureTheFlag);
		_eventMap.put(captureTheFlag.getSimpleEventName(), captureTheFlag);

		IEventContainer huntingGround = new HuntingGroundContainer();
		_eventList.add(huntingGround);
		_eventMap.put(huntingGround.getSimpleEventName(), huntingGround);

//		IEventContainer siege= new SiegeContainer();
//		_eventList.add(siege);
//		_eventMap.put(siege.getSimpleEventName(),siege);

	}

	public static EventLoader getInstance() {
		return EventLoader.SingletonHolder._instance;
	}

	private static class SingletonHolder {
		private static final EventLoader _instance = new EventLoader();
	}
}
