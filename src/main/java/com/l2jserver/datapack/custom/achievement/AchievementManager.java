package com.l2jserver.datapack.custom.achievement;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.l2jserver.datapack.custom.Json;
import com.l2jserver.datapack.custom.achievement.exception.AchievementParsingException;
import com.l2jserver.datapack.custom.achievement.handler.AchievementOverview;
import com.l2jserver.datapack.custom.achievement.holder.AchievementHolder;
import com.l2jserver.datapack.custom.achievement.holder.StateHolder;
import com.l2jserver.datapack.custom.achievement.pojo.AchievementPojo;
import com.l2jserver.datapack.custom.achievement.pojo.AchievementsListPojo;
import com.l2jserver.datapack.custom.achievement.stateImpl.Achievement;
import com.l2jserver.datapack.custom.achievement.stateImpl.IState;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.gameserver.model.quest.Quest;

public final class AchievementManager extends Quest {
    private final Set<Achievement> allOriginalAchievements = new CopyOnWriteArraySet<>();
    private final Map<Integer, Set<Achievement>> achievementsInProgress = new ConcurrentHashMap<>();
    private final Map<Integer, Set<Achievement>> completedAchievements = new ConcurrentHashMap<>();
    private File jsonFile;
    private final EventType[] specialEvents = {
            EventType.ON_PLAYER_LOGIN,
            EventType.ON_PLAYER_LOGOUT,
            EventType.ON_PLAYER_PROFESSION_CANCEL,
            EventType.ON_PLAYER_PROFESSION_CHANGE,
            EventType.ON_PLAYER_LEVEL_CHANGED
    };
    private EventDispatcher eventDispatcher;

