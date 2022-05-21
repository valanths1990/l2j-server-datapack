package com.l2jserver.datapack.autobots.behaviors.sequences;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.preferences.SocialPreferences;
import com.l2jserver.datapack.autobots.utils.CancellationToken;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.TradeItem;
import com.l2jserver.gameserver.model.TradeList;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TradingSequence implements Sequence {
    private final Autobot player;

    private CancellationToken cancellationToken;
    private boolean usedQuestionMark = false;
    private final long maxWaitMilliseconds = 60000;
    private final AtomicLong waitedMilliseconds = new AtomicLong(0);

    public TradingSequence(Autobot player) {
        this.player = player;
    }

    @Override
    public void definition() {
        if (player.getSocialBehavior().getSocialPreferences().tradingAction == null) {
            player.cancelActiveTrade();
            return;
        }
        ensureSufficientTradeItems();
        if (player.hasActiveTrade()) {
            if (!usedQuestionMark && player.getActiveTradeList() != null && player.getActiveTradeList().getItems().length == 0) {
                Util.sleep(2000);
                player.say("?");
                usedQuestionMark = true;
                Util.sleep(3000);
            }
            while (player.getActiveTradeList() != null
                    && !requestedItemsAreSufficient(player.getActiveTradeList().getPartner().getActiveTradeList())) {
                Util.sleep(1000);
                waitedMilliseconds.set(waitedMilliseconds.get() + 1000);
                if (waitedMilliseconds.get() >= maxWaitMilliseconds) {
                    player.cancelActiveTrade();
                    Util.sleep(1000);
                    player.say("bb");
                    return;
                }
                addedWhatIOffer();
                if (player.getActiveTradeList() != null && player.getActiveTradeList().getPartner().getActiveTradeList() != null
                        && !requestedItemsAreSufficient(player.getActiveTradeList().getPartner().getActiveTradeList())
                        && player.getActiveTradeList().getPartner().getActiveTradeList().isConfirmed()) {
                    Util.sleep(2000);
                    player.say("sorry more");
                    Util.sleep(1000);
                    player.cancelActiveTrade();
                }
            }
            if (player.getActiveTradeList() != null) {
                Util.sleep(Rnd.get(2000, 4000));
                player.getActiveTradeList().confirm();
            }
        }
    }

    private boolean addedWhatIOffer() {
        List<SocialPreferences.TradingItem> itemsIOffer = player.getSocialBehavior().getSocialPreferences().tradingAction.offersItems;
        List<SocialPreferences.TradingItem> itemsIWant = player.getSocialBehavior().getSocialPreferences().tradingAction.offersItems;
        SocialPreferences.TradingItem itemIWant = itemsIWant == null || itemsIWant.isEmpty() ? null : itemsIWant.get(Rnd.get(itemsIWant.size()));
        if (itemsIOffer == null) return false;
        if (player.getActiveTradeList() == null) return false;
        return itemsIOffer.stream().anyMatch(itemIOffer -> {
            List<TradeItem> items = Arrays.stream(player.getActiveTradeList().getItems()).filter(i -> i.getItem().getId() == itemIOffer.itemId).collect(Collectors.toList());
            if (items.isEmpty()) {
                L2ItemInstance[] itemsToOffer = player.getInventory().getAllItemsByItemId(itemIOffer.itemId);
                if (itemsToOffer == null || itemsToOffer.length == 0) {
                    player.addItem("bot trading", itemIOffer.itemId, itemIOffer.itemCount, null, false);
                } else {
                    itemsIOffer.forEach(itemCheckCount -> {
                        if (itemCheckCount.itemCount < itemIOffer.itemCount) {
                            player.addItem("bot trading", itemIOffer.itemCount - itemCheckCount.itemCount, null, false);
                        }
                    });
                }
                if (itemsToOffer == null) return false;
                boolean wasSuccessful = Arrays.stream(itemsToOffer).anyMatch(singleItem -> {
                    if (player.getActiveTradeList() == null) return false;
                    player.addTradeItem(singleItem);
                    Util.sleep(1000);
                    return true;
                });
                if (!wasSuccessful) return false;
                Util.sleep(2000);
                if (itemIWant != null) {
                    L2Item item = ItemTable.getInstance().getTemplate(itemIWant.itemId);
                    player.say("for " + item.getName().toLowerCase());
                }
            }
            return true;
        });
        //TODO
    }

    private boolean requestedItemsAreSufficient(TradeList activeTradeList) {
        List<SocialPreferences.TradingItem> looksForItems = player.getSocialBehavior().getSocialPreferences().tradingAction.looksForItems;
        if (looksForItems == null) return false;
        return looksForItems.stream().anyMatch(tradingItem -> {
            List<TradeItem> itemInTradeList = Arrays.stream(activeTradeList.getItems()).filter(i -> i.getItem().getId() == tradingItem.itemId).collect(Collectors.toList());
            if (itemInTradeList.isEmpty()) return false;
            long count = itemInTradeList.stream().mapToLong(TradeItem::getCount).sum();
            return count >= tradingItem.itemCount;
        });
    }

    private void ensureSufficientTradeItems() {
        List<SocialPreferences.TradingItem> looksForItems = player.getSocialBehavior().getSocialPreferences().tradingAction.offersItems;
        if (looksForItems == null) return;
        looksForItems.forEach(singleItem -> {
            L2ItemInstance item = player.getInventory().getItemByItemId(singleItem.itemId);
            if (item == null) {
                player.addItem("bot trading", singleItem.itemId, singleItem.itemCount, null, false);
            } else if (item.getCount() < singleItem.itemCount) {
                player.addItem("bot trading", singleItem.itemId, singleItem.itemCount - item.getCount(), null, false);
            }
        });
    }

    @Override
    public Autobot getPlayer() {
        return this.player;
    }

    @Override
    public CancellationToken getCancellationToken() {
        return cancellationToken;
    }

    @Override
    public void setCancellationToken(CancellationToken cancellationToken) {
        this.cancellationToken = cancellationToken;
    }


}
