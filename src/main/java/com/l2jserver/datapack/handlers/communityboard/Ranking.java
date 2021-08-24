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
package com.l2jserver.datapack.handlers.communityboard;

import java.util.ArrayList;
import java.util.List;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.instancemanager.CastleManager;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Castle;
import com.l2jserver.gameserver.model.entity.capturetower.ITowerBehavior;
import com.l2jserver.gameserver.model.entity.capturetower.SinglePlayerBehavior;
import com.l2jserver.gameserver.util.Util;

/**
 * Region board.
 *
 * @author Zoey76
 */
public class Ranking implements IWriteBoardHandler {
	// Region data
	// @formatter:off
	private static final int[] REGIONS = { 1049, 1052, 1053, 1057, 1060, 1059, 1248, 1247, 1056 };
	// @formatter:on
	private static final String[] COMMANDS = {
		"ranking"
	};

	@Override public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}

	@Override public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {
		return BypassHandler.getInstance().getHandler("ranking;homepage").useBypass(command, activeChar, null);
	}

	@Override public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5) {
		// TODO: Implement.
		return false;
	}
}
