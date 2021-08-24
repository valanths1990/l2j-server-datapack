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

import com.l2jserver.datapack.handlers.itemhandlers.Bypass;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IWriteBoardHandler;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.util.StringUtil;
import com.l2jserver.gameserver.util.Util;

/**
 * Clan board.
 *
 * @author Zoey76
 */
public class Clan implements IWriteBoardHandler {
	private static final String[] COMMANDS = {
		"clan"
	};

	@Override public String[] getCommunityBoardCommands() {
		return COMMANDS;
	}

	@Override public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar) {

		return BypassHandler.getInstance().getHandler("clan;homepage").useBypass(command, activeChar, null);
	}

	@Override
	public boolean writeCommunityBoardCommand(L2PcInstance activeChar, String arg1, String arg2, String arg3, String arg4, String arg5) {
		return true;
	}
}
