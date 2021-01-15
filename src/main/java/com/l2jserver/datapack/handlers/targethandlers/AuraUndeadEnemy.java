/*
 * Copyright © 2004-2021 L2J DataPack
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
package com.l2jserver.datapack.handlers.targethandlers;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.TargetType;

/**
 * Aura Undead Enemy target handler implementation.
 * @author Adry_85
 * @since 2.6.0.0
 */
public class AuraUndeadEnemy implements ITargetTypeHandler {
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
		List<L2Character> targetList = new ArrayList<>();
		int maxTargets = skill.getAffectLimit();
		for (L2Character obj : activeChar.getKnownList().getKnownCharactersInRadius(skill.getAffectRange())) {
			if (obj.isAttackable() && obj.isUndead()) {
				if (onlyFirst) {
					return new L2Character[] {
						obj
					};
				}
				
				if ((maxTargets > 0) && (targetList.size() >= maxTargets)) {
					break;
				}
				
				targetList.add(obj);
			}
		}
		return targetList.toArray(EMPTY_TARGET_LIST);
	}
	
	@Override
	public Enum<TargetType> getTargetType() {
		return TargetType.AURA_UNDEAD_ENEMY;
	}
}
