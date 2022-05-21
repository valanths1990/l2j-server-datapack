package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.List;

public interface RequiresMiscItem {


    List<MiscItem> getMiscItems();

    default void handleMiscItems(Autobot player) {
        getMiscItems().forEach(item -> {
            if (player.getInventory().getItemByItemId(item.getMiscItemId().apply(player)) != null) {
                if (player.getInventory().getItemByItemId(item.getMiscItemId().apply(player)).getCount() <= item.getMinimumAmountBeforeGive()) {
                    player.getInventory().addItem("autobot", item.getMiscItemId().apply(player), item.getCountToGive(), player, null);
                }
            } else {
                L2ItemInstance i = player.getInventory().getItemByItemId(item.getMiscItemId().apply(player));
                if (i != null && i.isEquipable()) {
                    player.getInventory().equipItem(i);
                }
            }
        });
    }
}
