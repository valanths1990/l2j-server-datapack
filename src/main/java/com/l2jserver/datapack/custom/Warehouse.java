package com.l2jserver.datapack.custom;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.ClanPrivilege;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.SortedWareHouseWithdrawalList;
import com.l2jserver.gameserver.network.serverpackets.WareHouseDepositList;
import com.l2jserver.gameserver.network.serverpackets.WareHouseWithdrawalList;

import static com.l2jserver.gameserver.config.Configuration.general;

public class Warehouse implements IBypassHandler {
	private static String[] COMMAND = { "warehouse;homepage" };

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String[] splitted = command.split(" ");
		if (splitted.length == 1) {
			CommunityBoardHandler.separateAndSend(HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/custom/warehouse/warehouse.html"), activeChar);
			return true;
		}

		if (splitted.length == 3) {
			if (activeChar.isEnchanting()) {
				return false;
			}
			activeChar.setInventoryBlockingStatus(true);
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			if (splitted[1].equals("private")) {
				activeChar.setActiveWarehouse(activeChar.getWarehouse());

				if (splitted[2].equals("deposit")) {
					activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.PRIVATE));
				}
				if (splitted[2].equals("withdraw")) {
					if (activeChar.getActiveWarehouse().getSize() == 0) {
						activeChar.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
						return false;
					}
					showWithdrawWindow(activeChar, SortedWareHouseWithdrawalList.WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z, WareHouseWithdrawalList.PRIVATE);
				}
				return true;
			}
			if (splitted[1].equals("clan")) {
				if (activeChar.getClan() == null) {
					activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
					return false;
				}

				if (activeChar.getClan().getLevel() == 0) {
					activeChar.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
					return false;
				}
				if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE)) {
					activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
					return false;
				}
				activeChar.setActiveWarehouse(activeChar.getClan().getWarehouse());

				if (splitted[2].equals("deposit")) {
					activeChar.sendPacket(new WareHouseDepositList(activeChar, WareHouseDepositList.CLAN));
				}
				if (splitted[2].equals("withdraw")) {
					if (activeChar.getActiveWarehouse().getSize() == 0) {
						activeChar.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
						return false;
					}
					showWithdrawWindow(activeChar, SortedWareHouseWithdrawalList.WarehouseListType.ALL, SortedWareHouseWithdrawalList.A2Z, WareHouseWithdrawalList.CLAN);
				}
			}
		}
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);

		return true;
	}

	private static void showWithdrawWindow(L2PcInstance player, SortedWareHouseWithdrawalList.WarehouseListType itemtype, byte sortorder, int withdrawelType) {

		if (itemtype != null) {
			player.sendPacket(new SortedWareHouseWithdrawalList(player, withdrawelType, itemtype, sortorder));
		} else {
			player.sendPacket(new WareHouseWithdrawalList(player, withdrawelType));
		}
		if (general().debug()) {
			_log.fine("Source: L2WarehouseInstance.java; Player: " + player.getName() + "; Command: showRetrieveWindow; Message: Showing stored items.");
		}
	}

	@Override public String[] getBypassList() {
		return COMMAND;
	}
}
