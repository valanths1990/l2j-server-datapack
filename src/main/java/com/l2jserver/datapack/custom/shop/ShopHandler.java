package com.l2jserver.datapack.custom.shop;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.MultisellData;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.network.serverpackets.ExShowBaseAttributeCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jserver.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jserver.gameserver.network.serverpackets.HennaEquipList;
import com.l2jserver.gameserver.network.serverpackets.HennaRemoveList;
import com.l2jserver.gameserver.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class ShopHandler implements IBypassHandler {
	private String[] COMMANDS = { "shop", "shop;advanced", "shop;basic", "shop;adenashop", "shop;homepage", "shop;toparmor" };

	public ShopHandler() {
		MultisellData.getInstance().parseDatapackDirectory("data/custom/shop/custom", true);
	}

	@Override public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
//		MultisellData.getInstance().load();
//		MultisellData.getInstance().parseDatapackDirectory("data/custom/shop/custom", true);
		String[] splitted = command.split(" ");
		String currentPage = splitted[0].split(";")[1];
		if (splitted.length == 1) {
			openBoard(player, currentPage);
			return true;
		} else if (splitted.length == 2 && !splitted[1].matches("[0-9]+")) {
			switch (splitted[1]) {
				case "releaseAttribute" -> player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
				case "addHenna" -> player.sendPacket(new HennaEquipList(player));
				case "removeHenna" -> player.sendPacket(new HennaRemoveList(player));
				case "addAugment" -> player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
				case "removeAugment" -> player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
				case "combine" -> {
					final Map<Integer, L2ItemInstance> talismans = new HashMap<>();
					Set<Integer> talismanIds;
					talismanIds = Arrays.stream(player.getInventory().getItems()).filter(item -> item.getName().contains("Talisman")).map(L2ItemInstance::getId).collect(Collectors.toSet());
					try {
						Arrays.stream(player.getInventory().getItems())

							.filter(Objects::nonNull)

							.filter(L2ItemInstance::isShadowItem)

							.filter(i -> talismanIds.contains(i.getId())) // Filter that only talismans go through.

							.sorted((i1, i2) -> Boolean.compare(i2.isEquipped(), i1.isEquipped())) // Equipped talismans first (so we can then pick equipped first for charging).

							.forEach(item -> {
								final L2ItemInstance talismanToCharge = talismans.putIfAbsent(item.getId(), item);

								if ((talismanToCharge != null) && player.destroyItem("combine", item, player, false)) {

									talismanToCharge.decreaseMana(false, -item.getMana()); // Minus in decrease = increase :P
								}
							});
					} catch (Exception ignored) {

					}

				}

			}

		} else {
			buyItem(player, Integer.parseInt(splitted[1]));
		}

		openBoard(player, currentPage);
		return true;
	}

	private void openBoard(L2PcInstance player, String boardToOpen) {
		String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/shop/html/" + boardToOpen + ".html");
		CommunityBoardHandler.separateAndSend(html, player);
	}

	private void buyItem(L2PcInstance player, int multisellId) {
		if (multisellId >= 22 && multisellId <= 27) {
			MultisellData.getInstance().separateAndSend(multisellId, player, null, true);
		} else {
			MultisellData.getInstance().separateAndSend(multisellId, player, null, false);
		}
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