    private AchievementManager() {
        super(-1, AchievementManager.class.getSimpleName(), "Achievement System");
        BypassHandler.getInstance().registerHandler(new AchievementOverview());
        try {
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean update() {

        try {
            AchievementsListPojo tempList = Json.fromJson(jsonFile, AchievementsListPojo.class);

            Set<Achievement> achievementsParsedWithoutErrorList = new CopyOnWriteArraySet<>();

            for (AchievementPojo a : tempList.getAchievementsList()) {
                if (!achievementsParsedWithoutErrorList.add(AchievementParser.parse(a))) {
                    System.out.println("Achievement with ID" + a.getId() + " could not be loaded");
                }
            }

            allOriginalAchievements.addAll(achievementsParsedWithoutErrorList);
            L2World.getInstance().getPlayers().forEach(p -> allOriginalAchievements.forEach(a -> assignAchievementToPlayer(p, a)));
            return true;
        } catch (AchievementParsingException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void load() throws IOException {
        allOriginalAchievements.clear();
        eventDispatcher = new EventDispatcher(this);
        jsonFile = new File(Configuration.server().getDatapackRoot() + "/data/custom/achievement/achievements.json");
        AchievementsListPojo achievementsListPojo = Json.fromJson(jsonFile, AchievementsListPojo.class);

        Set<EventType> allUsedEventTypes = achievementsListPojo.getAchievementsList().stream()
                .flatMap(a -> a.getState().stream())
                .map(a -> EventType.valueOf(a.getEvent()))
                .collect(Collectors.toSet());

        allUsedEventTypes.forEach(e -> registerConsumer(eventDispatcher::execute, e, ListenerRegisterType.GLOBAL));
        Arrays.stream(specialEvents).forEach(e -> registerConsumer(eventDispatcher::execute, e, ListenerRegisterType.GLOBAL));

        achievementsListPojo.getAchievementsList().forEach(a -> {
            try {
                allOriginalAchievements.add(AchievementParser.parse(a));
            } catch (AchievementParsingException e) {
                e.printStackTrace();
            }
        });
    }

    public void save() {
        ThreadPoolManager.getInstance().scheduleGeneral(new updateAllAchievementsIntoDatabase(), 0);
    }

    public boolean assignAchievementToPlayer(L2PcInstance player, Achievement a) {

        if (player == null || a == null) {
            return false;
        }
        if (!a.assignAchievementToPlayer(player)) {
            return false;
        }

        Achievement copiedAchievement = Achievement.getCopy(a);

        copiedAchievement.setOwner(player);

        AchievementHolder holder = getAchievementFromDatabase(copiedAchievement);

        if (holder == null) {
            insertOrUpdateAchievement(copiedAchievement);
        } else {
            IntStream.range(0, holder.getStates().size()).filter(index -> index < copiedAchievement.getStates().size())
                    .forEach(i -> copiedAchievement.getStates().get(i).increaseProgress(holder.getStates().get(i).getCurrent()));
        }
        if (copiedAchievement.getStates().stream().allMatch(IState::isDone)) {
            if (!completedAchievements.containsKey(player.getObjectId())) {
                completedAchievements.put(player.getObjectId(), new CopyOnWriteArraySet<>());
            }
            completedAchievements.get(player.getObjectId()).add(copiedAchievement);
            return true;
        }

        if (!achievementsInProgress.containsKey(player.getObjectId())) {
            achievementsInProgress.put(player.getObjectId(), new CopyOnWriteArraySet<>());
        }
        achievementsInProgress.get(player.getObjectId()).add(copiedAchievement);
        return true;
    }

    public int getNextAvailableId() {
        return allOriginalAchievements.stream().max(Comparator.comparingInt(Achievement::getId)).map(Achievement::getId).orElse(0);
    }

    public Set<Achievement> getAllPlayersAchievements(L2PcInstance player) {

        Set<Achievement> playersAchievements = new CopyOnWriteArraySet<>();

        if (achievementsInProgress.containsKey(player.getObjectId())) {
            playersAchievements.addAll(achievementsInProgress.get(player.getObjectId()));
        }

        if (completedAchievements.containsKey(player.getObjectId())) {
            playersAchievements.addAll(completedAchievements.get(player.getObjectId()));
        }
        return Collections.unmodifiableSet(playersAchievements);
    }

    public Set<Achievement> getAllAchievements() {
        return Collections.unmodifiableSet(allOriginalAchievements);
    }

    public Map<Integer, Set<Achievement>> getAchievementsInProgress() {
        return Collections.unmodifiableMap(achievementsInProgress);
    }

    public Map<Integer, Set<Achievement>> getCompletedAchievements() {
        return Collections.unmodifiableMap(completedAchievements);
    }


    private void deleteAchievementFromDatabase(Achievement a) {
        if (a == null || a.getOwner() == null) {
            return;
        }
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            String sqlDeleteAchievement = "DELETE FROM achievements,states USING achievements,states WHERE achievements.id = ? AND achievements.charId = ?";
            try (PreparedStatement st = con.prepareStatement(sqlDeleteAchievement)) {
                st.setInt(1, a.getId());
                st.setInt(2, a.getOwner().getObjectId());
                st.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private AchievementHolder getAchievementFromDatabase(Achievement a) {
        if (a == null || a.getOwner() == null) {
            return null;
        }
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            String sqlSelectAchievement = "SELECT achievements.id,achievements.charId,states.num,states.current,states.done FROM achievements  JOIN states ON  achievements.id = states.id AND achievements.charId = states.charId WHERE achievements.id = ? AND achievements.charId = ?";
            try (PreparedStatement st = con.prepareStatement(sqlSelectAchievement)) {
                st.setInt(1, a.getId());
                st.setInt(2, a.getOwner().getObjectId());
                ResultSet rs = st.executeQuery();

                if (!rs.isBeforeFirst()) {
                    return null;
                }
                rs.next();
                return new AchievementHolder(rs.getInt("id"), rs.getInt("charId")
                        , List.of(new StateHolder(rs.getInt("num"), rs.getDouble("current"), rs.getBoolean("done"))));
//                return achievementHolder;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


//    private void updateAchievementIntoDatabase(Achievement a) {
//
//        if (a == null || a.getOwner() == null) {
//            return;
//        }
//
//        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
//            for (int i = 0; i < a.getStates().size(); i++) {
//                String sqlUpdateStates = "UPDATE states set current = ?, done = ? where num = ? AND id = ? AND charId = ?";
//                try (PreparedStatement st = con.prepareStatement(sqlUpdateStates)) {
//                    st.setDouble(1, a.getStates().get(0).getCurrent().doubleValue());
//                    st.setBoolean(2, a.getStates().get(0).isDone());
//                    st.setInt(3, i);
//                    st.setInt(4, a.getId());
//                    st.setInt(5, a.getOwner().getObjectId());
//                    st.executeUpdate();
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

    private void insertOrUpdateAchievement(Achievement a) {

        if (a == null || a.getOwner() == null) {
            return;
        }
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            String sqlInsertAchievement = "INSERT IGNORE INTO achievements (id,charId) VALUES(?,?)";
            try (PreparedStatement st = con.prepareStatement(sqlInsertAchievement)) {
                st.setInt(1, a.getId());
                st.setInt(2, a.getOwner().getObjectId());
                st.execute();
            }
            for (int i = 0; i < a.getStates().size(); i++) {
                String sqlInsertStates = "INSERT INTO states(num,id,charId,current,done) VALUES(?,?,?,?,?) ON DUPLICATE KEY UPDATE current = ? , done = ?";
                try (PreparedStatement st = con.prepareStatement(sqlInsertStates)) {
                    st.setInt(1, i);
                    st.setInt(2, a.getId());
                    st.setInt(3, a.getOwner().getObjectId());
                    st.setDouble(4, a.getStates().get(i).getCurrent().doubleValue());
                    st.setBoolean(5, a.getStates().get(i).isDone());
                    st.setDouble(6, a.getStates().get(i).getCurrent().doubleValue());
                    st.setBoolean(7, a.getStates().get(i).isDone());
                    st.execute();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void achievementCompleted(Achievement a) {
        if (a.isRepeating()) {
            a.reset();
            insertOrUpdateAchievement(a);
            return;
        }
        if (achievementsInProgress.containsKey(a.getOwner().getObjectId())) {
            achievementsInProgress.get(a.getOwner().getObjectId()).remove(a);
        }
//        if (!completedAchievements.containsKey(a.getOwner().getObjectId())) {
//            completedAchievements.put(a.getOwner().getObjectId(), new CopyOnWriteArraySet<>());
//        }
        completedAchievements.computeIfAbsent(a.getOwner().getObjectId(), k -> new CopyOnWriteArraySet<>()).add(a);
//        completedAchievements.get(a.getOwner().getObjectId()).add(a);
        insertOrUpdateAchievement(a);
    }

    public void onPlayerLogin(L2PcInstance player) {
        allOriginalAchievements.forEach(a -> assignAchievementToPlayer(player, a));
    }

    public void onPlayerLogout(L2PcInstance player) {
        if (achievementsInProgress.containsKey(player.getObjectId())) {
            achievementsInProgress.remove(player.getObjectId())
                    .forEach(this::insertOrUpdateAchievement);
            completedAchievements.remove(player.getObjectId());
        }
//        achievementsInProgress.remove(player.getObjectId());
    }

    public void onProfessionChange(L2PcInstance player) {
        onChanges(player);
    }

    public void onProfessionCancel(L2PcInstance player) {
        onChanges(player);
    }

    public void onLevelChanged(L2PcInstance player) {
        onChanges(player);
    }

    private void onChanges(L2PcInstance player) {
        if (!achievementsInProgress.containsKey(player.getObjectId())) {
            return;
        }
        Set<Achievement> playersAchievements = achievementsInProgress.get(player.getObjectId());
        Set<Achievement> difference = new CopyOnWriteArraySet<>(allOriginalAchievements);
        if (playersAchievements != null) {
            playersAchievements.stream().filter(ach -> !ach.assignAchievementToPlayer(player))
                    .forEach(this::insertOrUpdateAchievement);
            playersAchievements.removeIf(a -> !a.assignAchievementToPlayer(player));
            difference.removeAll(playersAchievements);
        }
        difference.forEach(aa -> assignAchievementToPlayer(player, aa));
    }

    public void progressAchievement(L2PcInstance player, IBaseEvent event) {
        if (!achievementsInProgress.containsKey(player.getObjectId())) {
            return;
        }
        achievementsInProgress.get(player.getObjectId()).stream().filter(a -> a.transit(event)).forEach(a -> {
            achievementCompleted(a);
            ThreadPoolManager.getInstance().scheduleGeneral(new RewardTask(a), 0);
        });
    }

    private static final class RewardTask implements Runnable {
        private final Achievement achievement;

        public RewardTask(Achievement achievement) {
            this.achievement = achievement;
        }

        @Override
        public void run() {
//            Message msg = new Message(achievement.getOwner().getObjectId(), achievement.getTitle(),
//                    achievement.getDesc(), SendBySystem.NEWS);
//            achievement.getRewardItems().stream().limit(8).forEach(reward -> Objects.requireNonNull(msg.createAttachments()).addItem("Achievement Reward", reward.getId(), reward.getCount(), null, null));
////            achievement.getRewa
//            MailManager.getInstance().sendMessage(msg);
            achievement.getRewardOperations().forEach(a -> a.executeOperation(achievement.getOwner()));
        }
    }

    private final class updateAllAchievementsIntoDatabase implements Runnable {
        @Override
        public void run() {
            achievementsInProgress.values().forEach(s -> s.forEach(AchievementManager.this::insertOrUpdateAchievement));
        }

    }

    public static AchievementManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AchievementManager INSTANCE = new AchievementManager();
    }

    public static void main(String[] args) {
        AchievementManager.getInstance();
    }
}
