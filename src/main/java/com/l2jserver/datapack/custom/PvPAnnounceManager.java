package com.l2jserver.datapack.custom;

import com.l2jserver.datapack.eventengine.datatables.MessageData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.SystemMessage;
import com.l2jserver.gameserver.util.Broadcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class PvPAnnounceManager {

	private final Map<L2PcInstance, PvPCountHolder> playersPvps = new ConcurrentHashMap<>();

	private enum KillingAnnouncements {

		DoubleKill(2, "- Double kill!"), TripleKill(3, "- Triple kill!"), Quadrakill(4, "- Quadra kill!"), PentaKill(5, "- Penta kill!"), Legendary(6, "is Legendary!!"), Dominatig(7, "is Dominating!!"), Unstoppable(8, "is Unstoppable!!"), Godlike(9, "is Godlike!!!!"), AbsoluteMadLad(10, "is Absolute Mad Lad!!!");

		private final int number;
		private final String text;

		KillingAnnouncements(int number, String text) {
			this.number = number;
			this.text = text;
		}

		public int getNumber() {
			return this.number;
		}

		public String getText() {
			return this.text;
		}

		public static KillingAnnouncements getKillingAnnouncementByNumber(int number) {
			return Stream.of(values()).filter(k -> k.getNumber() == number).findFirst().orElse(AbsoluteMadLad);
		}
	}

	private PvPAnnounceManager() {
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, this::onPlayerPvpKill, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGOUT, event -> playersPvps.remove(((OnPlayerLogout) event).getActiveChar()), this));
	}

	private void onPlayerPvpKill(IBaseEvent event) {
		OnPlayerPvPKill onPvpKill = (OnPlayerPvPKill) event;
		L2PcInstance player = onPvpKill.getActiveChar();
		playersPvps.putIfAbsent(player, new PvPCountHolder(player));

		if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - playersPvps.get(player).lastUpdate) > 30) {
			playersPvps.get(player).pvpCount = 0;
		}

		PvPCountHolder holder = playersPvps.get(player);
		holder.pvpCount++;
		holder.lastUpdate = System.currentTimeMillis();
		announce(holder);
	}

	private void announce(PvPCountHolder holder) {

		if (holder.pvpCount <= 1) {
			return;
		}
		KillingAnnouncements announcements = KillingAnnouncements.getKillingAnnouncementByNumber(holder.pvpCount);
		SystemMessage smg = SystemMessage.getSystemMessage(SystemMessageId.C1_KILL_ANNOUNCEMENT_S2);
		smg.addPcName(holder.player);
		smg.addString(announcements.getText());
		Broadcast.toAllOnlinePlayers(smg);
	}

	private static final class PvPCountHolder {
		int pvpCount = 0;
		long lastUpdate = System.currentTimeMillis();
		L2PcInstance player;

		PvPCountHolder(L2PcInstance player) {
			this.player = player;
		}
	}

	public static PvPAnnounceManager getInstance() {
		return PvPAnnounceManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final PvPAnnounceManager instance = new PvPAnnounceManager();
	}

	public static void main(String[] args) {
		PvPAnnounceManager.getInstance();
	}

}
