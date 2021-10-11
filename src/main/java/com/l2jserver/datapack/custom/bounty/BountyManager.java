package com.l2jserver.datapack.custom.bounty;

import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.custom.bounty.model.BountyHolder;
import com.l2jserver.datapack.custom.bounty.pojo.*;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureKill;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureZoneExit;
import com.l2jserver.gameserver.model.events.impl.character.player.*;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.events.listeners.FunctionEventListener;
import com.l2jserver.gameserver.model.events.returns.TerminateReturn;
import com.l2jserver.gameserver.model.zone.ZoneId;

public class BountyManager {

    private Bounties bounties;
    private final Map<Integer, Map<String, Bounty>> playersBounties = new ConcurrentHashMap<>();
    private final Map<Integer, String> storeTitle = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> storeTitleColor = new ConcurrentHashMap<>();
    private long updateIntervalInSeconds = 30;

    private final String sqlInsert = "INSERT INTO bounty(charId,pvpBounty,pkBounty,assistBounty) VALUES (?,?,?,?)";
    private final String sqlUpdate = "UPDATE bounty SET pvpBounty = ? , pkBounty = ?, assistBounty = ? WHERE charId = ?";
    private final String sqlDelete = "DELETE FROM bounty WHERE charId = ?";
    private final String sqlSelect = "SELECT charId, pvpBounty,pkBounty,assistBounty FROM bounty WHERE charId = ?";


