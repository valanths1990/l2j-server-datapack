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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.model.entity.FriendlyNpc;
import com.l2jserver.datapack.eventengine.model.entity.Monster;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.datapack.eventengine.model.entity.Npc;
import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;

public class SpawnManager {

	private static final Logger LOGGER = Logger.getLogger(SpawnManager.class.getName());

	// List of NPC in the event.
	private final Set<Npc> _eventNpcs = ConcurrentHashMap.newKeySet();




	public Npc addNpc(int npcId, LocationHolder loc, String title, boolean randomOffset, int instanceId) {
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();
		int heading = loc.getHeading();

		L2Npc l2Npc;
		try {
			L2NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
			if (template != null) {
				if (randomOffset) {
					x += Rnd.get(-1000, 1000);
					y += Rnd.get(-1000, 1000);
				}

				L2Spawn spawn = new L2Spawn(template);
				spawn.setHeading(heading);
				spawn.setX(x);
				spawn.setY(y);
				spawn.setZ(z + 20);
				spawn.setAmount(1);
				spawn.setInstanceId(instanceId);
				l2Npc = spawn.doSpawn();// isSummonSpawn
				if (title != null)
					l2Npc.setTitle(title);
				SpawnTable.getInstance().addNewSpawn(spawn, false);
				spawn.init();
				// animation
				spawn.getLastSpawn().broadcastPacket(new MagicSkillUse(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));
								Npc npc = (Npc) CacheManager.getInstance().getCharacter(l2Npc, true);
				if (l2Npc instanceof L2MonsterInstance) {
					npc = new Monster(l2Npc.getId());
				} else {
					npc = new FriendlyNpc(l2Npc.getObjectId());
				}
				_eventNpcs.add(npc);
				return npc;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

		return null;
	}

	public Collection<Npc> getAllEventNpcs() {
		return _eventNpcs;
	}
    public void addNpc(L2Npc l2npc){
		Npc npc;
		if (l2npc instanceof L2MonsterInstance) {
			npc = new Monster(l2npc.getId());
		} else {
			npc = new FriendlyNpc(l2npc.getObjectId());
		}
		_eventNpcs.add(npc);
	}
	public void removeAllNpcs() {
		_eventNpcs.forEach(npc -> {
			npc.stopRespawn();
			npc.deleteMe();
		});
		_eventNpcs.clear();
	}

	public boolean isNpcInEvent(Npc npc) {
		return _eventNpcs.contains(npc);
	}

	public void removeNpc(Npc npc) {
		if (npc != null) {
			npc.stopRespawn();
			npc.deleteMe();
			_eventNpcs.remove(npc);
		}
	}
}