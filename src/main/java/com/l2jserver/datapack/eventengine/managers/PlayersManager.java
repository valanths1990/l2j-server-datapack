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
package com.l2jserver.datapack.eventengine.managers;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.l2jserver.datapack.eventengine.events.listeners.EventEngineListener;
import com.l2jserver.datapack.eventengine.interfaces.IParticipant;
import com.l2jserver.datapack.eventengine.model.entity.*;
import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.model.entity.Character;

public class PlayersManager {
	private final Set<Character> _eventPlayers = ConcurrentHashMap.newKeySet();

	/**
	 * We obtain the full list of all players within an event.
	 *
	 * @return Collection<Player>
	 */
	public Set<Player> getAllEventPlayers() {
		return _eventPlayers.stream().filter(c -> c instanceof Player).map(c -> (Player) c).collect(Collectors.toSet());
	}

	public Set<IParticipant> getAllEventParticipants() {
		return _eventPlayers.stream().filter(p -> p instanceof IParticipant).map(p -> (IParticipant) p).collect(Collectors.toSet());
	}

	/**
	 * We add all the characters registered to our list of characters in the event.<br>
	 * Check if player in olympiad.<br>
	 * Check if player in duel.<br>
	 * Check if player in observer mode.
	 */
	public void createEventPlayers() {
		for (Player player : EventEngineManager.getInstance().getAllRegisteredPlayers()) {
			// Check if player in olympiad
			if (player.getPcInstance().isInOlympiadMode()) {
				player.sendMessage("You can not attend the event being in the Olympics.");
				continue;
			}
			// Check if player in duel
			if (player.getPcInstance().isInDuel()) {
				player.sendMessage("You can not attend the event being in the Duel.");
				continue;
			}
			// Check if player in observer mode
			if (player.getPcInstance().inObserverMode()) {
				player.sendMessage("You can not attend the event being in the Observer mode.");
				continue;
			}
			_eventPlayers.add(player);
			player.getPcInstance().addEventListener(new EventEngineListener(player.getPcInstance()));
		}
		// We clean the list, no longer we need it
		EventEngineManager.getInstance().clearRegisteredPlayers();
	}

	/**
	 * Check if the playable is participating in any event. In the case of a summon, verify that the owner participates. For not participate in an event is returned <u>false.</u>
	 *
	 * @param playable
	 * @return boolean
	 */
	public boolean isPlayableInEvent(Playable playable) {
		if (playable.isPlayer() || playable.isSummon()) {
			return _eventPlayers.contains((IParticipant) playable);
		}
		return false;
	}

	/**
	 * Check if a player is participating in any event. In the case of dealing with a summon, verify the owner. For an event not participated returns <u>null.</u>
	 *
	 * @param character
	 * @return Player
	 */
	public Player getEventPlayer(Character character) {
		if (character instanceof Playable && isPlayableInEvent((Playable) character)) {
			Character c = character instanceof Summon ? ((Summon) character).getOwner() : character;
			return _eventPlayers.stream().filter(p -> p.getObjectId() == c.getObjectId()).map(ca -> (Player) ca).findFirst().orElse(null);
		}
		return null;
	}

	public Player getEventPlayer(int id) {
		return _eventPlayers.stream().filter(c -> c.getObjectId() == id && c instanceof Player).map(c -> (Player) c).findFirst().orElse(null);
	}

	public Character getCharacter(int id) {
		return _eventPlayers.stream().filter(c -> c.getObjectId() == id).findFirst().orElse(null);
	}

	public boolean contains(int id) {
		return _eventPlayers.stream().anyMatch(c -> c.getObjectId() == id);
	}

	public Playable getPlayable(int id) {
		return _eventPlayers.stream().filter(c -> c.getObjectId() == id && c instanceof Playable).map(c -> (Player) c).findFirst().orElse(null);
	}
}