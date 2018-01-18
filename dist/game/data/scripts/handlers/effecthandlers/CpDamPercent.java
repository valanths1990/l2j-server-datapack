/*
 * Copyright (C) 2004-2018 L2J DataPack
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
package handlers.effecthandlers;

import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

/**
 * CP Damage Percent effect implementation.
 * @author Zoey76, Adry_85
 */
public final class CpDamPercent extends AbstractEffect
{
	private final double _power;
	
	public CpDamPercent(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params)
	{
		super(attachCond, applyCond, set, params);
		
		_power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void onStart(BuffInfo info)
	{
		if (!info.getEffected().isPlayer())
		{
			return;
		}
		
		final L2Character target = info.getEffected();
		if (target.isPlayer() && target.getActingPlayer().isFakeDeath())
		{
			target.stopFakeDeath(true);
		}
		
		double damage = (target.getCurrentCp() * _power) / 100;
		// Manage attack or cast break of the target (calculating rate, sending message)
		if (!target.isRaid() && Formulas.calcAtkBreak(target, damage))
		{
			target.breakAttack();
			target.breakCast();
		}
		
		if (damage > 0)
		{
			target.setCurrentCp(target.getCurrentCp() - damage);
		}
	}
}