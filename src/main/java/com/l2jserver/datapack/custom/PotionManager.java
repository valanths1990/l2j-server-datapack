package com.l2jserver.datapack.custom;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.impl.item.OnItemUse;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.AbnormalType;
import com.l2jserver.gameserver.network.serverpackets.ExAutoSoulShot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class PotionManager {

	private final int MANA_POTION_ID = 728;
	private final int HEALING_POTION_ID = 1539;
	private final int SOUL_POTION_ID = 10410;
	private final int CP_POTION_ID = 5592;

	private final Map<L2PcInstance, List<L2ItemInstance>> activePotions = new ConcurrentHashMap<>();

	public PotionManager() {

		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_ITEM_USE, this::receivedEvent, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGOUT, this::receivedEvent, this));
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::replenishPotions, 0, 500, TimeUnit.MILLISECONDS);
	}

	private void receivedEvent(IBaseEvent event) {

		switch (event.getType()) {
			case ON_ITEM_USE -> {
				OnItemUse itemUse = (OnItemUse) event;
				L2ItemInstance item = itemUse.getItem();
				L2PcInstance player = itemUse.getPlayer();
				if (!isPotion(item)) {
					return;
				}
				if (!activePotions.containsKey(player)) {
					activePotions.put(itemUse.getPlayer(), new CopyOnWriteArrayList<>());
				}
				if (activePotions.get(player).contains(item)) {
					player.sendPacket(new ExAutoSoulShot(item.getId(), 0));
					activePotions.get(player).remove(item);
				} else {
					player.sendPacket(new ExAutoSoulShot(item.getId(), 1));
					activePotions.get(player).add(item);
				}
			}
			case ON_PLAYER_LOGOUT -> {
				L2PcInstance player = ((OnPlayerLogout) event).getActiveChar();
				activePotions.remove(player);
			}

		}

	}

	private void replenishPotions() {

		activePotions.forEach((player, list) -> {
			list.forEach(item -> {
				if(player.getEffectList().getBuffInfoByAbnormalType(AbnormalType.INVINCIBILITY) !=null){
					return;
				}
				if (player.isInOlympiadMode()) {
					return;
				}
				if(player.isAlikeDead()){
					return;
				}
				if (player.getItemRemainingReuseTime(item.getObjectId()) > 0) {
					return;
				}
				boolean canUse = true;
				if (item.getId() == HEALING_POTION_ID && player.getMaxHp() * 0.95 < player.getCurrentHp()) {
					canUse = false;
				}
				if (item.getId() == MANA_POTION_ID && player.getMaxMp() * 0.95 < player.getCurrentMp()) {
					canUse = false;
				}
				if (item.getId() == CP_POTION_ID && player.getMaxCp() * 0.95 < player.getCurrentCp()) {
					canUse = false;
				}
				if (item.getId() == SOUL_POTION_ID && player.getChargedSouls() == 40) {
					canUse = false;
				}
				if (canUse) {
					IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
					if (handler != null) {
						if (!handler.useItem(player, item, false) && item.getCount() == 0) {
						activePotions.get(player).remove(item);
						}
					}
				}
			});
		});

	}

	private boolean isPotion(L2ItemInstance item) {
		switch (item.getId()) {
			case MANA_POTION_ID, CP_POTION_ID, HEALING_POTION_ID, SOUL_POTION_ID -> {
				return true;
			}
		}
		return false;
	}

	public static PotionManager getInstance() {
		return PotionManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final PotionManager instance = new PotionManager();
	}

	public static void main(String[] args)  {
		PotionManager.getInstance();
	}

}
