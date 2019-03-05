/*
 * Copyright © 2004-2019 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.hellbound;

import java.util.logging.Logger;

import com.l2jserver.datapack.handlers.admincommandhandlers.AdminHellbound;
import com.l2jserver.datapack.handlers.voicedcommandhandlers.Hellbound;
import com.l2jserver.datapack.hellbound.AI.Amaskari;
import com.l2jserver.datapack.hellbound.AI.Chimeras;
import com.l2jserver.datapack.hellbound.AI.DemonPrince;
import com.l2jserver.datapack.hellbound.AI.HellboundCore;
import com.l2jserver.datapack.hellbound.AI.Keltas;
import com.l2jserver.datapack.hellbound.AI.NaiaLock;
import com.l2jserver.datapack.hellbound.AI.OutpostCaptain;
import com.l2jserver.datapack.hellbound.AI.Ranku;
import com.l2jserver.datapack.hellbound.AI.Slaves;
import com.l2jserver.datapack.hellbound.AI.Typhoon;
import com.l2jserver.datapack.hellbound.AI.NPC.Bernarde.Bernarde;
import com.l2jserver.datapack.hellbound.AI.NPC.Budenka.Budenka;
import com.l2jserver.datapack.hellbound.AI.NPC.Buron.Buron;
import com.l2jserver.datapack.hellbound.AI.NPC.Deltuva.Deltuva;
import com.l2jserver.datapack.hellbound.AI.NPC.Falk.Falk;
import com.l2jserver.datapack.hellbound.AI.NPC.Hude.Hude;
import com.l2jserver.datapack.hellbound.AI.NPC.Jude.Jude;
import com.l2jserver.datapack.hellbound.AI.NPC.Kanaf.Kanaf;
import com.l2jserver.datapack.hellbound.AI.NPC.Kief.Kief;
import com.l2jserver.datapack.hellbound.AI.NPC.Natives.Natives;
import com.l2jserver.datapack.hellbound.AI.NPC.Quarry.Quarry;
import com.l2jserver.datapack.hellbound.AI.NPC.Shadai.Shadai;
import com.l2jserver.datapack.hellbound.AI.NPC.Solomon.Solomon;
import com.l2jserver.datapack.hellbound.AI.NPC.Warpgate.Warpgate;
import com.l2jserver.datapack.hellbound.AI.Zones.AnomicFoundry.AnomicFoundry;
import com.l2jserver.datapack.hellbound.AI.Zones.BaseTower.BaseTower;
import com.l2jserver.datapack.hellbound.AI.Zones.TowerOfInfinitum.TowerOfInfinitum;
import com.l2jserver.datapack.hellbound.AI.Zones.TowerOfNaia.TowerOfNaia;
import com.l2jserver.datapack.hellbound.AI.Zones.TullyWorkshop.TullyWorkshop;
import com.l2jserver.datapack.hellbound.Instances.DemonPrinceFloor.DemonPrinceFloor;
import com.l2jserver.datapack.hellbound.Instances.RankuFloor.RankuFloor;
import com.l2jserver.datapack.hellbound.Instances.UrbanArea.UrbanArea;
import com.l2jserver.datapack.quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import com.l2jserver.datapack.quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;
import com.l2jserver.gameserver.config.Config;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.IAdminCommandHandler;
import com.l2jserver.gameserver.handler.IVoicedCommandHandler;
import com.l2jserver.gameserver.handler.VoicedCommandHandler;

/**
 * Hellbound class-loader.
 * @author Zoey76
 */
public final class HellboundLoader
{
	private static final Logger _log = Logger.getLogger(HellboundLoader.class.getName());
	
	private static final Class<?>[] SCRIPTS =
	{
		// Commands
		AdminHellbound.class,
		Hellbound.class,
		// AIs
		Amaskari.class,
		Chimeras.class,
		DemonPrince.class,
		HellboundCore.class,
		Keltas.class,
		NaiaLock.class,
		OutpostCaptain.class,
		Ranku.class,
		Slaves.class,
		Typhoon.class,
		// NPCs
		Bernarde.class,
		Budenka.class,
		Buron.class,
		Deltuva.class,
		Falk.class,
		Hude.class,
		Jude.class,
		Kanaf.class,
		Kief.class,
		Natives.class,
		Quarry.class,
		Shadai.class,
		Solomon.class,
		Warpgate.class,
		// Zones
		AnomicFoundry.class,
		BaseTower.class,
		TowerOfInfinitum.class,
		TowerOfNaia.class,
		TullyWorkshop.class,
		// Instances
		DemonPrinceFloor.class,
		UrbanArea.class,
		RankuFloor.class,
		// Quests
		Q00130_PathToHellbound.class,
		Q00133_ThatsBloodyHot.class,
	};
	
	public static void main(String[] args)
	{
		_log.info(HellboundLoader.class.getSimpleName() + ": Loading Hellbound related scripts:");
		// Data
		HellboundPointData.getInstance();
		HellboundSpawns.getInstance();
		// Engine
		HellboundEngine.getInstance();
		for (Class<?> script : SCRIPTS)
		{
			try
			{
				final Object instance = script.getDeclaredConstructor().newInstance();
				if (instance instanceof IAdminCommandHandler)
				{
					AdminCommandHandler.getInstance().registerHandler((IAdminCommandHandler) instance);
				}
				else if (Config.L2JMOD_HELLBOUND_STATUS && (instance instanceof IVoicedCommandHandler))
				{
					VoicedCommandHandler.getInstance().registerHandler((IVoicedCommandHandler) instance);
				}
			}
			catch (Exception e)
			{
				_log.severe(HellboundLoader.class.getSimpleName() + ": Failed loading " + script.getSimpleName() + ":" + e.getMessage());
			}
		}
	}
}
