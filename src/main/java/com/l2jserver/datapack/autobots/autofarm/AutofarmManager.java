package com.l2jserver.datapack.autobots.autofarm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.SocialBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.behaviors.sequences.Sequence;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AutofarmManager {
    private final Map<Integer, Autobot> activePlayers = new ConcurrentHashMap<>();
    private final Map<Integer, CombatBehavior> combatBehaviors = new HashMap<>();
    private final Map<Integer, SocialBehavior> socialBehaviors = new HashMap<>();
    private final Map<Integer, Sequence> sequences = new HashMap<>();
    private final Map<Integer, Boolean> busyStates = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, CombatPreferences>> combatPreferences = new HashMap<>();
    private final Map<Integer, Map<Integer, SkillPreferences>> skillPreferences = new HashMap<>();

    private AutofarmManager() {
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
            activePlayers.values().forEach(p -> {
                try {
                    if (!busyStates.containsKey(p.getObjectId())) {
                        busyStates.put(p.getObjectId(), true);
                    }

                    if (p.isInsideZone(ZoneId.TOWN)) {
                        p.getSocialBehavior().onUpdate();
                    } else {
                        p.getCombatBehavior().onUpdate();
                    }
                    busyStates.remove(p.getObjectId());

                } catch (Exception e) {
                    busyStates.remove(p.getObjectId());
                    e.printStackTrace();
                }


            });
        }, 1000, 500);
    }

    public void startFarm(Autobot player) {
        if (!player.isClassSupported(Arrays.stream(ClassId.values()).filter(it -> it.getId() == player.getActiveClass()).findFirst().orElse(null))) {
            player.sendMessage("Autofarm doesn't support your class");
            return;
        }

        if (activePlayers.containsKey(player.getObjectId())) {
            player.sendMessage("Autofarm is already enabled");
            return;
        }

        CombatBehavior behavior = getBehaviorForActiveClass(player);

        activePlayers.put(player.getObjectId(), player);
        combatBehaviors.put(player.getObjectId(), behavior);
        socialBehaviors.put(player.getObjectId(), new SocialBehavior(player, new SocialPreferences(SocialPreferences.TownAction.None)));

        player.sendMessage("Autofarm activated");
    }

    public void stopFarm(Autobot player) {
        if (!activePlayers.containsKey(player.getObjectId())) {
            player.sendMessage("Autofarm is not enabled");
            return;
        }

        cleanUpStates(player);
        player.sendMessage("Autofarm deactivated");
    }

    private void cleanUpStates(L2PcInstance player) {
        cleanUpStates(player, false);
    }

    private void cleanUpStates(L2PcInstance player, boolean includeBehaviors) {
        activePlayers.remove(player.getObjectId());
        combatBehaviors.remove(player.getObjectId());
        socialBehaviors.remove(player.getObjectId());
        sequences.remove(player.getObjectId());
        busyStates.remove(player.getObjectId());
        if (includeBehaviors) {
            combatPreferences.remove(player.getObjectId());
            skillPreferences.remove(player.getObjectId());
        }
    }

    public void onEnterWorld(Autobot player) {
        List<PlayerPreferencesDto> playerPreferencesDto = restorePreferences(player);

        boolean isInit = playerPreferencesDto.isEmpty();

        combatPreferences.put(player.getObjectId(), new HashMap<>());
        skillPreferences.put(player.getObjectId(), new HashMap<>());

        playerPreferencesDto.forEach(dto -> {
            combatPreferences.get(dto.playerId).put(dto.classId, dto.combatPreferences);
            skillPreferences.get(dto.playerId).put(dto.classId, dto.skillPreferencess);
        });

        if (isInit) {
            getBehaviorForActiveClass(player);
            savePreferences(player);
        }
    }

    public void savePreferences(L2PcInstance player) {
        if(!combatPreferences.containsKey(player.getObjectId()))return;
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO character_autofarm(obj_Id, classId, combat_prefs, skill_prefs) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE combat_prefs=?, skill_prefs=?")) {
                combatPreferences.get(player.getObjectId()).forEach((key, value) -> {
                    SkillPreferences skillPrefs = skillPreferences.get(player.getObjectId()).get(key);
                    try {
                        ps.setInt(1, player.getObjectId());
                        ps.setInt(2, key);
                        String combatPrefs = Util.mapper.writeValueAsString(value);
                        String skillPrefsAsString = skillPrefs != null ? Util.mapper.writeValueAsString(skillPrefs) : null;
                        ps.setString(3, combatPrefs);
                        ps.setString(4, skillPrefsAsString);
                        ps.setString(5, combatPrefs);
                        ps.setString(6, skillPrefsAsString);
                        ps.addBatch();
                    } catch (SQLException | JsonProcessingException e) {
                        e.printStackTrace();
                    }

                });
                ps.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private List<PlayerPreferencesDto> restorePreferences(Autobot player) {
        List<PlayerPreferencesDto> prefs = new ArrayList<>();
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM character_autofarm WHERE obj_Id = ? ")) {
                ps.setInt(1, player.getObjectId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int playerId = rs.getInt("obj_Id");
                    int classId = rs.getInt("classId");
                    String jsonCombatPres = rs.getString("combat_prefs");
                    CombatPreferences combatPrefs;
                    if (jsonCombatPres == null || jsonCombatPres.isEmpty()) {
                        combatPrefs = AutobotHelpers.supportedCombatPrefs.get(Arrays.stream(ClassId.values()).filter(c -> c.getId() == player.getActiveClass()).findFirst().orElse(null)).get();
                    } else {
                        combatPrefs = Util.mapper.readValue(jsonCombatPres, AutobotHelpers.supportedCombatPrefs.getOrDefault(Arrays.stream(ClassId.values()).filter(c -> c.getId() == player.getActiveClass()).findFirst().orElse(null), DefaultCombatPreferences::new).get().getClass());
                    }
                    String jsonSkillPrefs = rs.getString("skill_prefs");
                    SkillPreferences skillPrefs = null;

                    if (jsonSkillPrefs == null || jsonSkillPrefs.isEmpty()) {
                        skillPrefs = Util.mapper.readValue(jsonCombatPres, player.getCombatBehavior().getSkillPreferences().getClass());
                    }
                    prefs.add(new PlayerPreferencesDto(playerId, classId, combatPrefs, skillPrefs));
                }

            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return prefs;
    }

    public void onLogout(L2PcInstance player) {
        savePreferences(player);
        cleanUpStates(player, true);
    }

    public boolean isAutoFarming(Autobot player) {
        return activePlayers.containsKey(player.getObjectId());
    }

    private CombatBehavior getBehaviorForActiveClass(Autobot player) {
        CombatBehavior behavior = player.getCombatBehaviorForClass();

        CombatPreferences combatPrefs = combatPreferences
                .getOrDefault(player.getObjectId(), new HashMap<>())
                .getOrDefault(player.getActiveClass(), AutobotHelpers.supportedCombatPrefs.getOrDefault(Arrays.stream(ClassId.values()).filter(it -> it.getId() == player.getActiveClass()).findFirst().orElse(null), DefaultCombatPreferences::new).get());

        behavior.setCombatPreferences(combatPrefs);
        behavior.setActivityPreferences(new ActivityPreferences());

        skillPreferences.getOrDefault(player.getObjectId(), new HashMap<>());
        SkillPreferences skillPref = skillPreferences.get(player.getObjectId()).get(player.getActiveClass());
        if (skillPref != null) {
            behavior.setSkillPreferences(skillPref);
        } else {
            skillPreferences.get(player.getObjectId()).put(player.getActiveClass(), behavior.getSkillPreferences());
        }
        return behavior;
    }

    public Map<Integer, Autobot> getActivePlayers() {
        return activePlayers;
    }

    public Map<Integer, CombatBehavior> getCombatBehaviors() {
        return combatBehaviors;
    }

    public Map<Integer, SocialBehavior> getSocialBehaviors() {
        return socialBehaviors;
    }

    public Map<Integer, Sequence> getSequences() {
        return sequences;
    }

    public Map<Integer, Boolean> getBusyStates() {
        return busyStates;
    }

    public Map<Integer, Map<Integer, CombatPreferences>> getCombatPreferences() {
        return combatPreferences;
    }

    public Map<Integer, Map<Integer, SkillPreferences>> getSkillPreferences() {
        return skillPreferences;
    }

    private static class PlayerPreferencesDto {
        public int playerId;
        public int classId;
        public CombatPreferences combatPreferences;
        public SkillPreferences skillPreferencess;

        public PlayerPreferencesDto(int playerId, int classId, CombatPreferences combatPreferences, SkillPreferences skillPreferencess) {
            this.playerId = playerId;
            this.classId = classId;
            this.combatPreferences = combatPreferences;
            this.skillPreferencess = skillPreferencess;
        }

    }
    public static AutofarmManager getInstance() {
        return AutofarmManager.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutofarmManager INSTANCE = new AutofarmManager();
    }
}