    private BountyManager() {
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, this::onPlayerLogin, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_CREATURE_ZONE_ENTER, this::onPlayerEnterZone, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_CREATURE_ZONE_EXIT, this::onPlayerExitZone, this));

        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new UpdateDatabase(), 0, updateIntervalInSeconds, TimeUnit.SECONDS);
        load();
    }

    public Map<Integer, Map<String, Bounty>> getPlayersBounties() {
        return Collections.unmodifiableMap(playersBounties);
    }

    private void load() {
        try {
            File f = new File(Configuration.server().getDatapackRoot() + "/data/custom/bounty/bountyConfig.json");
            bounties = Json.fromJson(f, Bounties.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEventsOnPlayer(L2PcInstance pc) {
        pc.addListener(new ConsumerEventListener(pc, EventType.ON_PLAYER_PVP_KILL, this::onPlayerPvpKill, this));
        pc.addListener(new ConsumerEventListener(pc, EventType.ON_PLAYER_PK_KILL, this::onPlayerPkKill, this));
        pc.addListener(new ConsumerEventListener(pc, EventType.ON_PLAYER_ASSIST_KILL, this::onPlayerAssistKill, this));
        pc.addListener(new ConsumerEventListener(pc, EventType.ON_PLAYER_LOGOUT, this::onPlayerLogout, this));
        pc.addListener(new ConsumerEventListener(pc, EventType.ON_CREATURE_KILL, this::onPlayerDie, this));
        pc.addListener(new FunctionEventListener(pc, EventType.ON_PLAYER_TITLE_CHANGE, this::onPlayerTitleChange, this));
    }

    public TerminateReturn onPlayerTitleChange(IBaseEvent event) {
        OnPlayerTitleChange titleEvent = (OnPlayerTitleChange) event;
        if (!playersBounties.containsKey(titleEvent.getPlayer().getObjectId())) {
            return null;
        }
        if (!titleEvent.getPlayer().isInsideZone(ZoneId.PVP)) {
            return null;
        }
        return new TerminateReturn(true, true, true);
    }

    private void onPlayerPvpKill(IBaseEvent event) {
        L2PcInstance player = ((OnPlayerPvPKill) event).getActiveChar();
        if (!player.isInsideZone(ZoneId.PVP)) {
            return;
        }
        playersBounties.computeIfAbsent(player.getObjectId(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent("pvp", k -> new PvpBounty((PvpBounty) bounties.getBounties().get("pvp"))).increaseBounty();
        setTitleAndColor(player);
    }

    private void onPlayerPkKill(IBaseEvent event) {
        L2PcInstance player = ((OnPlayerPkKill) event).getActiveChar();
        playersBounties.computeIfAbsent(player.getObjectId(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent("pk", k -> new PkBounty((PkBounty) bounties.getBounties().get("pk"))).increaseBounty();
    }

    private void onPlayerAssistKill(IBaseEvent event) {
        L2PcInstance player = ((OnPlayerAssistKill) event).getActiveChar();
        if (!player.isInsideZone(ZoneId.PVP)) {
            return;
        }
        playersBounties.computeIfAbsent(player.getObjectId(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent("assist", k -> new AssistBounty((AssistBounty) bounties.getBounties().get("assist"))).increaseBounty();
    }

    private void onPlayerLogin(IBaseEvent event) {
        L2PcInstance player = ((OnPlayerLogin) event).getActiveChar();
        registerEventsOnPlayer(player);
        BountyHolder holder = getBounty(player.getObjectId());
        if (holder == null) {
            return;
        }
        playersBounties.computeIfAbsent(player.getObjectId(), k -> new ConcurrentHashMap<>());
        Bounty b;
        if (holder.getPvpBounty() > 0) {
            b = new PvpBounty((PvpBounty) bounties.getBounties().get("pvp"));
            b.setBounty(holder.getPvpBounty());
            playersBounties.get(player.getObjectId()).put("pvp", b);
        }
        if (holder.getPkBounty() > 0) {
            b = new PkBounty((PkBounty) bounties.getBounties().get("pk"));
            b.setBounty(holder.getPvpBounty());
            playersBounties.get(player.getObjectId()).put("pk", b);
        }
        if (holder.getAssistBounty() > 0) {
            b = new AssistBounty((AssistBounty) bounties.getBounties().get("assist"));
            b.setBounty(holder.getPvpBounty());
            playersBounties.get(player.getObjectId()).put("assist", b);
        }
            if(player.isInsideZone(ZoneId.PVP)){
                setTitleAndColor(player);
            }
    }

    private void onPlayerLogout(IBaseEvent event) {
        L2PcInstance player = ((OnPlayerLogout) event).getActiveChar();
        if (!playersBounties.containsKey(player.getObjectId())) {
            return;
        }
        restoreTitleAndColor(player);
        BountyHolder b = new BountyHolder();
        b.setCharId(player.getObjectId());
        playersBounties.get(player.getObjectId()).forEach((type, bounty) -> {
            if (type.equals("pvp")) {
                b.setPvpBounty(bounty.getCurrentBounty());
            }
            if (type.equals("pk")) {
                b.setPkBounty(bounty.getCurrentBounty());
            }
            if (type.equals("assist")) {
                b.setAssistBounty(bounty.getCurrentBounty());
            }
        });
        if (getBounty(player.getObjectId()) != null) {
            updateBounty(b);
        } else {
            insertBounty(b);
        }
        playersBounties.remove(player.getObjectId());
    }

    private void onPlayerDie(IBaseEvent event) {
        OnCreatureKill killEvent = (OnCreatureKill) event;
        if (!(killEvent.getTarget() instanceof L2PcInstance) || !(killEvent.getAttacker() instanceof L2PcInstance)) {
            return;
        }
        L2PcInstance target = (L2PcInstance) killEvent.getTarget();
        L2PcInstance player = (L2PcInstance) killEvent.getAttacker();
        if (!playersBounties.containsKey(target.getObjectId())) {
            return;
        }
        ThreadPoolManager.getInstance().scheduleGeneral(new RewardPlayer(new ArrayList<>(playersBounties
                .get(target.getObjectId()).values()), player), 0);

        restoreTitleAndColor(target);
        playersBounties.remove(target.getObjectId());
        deleteBounty(target.getObjectId());
    }

    private void onPlayerEnterZone(IBaseEvent event) {
        OnCreatureZoneEnter zoneEnter = (OnCreatureZoneEnter) event;
        if (!(zoneEnter.getCreature() instanceof L2PcInstance)) {
            return;
        }
        if (!zoneEnter.getCreature().isInsideZone(ZoneId.PVP)) {
            return;
        }
        setTitleAndColor((L2PcInstance) zoneEnter.getCreature());
    }

    private void onPlayerExitZone(IBaseEvent event) {
        OnCreatureZoneExit zoneExit = (OnCreatureZoneExit) event;
        if (!(zoneExit.getCreature() instanceof L2PcInstance)) {
            return;
        }
        restoreTitleAndColor((L2PcInstance) zoneExit.getCreature());
    }

    private void setTitleAndColor(L2PcInstance player) {
        if (!playersBounties.containsKey(player.getObjectId())) {
            return;
        }
        storeTitle.putIfAbsent(player.getObjectId(), player.getTitle());
        storeTitleColor.putIfAbsent(player.getObjectId(), player.getAppearance().getTitleColor());

        Bounty highestBounty = playersBounties.get(player.getObjectId()).values().stream()
                .max(Comparator.comparing(Bounty::getCurrentBounty)).orElse(null);
        if (highestBounty == null) {
            return;
        }
        player.setTitle("Bounty:" + ((int) highestBounty.getCurrentBounty()) + "x");
        Color c = highestBounty.getColor();
        player.getAppearance().setTitleColor(c.getRed(), c.getGreen(), c.getBlue());

        player.broadcastTitleInfo();
    }

    private void restoreTitleAndColor(L2PcInstance player) {
        if (!playersBounties.containsKey(player.getObjectId())) {
            return;
        }
        if (!storeTitle.containsKey(player.getObjectId()) || !storeTitleColor.containsKey(player.getObjectId())) {
            return;
        }
        player.getAppearance().setTitleColor(storeTitleColor.get(player.getObjectId()));
        player.setTitle(storeTitle.get(player.getObjectId()));
        player.broadcastTitleInfo();
        storeTitle.remove(player.getObjectId());
        storeTitleColor.remove(player.getObjectId());
    }

    private BountyHolder getBounty(int charId) {
        BountyHolder holder;
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement st = con.prepareStatement(sqlSelect)) {
                st.setInt(1, charId);
                ResultSet rs = st.executeQuery();
                if (!rs.isBeforeFirst()) {
                    return null;
                }
                holder = new BountyHolder();
                if (rs.next()) {
                    holder.setCharId(rs.getInt("charId"));
                    holder.setPvpBounty(rs.getDouble("pvpBounty"));
                    holder.setPkBounty(rs.getDouble("pkBounty"));
                    holder.setAssistBounty(rs.getDouble("assistBounty"));
                }
                return holder;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateBounty(BountyHolder holder) {
        if (holder == null) {
            return;
        }
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement st = con.prepareStatement(sqlUpdate)) {
                st.setDouble(1, holder.getPvpBounty());
                st.setDouble(2, holder.getPkBounty());
                st.setDouble(3, holder.getAssistBounty());
                st.setInt(4, holder.getCharId());
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void insertBounty(BountyHolder holder) {
        if (holder == null) {
            return;
        }
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement st = con.prepareStatement(sqlInsert)) {
                st.setInt(1, holder.getCharId());
                st.setDouble(2, holder.getPvpBounty());
                st.setDouble(3, holder.getPkBounty());
                st.setDouble(4, holder.getAssistBounty());
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void deleteBounty(int charId) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement st = con.prepareStatement(sqlDelete)) {
                st.setInt(1, charId);
                st.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private final class UpdateDatabase implements Runnable {
        @Override
        public void run() {
            playersBounties.forEach((key, value) -> {
                BountyHolder bh = new BountyHolder();
                bh.setCharId(key);
                value.forEach((key1, value1) -> {
                    switch (key1) {
                        case "pvp" -> bh.setPvpBounty(value1.getCurrentBounty());
                        case "pk" -> bh.setPkBounty(value1.getCurrentBounty());
                        case "assist" -> bh.setAssistBounty(value1.getCurrentBounty());
                    }
                });
                updateBounty(bh);
            });
        }
    }

    private static final class RewardPlayer implements Runnable {

        List<Bounty> bounties;
        L2PcInstance player;

        public RewardPlayer(List<Bounty> bounties, L2PcInstance player) {
            this.bounties = bounties;
            this.player = player;
        }

        @Override
        public void run() {
            this.bounties.forEach(b -> b.getRewards().forEach(r -> {
                if (r.isMultiply()) {
                    player.addItem("Bounty", r.getId(), (long) (b.getCurrentBounty() * r.getCount()), 0,
                            null, true);
                } else {
                    player.addItem("Bounty", r.getId(), r.getCount(), 0, null, true);
                }
            }));
        }

    }

    public static BountyManager getInstance() {
        return SingeltonHolder.SINGLETON;
    }

    private static class SingeltonHolder {
        private static final BountyManager SINGLETON = new BountyManager();
    }

}
