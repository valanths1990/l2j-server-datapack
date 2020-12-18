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
package com.l2jserver.datapack.handlers.targethandlers;

import static com.l2jserver.gameserver.model.skills.targets.AffectScope.SINGLE;
import static com.l2jserver.gameserver.model.skills.targets.L2TargetType.ENEMY_ONLY;
import static com.l2jserver.gameserver.model.zone.ZoneId.PVP;
import static com.l2jserver.gameserver.network.SystemMessageId.INCORRECT_TARGET;

import com.l2jserver.gameserver.handler.ITargetTypeHandler;
import com.l2jserver.gameserver.instancemanager.DuelManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.skills.targets.L2TargetType;

/**
 * Enemy Only target type handler.
 * @author Zoey76
 * @since 2.6.0.0
 */
public class EnemyOnly implements ITargetTypeHandler {
	@Override
	public L2Object[] getTargetList(Skill skill, L2Character activeChar, boolean onlyFirst, L2Character target) {
		if (skill.getAffectScope() != SINGLE) {
			return EMPTY_TARGET_LIST;
		}
		
		if (target == null) {
			return EMPTY_TARGET_LIST;
		}
		
		if (target.getObjectId() == activeChar.getObjectId()) {
			activeChar.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		if (target.isDead()) {
			activeChar.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		if (target.isNpc()) {
			if (target.isAttackable()) {
				return new L2Character[] {
					target
				};
			}
			activeChar.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		final var player = activeChar.getActingPlayer();
		if (player == null) {
			return EMPTY_TARGET_LIST;
		}
		
		// In Olympiad, different sides.
		if (player.isInOlympiadMode()) {
			final var targetPlayer = target.getActingPlayer();
			if ((targetPlayer != null) && (player.getOlympiadSide() != targetPlayer.getOlympiadSide())) {
				return new L2Character[] {
					target
				};
			}
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// In Duel, different sides.
		if (player.isInDuelWith(target)) {
			final var targetPlayer = target.getActingPlayer();
			final var duel = DuelManager.getInstance().getDuel(player.getDuelId());
			final var teamA = duel.getTeamA();
			final var teamB = duel.getTeamB();
			if (teamA.contains(player) && teamB.contains(targetPlayer) || //
				teamB.contains(player) && teamA.contains(targetPlayer)) {
				return new L2Character[] {
					target
				};
			}
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// Not in same party.
		if (player.isInPartyWith(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// In PVP Zone.
		if (player.isInsideZone(PVP)) {
			return new L2Character[] {
				target
			};
		}
		
		// Not in same clan.
		if (player.isInClanWith(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// TODO(Zoey76): Validate.
		// Not in same alliance.
		if (player.isInAllyWith(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// TODO(Zoey76): Validate.
		// Not in same command channel.
		if (player.isInCommandChannelWith(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// Not on same Siege Side.
		if (player.isOnSameSiegeSideWith(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		// At Clan War.
		if (player.isAtWarWith(target)) {
			return new L2Character[] {
				target
			};
		}
		
		// Cannot PvP.
		if (!player.checkIfPvP(target)) {
			player.sendPacket(INCORRECT_TARGET);
			return EMPTY_TARGET_LIST;
		}
		
		return new L2Character[] {
			target
		};
	}
	
	@Override
	public Enum<L2TargetType> getTargetType() {
		return ENEMY_ONLY;
	}
}
