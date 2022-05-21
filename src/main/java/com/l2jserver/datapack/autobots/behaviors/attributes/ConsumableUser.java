package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.items.L2EtcItem;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.ArrayList;
import java.util.List;

public interface ConsumableUser {
    List<Consumable> consumable = new ArrayList<>();

    default void handleConsumables(Autobot player) {
        if (consumable.isEmpty()) {
            return;
        }
        consumable.stream().filter(c -> c.getCondition().apply(player)).forEach(c -> {
            List<L2ItemInstance> items = player.getInventory().getItemsByItemId(c.getConsumableId());
            if (items.isEmpty()) {
                player.getInventory().addItem("AubotoItem", c.getConsumableId(), 200, player, false);
            }
            L2ItemInstance item = player.getInventory().getItemByItemId(c.getConsumableId());
            if (item.getItem().hasExImmediateEffect()) {
                final IItemHandler handler = ItemHandler.getInstance().getHandler(item.getItem() instanceof L2EtcItem ? (L2EtcItem) item.getItem() : null);
                if (handler != null) {
                    handler.useItem(player, new L2ItemInstance(item.getId()), false);
                }
            }
        });
    }

}
