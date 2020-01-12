/*
 * Copyright © 2004-2020 L2J DataPack
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
package com.l2jserver.datapack.gracia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.gracia.AI.EnergySeeds;
import com.l2jserver.datapack.gracia.AI.Lindvior;
import com.l2jserver.datapack.gracia.AI.Maguen;
import com.l2jserver.datapack.gracia.AI.StarStones;
import com.l2jserver.datapack.gracia.AI.NPC.FortuneTelling.FortuneTelling;
import com.l2jserver.datapack.gracia.AI.NPC.GeneralDilios.GeneralDilios;
import com.l2jserver.datapack.gracia.AI.NPC.Lekon.Lekon;
import com.l2jserver.datapack.gracia.AI.NPC.Nemo.Nemo;
import com.l2jserver.datapack.gracia.AI.NPC.Nottingale.Nottingale;
import com.l2jserver.datapack.gracia.AI.NPC.Seyo.Seyo;
import com.l2jserver.datapack.gracia.AI.NPC.ZealotOfShilen.ZealotOfShilen;
import com.l2jserver.datapack.gracia.AI.SeedOfAnnihilation.SeedOfAnnihilation;
import com.l2jserver.datapack.gracia.instances.SecretArea.SecretArea;
import com.l2jserver.datapack.gracia.instances.SeedOfDestruction.Stage1;
import com.l2jserver.datapack.gracia.instances.SeedOfInfinity.HallOfSuffering.HallOfSuffering;
import com.l2jserver.datapack.gracia.vehicles.AirShipGludioGracia.AirShipGludioGracia;
import com.l2jserver.datapack.gracia.vehicles.KeucereusNorthController.KeucereusNorthController;
import com.l2jserver.datapack.gracia.vehicles.KeucereusSouthController.KeucereusSouthController;
import com.l2jserver.datapack.gracia.vehicles.SoDController.SoDController;
import com.l2jserver.datapack.gracia.vehicles.SoIController.SoIController;

/**
 * Gracia class-loader.
 * @author Pandragon
 */
public final class GraciaLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(GraciaLoader.class);
	
	private static final Class<?>[] SCRIPTS = {
		// AIs
		EnergySeeds.class,
		Lindvior.class,
		Maguen.class,
		StarStones.class,
		// NPCs
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		Seyo.class,
		ZealotOfShilen.class,
		// Seed of Annihilation
		SeedOfAnnihilation.class,
		// Instances
		SecretArea.class,
		Stage1.class, // Seed of Destruction
		HallOfSuffering.class, // Seed of Infinity
		// Vehicles
		AirShipGludioGracia.class,
		KeucereusNorthController.class,
		KeucereusSouthController.class,
		SoIController.class,
		SoDController.class,
	};
	
	public static void main(String[] args) {
		LOG.info("Loading Gracia scripts...");
		for (Class<?> script : SCRIPTS) {
			try {
				script.getDeclaredConstructor().newInstance();
			} catch (Exception ex) {
				LOG.error("Failed loading {}!", script.getSimpleName(), ex);
			}
		}
	}
}
