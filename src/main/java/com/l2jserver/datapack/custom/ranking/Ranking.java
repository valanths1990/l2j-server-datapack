package com.l2jserver.datapack.custom.ranking;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.custom.reward.RewardManager;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.L2Clan;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.*;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanCreate;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.OnPlayerClanDestroy;
import com.l2jserver.gameserver.model.events.impl.clan.OnClanReputationChanged;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;

import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Ranking implements IBypassHandler {
	private final String[] COMMANDS = {
		"ranking;homepage",
	};
	private final Integer refreshTimeInSeconds = Configuration.customs().getRankingBoardRefreshTime();
	private final String firstPlaceColor = "d90b0b";
	private final String secondPlaceColor = "910da8";
	private final String thirdPlaceColor = "0da81a";
	private final String requestingPlayersColor = "2e7299";
	private final String[] circularRowColor = {
		"f78c8a", "f2dddc"
	};
	private final String placeholderForWinners = "<table border=0 bgcolor=\"%tableColor%\" width=250 id=generatedtable><tr><td align=center width=30><font name=\"hs9\" color=\"%rowColor%\">%position%</font></td><td align=center width=70><font name=\"hs9\" color=\"%rowColor%\">%player%</font></td><td align=center width=30><font name=\"hs9\" color=\"%rowColor%\">%pvp%</font></td></tr></table>";
	private final String normalPlaceHolder = "<table border=0 bgcolor=\"%tableColor%\" width=250><tr><td align=center width=30>%position%</td><td align=center width=70>%player%</td><td align=center width=30>%pvp%</td></tr></table>";
	private final String sqlSelect = "SELECT id,name,type,rankingtype,count FROM ranking";
	private final String sqlUpdateOrInsert = "INSERT INTO ranking(id,name,type,rankingtype,count) values ( ? , ? , ? , ? ,? ) ON DUPLICATE KEY UPDATE count = ? ";
	private final String sqlDelete = "DELETE FROM ranking WHERE id = ?";
	Map<RankingInfo.RankingType, List<RankingInfo>> rankingMap = new ConcurrentHashMap<>();
	private final Map<String, String> htmlTables = new ConcurrentHashMap<>();

	public Ranking() {
		Arrays.stream(RankingInfo.RankingType.values()).forEach(type -> rankingMap.put(type, new CopyOnWriteArrayList<>()));
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::refreshHtmlTables, 0, refreshTimeInSeconds, TimeUnit.SECONDS);
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, this::receivedEvents, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_ASSIST_KILL, this::receivedEvents, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PK_KILL, this::receivedEvents, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_CLAN_REPUTATION_CHANGED, this::receivedEvents, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_DELETE, this::receivedEvents, this));
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_GAME_SHUTDOWN, this::receivedEvents, this));
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CREATE, this::receivedEvents, this));
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CLAN_CREATE, this::receivedEvents, this));
		Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CLAN_DESTROY, this::receivedEvents, this));
		load();
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::weeklyRewards, getRemainingTimeForWeeklyReset() + 1000, getRemainingTimeForWeeklyReset(), TimeUnit.MILLISECONDS);
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::monthlyRewards, getRemainingTimeForMonthlyReset() + 1000, getRemainingTimeForMonthlyReset(), TimeUnit.MILLISECONDS);
	}

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String[] splitted = command.split(" ");
		String currentPage = splitted[0].split(";")[1];
		if (splitted.length == 1) {
			openBoard(activeChar, currentPage);
			return true;
		}
		openBoard(activeChar, splitted[1]);
		return false;
	}

	private void openBoard(L2PcInstance player, String boardToOpen) {
		String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/ranking/" + boardToOpen + ".html");
		if (!boardToOpen.equals("homepage")) {
			html = html.replace("%alltimeRankingTable%", htmlTables.get("alltime" + boardToOpen)).replace("%weeklyRankingTable%", htmlTables.get("weekly" + boardToOpen)).replace("%monthlyRankingTable%", htmlTables.get("monthly" + boardToOpen));
		}
		CommunityBoardHandler.separateAndSend(html, player);
	}

	private void receivedEvents(IBaseEvent event) {

		switch (event.getType()) {
			case ON_PLAYER_PVP_KILL -> {
				OnPlayerPvPKill pvpEvent = (OnPlayerPvPKill) event;
				rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("PVP")).flatMap(entry -> entry.getValue().stream().filter(r -> r.getId() == pvpEvent.getActiveChar().getObjectId())).forEach(r -> r.setCount(r.getCount() + 1));
			}
			case ON_PLAYER_PK_KILL -> {
				OnPlayerPkKill pkEvent = (OnPlayerPkKill) event;
				rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("PK")).flatMap(entry -> entry.getValue().stream().filter(r -> r.getId() == pkEvent.getActiveChar().getObjectId())).forEach(r -> r.setCount(r.getCount() + 1));
			}
			case ON_PLAYER_ASSIST_KILL -> {
				OnPlayerAssistKill assistEvent = (OnPlayerAssistKill) event;
				rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("ASSIST")).flatMap(entry -> entry.getValue().stream().filter(r -> r.getId() == assistEvent.getActiveChar().getObjectId())).forEach(r -> r.setCount(r.getCount() + 1));
			}
			case ON_CLAN_REPUTATION_CHANGED -> {
				OnClanReputationChanged clanEvent = (OnClanReputationChanged) event;
				rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("SCORE")).flatMap(entry -> entry.getValue().stream().filter(r -> r.getId() == clanEvent.getClan().getId())).forEach(r -> r.setCount(r.getCount() + 1));
			}
			case ON_PLAYER_CREATE -> {
				OnPlayerCreate createEvent = (OnPlayerCreate) event;
				rankingMap.entrySet().stream().filter(entry -> !entry.getKey().name().contains("SCORE")).forEach(entry -> entry.getValue().add(new RankingInfo(createEvent.getObjectId(), createEvent.getName(), RankingInfo.Type.PLAYER, entry.getKey(), 0)));
			}
			case ON_PLAYER_CLAN_CREATE -> {
				OnPlayerClanCreate clanCreateEvent = (OnPlayerClanCreate) event;
				rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("SCORE")).forEach(entry -> entry.getValue().add(new RankingInfo(clanCreateEvent.getClan().getId(), clanCreateEvent.getClan().getName(), RankingInfo.Type.CLAN, entry.getKey(), clanCreateEvent.getClan().getReputationScore())));
			}
			case ON_PLAYER_CLAN_DESTROY -> {
				int id = ((OnPlayerClanDestroy) event).getClan().getId();
				delete(id);
			}
			case ON_PLAYER_DELETE -> {
				int id = ((OnPlayerDelete) event).getObjectId();
				delete(id);
			}
			case ON_GAME_SHUTDOWN -> save();
		}
	}

	private void refreshHtmlTables() {
		rankingMap.forEach((key, list) -> {
			String htmlKey = key.name().toLowerCase();
			List<RankingInfo> top10Rankings = list.stream().sorted(Comparator.comparingInt(RankingInfo::getCount)).limit(10).collect(Collectors.toList());
			String createdTable = createTable(top10Rankings);
			htmlTables.put(htmlKey, createdTable);
		});

	}

	private String createTable(List<RankingInfo> rankings) {
		StringBuilder str = new StringBuilder();
		AtomicInteger colorIndexing = new AtomicInteger(0);
		AtomicInteger position = new AtomicInteger(1);

		rankings.forEach(r -> {
			colorIndexing.set((colorIndexing.get() % (circularRowColor.length * 2)) % circularRowColor.length);
			if (position.get() == 1) {
				str.append(placeholderForWinners.replace("%tableColor%", circularRowColor[colorIndexing.get()]).replaceAll("%rowColor%", firstPlaceColor).replace("%position%", String.valueOf(position.get())).replace("%player%", r.getName()).replace("%pvp%", String.valueOf(r.getCount())));
			} else if (position.get() == 2) {
				str.append(placeholderForWinners.replace("%tableColor%", circularRowColor[colorIndexing.get()]).replaceAll("%rowColor%", secondPlaceColor).replace("%position%", String.valueOf(position.get())).replace("%player%", r.getName()).replace("%pvp%", String.valueOf(r.getCount())));
			} else if (position.get() == 3) {
				str.append(placeholderForWinners.replace("%tableColor%", circularRowColor[colorIndexing.get()]).replaceAll("%rowColor%", thirdPlaceColor).replace("%position%", String.valueOf(position.get())).replace("%player%", r.getName()).replace("%pvp%", String.valueOf(r.getCount())));
			} else {
				str.append(normalPlaceHolder.replace("%tableColor%", circularRowColor[colorIndexing.get()]).replace("%position%", String.valueOf(position.get())).replace("%player%", r.getName()).replace("%pvp%", String.valueOf(r.getCount())));
			}
			colorIndexing.incrementAndGet();
			position.incrementAndGet();
		});
		return str.toString();
	}

	private void load() {
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {
			try (PreparedStatement st = con.prepareStatement(sqlSelect)) {
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					RankingInfo.RankingType rankingType = RankingInfo.RankingType.valueOf(rs.getString("rankingtype"));
					RankingInfo.Type type = RankingInfo.Type.valueOf(rs.getString("type"));
					RankingInfo info = new RankingInfo(rs.getInt("id"), rs.getString("name"), type, rankingType, rs.getInt("count"));
					rankingMap.get(rankingType).add(info);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void delete(int id) {
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {
			try (PreparedStatement st = con.prepareStatement(sqlDelete)) {
				st.setInt(1, id);
				st.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		try (Connection con = ConnectionFactory.getInstance().getConnection()) {

			try (PreparedStatement st = con.prepareStatement(sqlUpdateOrInsert)) {
				rankingMap.forEach((key, value) -> value.forEach(r -> {
					try {
						st.setInt(1, r.getId());
						st.setString(2, r.getName());
						st.setString(3, r.getType().name());
						st.setString(4, r.getRankingType().name());
						st.setInt(5, r.getCount());
						st.setInt(6, r.getCount());
						st.addBatch();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}));
				st.executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void weeklyRewards() {
		List<RankingInfo> weeklyRewardsForPlayers = rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("WEEKLY") && !entry.getKey().name().contains("SCORE")).flatMap(entry -> entry.getValue().stream().sorted(Comparator.comparingInt(RankingInfo::getCount)).limit(10)).collect(Collectors.toList());
		weeklyRewardsForPlayers.forEach(r -> RewardManager.getInstance().rewardPlayerWithMail(r.getId(), "Ranking:weekly", "Weekly Reward For Top 10 Player", "Here is your Reward for beeing under the Top 10 in Ranking "));
		List<RankingInfo> weeklyRewardsForClanLeaders = rankingMap.entrySet().stream().filter(entry -> entry.getKey() == RankingInfo.RankingType.MONTHLYSCORE).flatMap(entry -> entry.getValue().stream().sorted(Comparator.comparingInt(RankingInfo::getCount)).limit(10)).collect(Collectors.toList());
		weeklyRewardsForClanLeaders.forEach(r -> {
			L2Clan clan = ClanTable.getInstance().getClan(r.getId());
			if (clan == null) {
				return;
			}
			int leaderId = clan.getLeaderId();
			RewardManager.getInstance().rewardPlayerWithMail(leaderId, "ranking:weekly", "Weekly Reward For Top 10 Player", "Here is your Reward for beeing under the Top 10 in Ranking ");
		});
	}

	private void monthlyRewards() {
		List<RankingInfo> weeklyRewardsForPlayers = rankingMap.entrySet().stream().filter(entry -> entry.getKey().name().contains("MONTHLY") && !entry.getKey().name().contains("SCORE")).flatMap(entry -> entry.getValue().stream().sorted(Comparator.comparingInt(RankingInfo::getCount)).limit(10)).collect(Collectors.toList());
		weeklyRewardsForPlayers.forEach(r -> RewardManager.getInstance().rewardPlayerWithMail(r.getId(), "ranking:monthly", "Weekly Reward For Top 10 Player", "Here is your Reward for beeing under the Top 10 in Ranking "));
		List<RankingInfo> weeklyRewardsForClanLeaders = rankingMap.entrySet().stream().filter(entry -> entry.getKey() == RankingInfo.RankingType.MONTHLYSCORE).flatMap(entry -> entry.getValue().stream().sorted(Comparator.comparingInt(RankingInfo::getCount)).limit(10)).collect(Collectors.toList());
		weeklyRewardsForClanLeaders.forEach(r -> {
			L2Clan clan = ClanTable.getInstance().getClan(r.getId());
			if (clan == null) {
				return;
			}
			int leaderId = clan.getLeaderId();
			RewardManager.getInstance().rewardPlayerWithMail(leaderId, "Ranking:weekly", "Weekly Reward For Top 10 Player", "Here is your Reward for beeing under the Top 10 in Ranking ");
		});
	}

	private long getRemainingTimeForMonthlyReset() {
		LocalDate endOfMonth = LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()).plusMonths(1);
		LocalDateTime endOfDay = endOfMonth.atTime(LocalTime.MAX);
		long epoch = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return epoch - System.currentTimeMillis();
	}

	private long getRemainingTimeForWeeklyReset() {
		LocalDate endOfWeek = LocalDate.now(ZoneId.systemDefault()).with(DayOfWeek.SUNDAY);
		LocalDateTime endOfDay = endOfWeek.atTime(LocalTime.MAX);
		long epoch = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return epoch - System.currentTimeMillis();
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
