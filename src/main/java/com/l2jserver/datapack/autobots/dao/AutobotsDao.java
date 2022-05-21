package com.l2jserver.datapack.autobots.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.models.AutobotInfo;
import com.l2jserver.datapack.autobots.models.ScheduledSpawnInfo;
import com.l2jserver.datapack.autobots.ui.IndexBotOrdering;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.dao.impl.mysql.PlayerDAOMySQLImpl;
import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.model.*;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.entity.Hero;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerEnterWorld;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.util.EnumIntBitmask;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutobotsDao extends PlayerDAOMySQLImpl {
    private final String Create = "INSERT INTO autobots (obj_Id,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,karma,pvpkills,pkkills,clanid,race,classid,deletetime,cancraft,title,accesslevel,online,isin7sdungeon,clan_privs,wantspeace,base_class,nobless,power_grade, heading, x, y, z, creationDate, modificationDate) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String Update = "UPDATE autobots SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,karma=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,deletetime=?,title=?,accesslevel=?,online=?,isin7sdungeon=?,clan_privs=?,wantspeace=?,base_class=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,death_penalty_level=?,modificationDate=?,combat_prefs=?,activity_prefs=?,skill_prefs=?,social_prefs=? WHERE obj_Id=?";
    private final String LoadByName = "SELECT * FROM autobots WHERE char_name=? LIMIT 1";
    private final String LoadById = "SELECT * FROM autobots WHERE obj_id=? LIMIT 1";
    private final String Search = "SELECT char_name, level, online, classid, obj_Id, clanid from autobots ORDER BY {{orderterm}} LIMIT ?,?";
    private final String SearchByName = "SELECT char_name, level, online, classid, obj_Id, clanid from autobots where char_name LIKE ? ORDER BY {{orderterm}} LIMIT ?,?";
    private final String CountBots = "SELECT Count(1) from autobots where char_name LIKE ?";
    private final String UpdateOnlineStatus = "Update autobots set online=?";
    private final String GetBotInfoByName = "SELECT char_name, level, online, classid, obj_Id, clanid from autobots where char_name=? LIMIT 1";
    private final String BotNameExists = "SELECT count(1)  from autobots where char_name=? LIMIT 1";
    private final String GetAllBotInfo = "SELECT char_name, level, online, classid, obj_Id, clanid from autobots";
    private final String DeleteById = "DELETE FROM autobots where obj_Id = ?";
    private final String ScheduledBotSpawn = "SELECT char_name, JSON_EXTRACT(activity_prefs, '$.loginTime') AS loginTime, JSON_EXTRACT(activity_prefs, '$.logoutTime') AS logoutTime FROM autobots WHERE JSON_CONTAINS(activity_prefs, '\"Schedule\"', '$.activityType')";

    private final ObjectMapper mapper;

    private AutobotsDao() {
        mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public void createAutobot(Autobot autobot) {

        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(Create)) {
                ps.setInt(1, autobot.getObjectId());
                ps.setString(2, autobot.getName());
                ps.setInt(3, autobot.getLevel());
                ps.setInt(4, autobot.getMaxHp());
                ps.setDouble(5, autobot.getCurrentHp());
                ps.setInt(6, autobot.getMaxCp());
                ps.setDouble(7, autobot.getCurrentCp());
                ps.setInt(8, autobot.getMaxMp());
                ps.setDouble(9, autobot.getCurrentMp());
                ps.setInt(10, autobot.getAppearance().getFace());
                ps.setInt(11, autobot.getAppearance().getHairStyle());
                ps.setInt(12, autobot.getAppearance().getHairColor());
                ps.setInt(13, autobot.getAppearance().getSex() ? 1 : 0);
                ps.setLong(14, autobot.getExp());
                ps.setInt(15, autobot.getSp());
                ps.setInt(16, autobot.getKarma());
                ps.setInt(17, autobot.getPvpKills());
                ps.setInt(18, autobot.getPkKills());
                ps.setInt(19, autobot.getClanId());
                ps.setInt(20, autobot.getRace().ordinal());
                ps.setInt(21, autobot.getClassId().getId());
                ps.setLong(22, autobot.getDeleteTimer());
                ps.setInt(23, autobot.hasDwarvenCraft() ? 1 : 0);
                ps.setString(24, autobot.getTitle());
                ps.setInt(25, autobot.getAccessLevel().getLevel());
                ps.setInt(26, autobot.isOnlineInt());
                ps.setInt(27, autobot.isIn7sDungeon() ? 1 : 0);
                ps.setInt(28, autobot.getClanPrivileges().getBitmask());
                ps.setInt(29, autobot.getWantsPeace());
                ps.setInt(30, autobot.getBaseClass());
                ps.setInt(31, autobot.isNoble() ? 1 : 0);
                ps.setLong(32, 0);
                ps.setInt(33, autobot.getHeading());
                ps.setInt(34, autobot.getX());
                ps.setInt(35, autobot.getY());
                ps.setInt(36, autobot.getZ());
                ps.setLong(37, System.currentTimeMillis());
                ps.setLong(38, System.currentTimeMillis());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Autobot loadByName(String name) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(LoadByName)) {

                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return null;
                int objectId = rs.getInt("obj_Id");
                int activeClassId = rs.getInt("classId");
                PcAppearance app = new PcAppearance(rs.getByte("face"), rs.getByte("hairColor"), rs.getByte("hairStyle"), rs.getInt("sex") > 0);
                Autobot player = new Autobot(objectId, activeClassId, "Autobots", app);
                player.setIsRunning(true);
                player.setName(rs.getString("char_name"));
                player.getStat().setExp(rs.getLong("exp"));
                player.getStat().setLevel(rs.getByte("level"));
                player.getStat().setSp(rs.getInt("sp"));
                player.setExpBeforeDeath(rs.getLong("expBeforeDeath"));
                player.setWantsPeace(rs.getInt("wantspeace"));
                player.setKarma(rs.getInt("karma"));
                player.setPvpKills(rs.getInt("pvpkills"));
                player.setPkKills(rs.getInt("pkkills"));
                player.setOnlineTime(rs.getLong("onlinetime"));
                player.setNoble(rs.getInt("nobless") == 1);
                player.setClanJoinExpiryTime(rs.getLong("clan_join_expiry_time"));
                if (player.getClanJoinExpiryTime() < System.currentTimeMillis()) player.setClanJoinExpiryTime(0);
                player.setClanCreateExpiryTime(rs.getLong("clan_create_expiry_time"));
                if (player.getClanCreateExpiryTime() < System.currentTimeMillis()) player.setClanCreateExpiryTime(0);
                player.setPowerGrade(rs.getInt("power_grade"));
                player.setPledgeType(rs.getInt("subpledge"));
                int clanId = rs.getInt("clanid");
                if (clanId > 0)
                    player.setClan(ClanTable.getInstance().getClan(clanId));
                if (player.getClan() != null) {
                    if (player.getClan().getLeaderId() != player.getObjectId()) {
                        if (player.getPowerGrade() == 0) player.setPowerGrade(5);
                        player.setClanPrivileges(player.getClan().getRankPrivs(player.getPowerGrade()));
                    } else {
                        player.setClanPrivileges(new EnumIntBitmask<>(ClanPrivilege.class, true));
                        player.setPowerGrade(1);
                    }
                } else player.setClanPrivileges(new EnumIntBitmask<>(ClanPrivilege.class, false));
                player.setDeleteTimer(rs.getLong("deletetime"));
                player.setTitle(rs.getString("title"));
                player.setAccessLevel(rs.getInt("accesslevel"));
                player.setUptime(System.currentTimeMillis());
                player.setRecomHave(rs.getInt("rec_have"));
                player.setRecomLeft(rs.getInt("rec_left"));
                player.setClassIndex(0);
                try {
                    player.setBaseClass(rs.getInt("base_class"));
                } catch (Exception e) {
                    player.setBaseClass(activeClassId);
                }

                if (player.getClassIndex() == 0 && activeClassId != player.getBaseClass())
                    player.setClassId(player.getBaseClass());
                else player.mySetActiveClass(activeClassId);
                player.setApprentice(rs.getInt("apprentice"));
                player.setSponsor(rs.getInt("sponsor"));
                player.setLvlJoinedAcademy(rs.getInt("lvl_joined_academy"));
                player.setIsIn7sDungeon(rs.getInt("isin7sdungeon") == 1);
//                player.().load(rs.getInt("punish_level"), rs.getLong("punish_timer"))
//                PunishmentManager.getInstance().
                CursedWeaponsManager.getInstance().checkPlayer(player);
                player.setDeathPenaltyBuffLevel(rs.getInt("death_penalty_level"));
                player.setXYZ(new Location(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("heading")));
                player.setHero(Hero.getInstance().isHero(objectId));
                player.setPledgeClass(L2ClanMember.calculatePledgeClass(player));
                player.rewardSkills();
                double currentHp = rs.getDouble("curHp");
                player.setCurrentCp(rs.getDouble("curCp"));
                player.setCurrentHp(currentHp);
                player.setCurrentMp(rs.getDouble("curMp"));
                if (currentHp < 0.5) {
                    player.setIsDead(true);
                    player.getStatus().stopHpMpRegeneration();
                }
                L2Summon pet = L2World.getInstance().getPet(player.getObjectId());
                if (pet != null) {
                    player.setSummoner(pet);
                    pet.setOwner(player);
                }

                String jsonCombatPrefs = rs.getString("combat_prefs");
                CombatPreferences combatPrefs = null;
                if (jsonCombatPrefs == null || jsonCombatPrefs.isEmpty()) {

                    if (AutobotHelpers.supportedCombatPrefs.containsKey(player.getClassId())) {
                        combatPrefs = AutobotHelpers.supportedCombatPrefs.get(player.getClassId()).get();
                    } else {
                        combatPrefs = mapper.readValue(jsonCombatPrefs, AutobotHelpers.supportedCombatPrefs.getOrDefault(player.getClassId(), DefaultCombatPreferences::new).get().getClass());
                    }

                }
                if (combatPrefs != null) {
                    player.getCombatBehavior().setCombatPreferences(combatPrefs);
                }

                String jsonActivityPrefs = rs.getString("activity_prefs");

                ActivityPreferences activityPrefs;
                if (jsonActivityPrefs == null || jsonActivityPrefs.isEmpty()) {
                    activityPrefs = new ActivityPreferences();
                } else {
                    activityPrefs = mapper.readValue(jsonActivityPrefs, ActivityPreferences.class);
                }

                player.getCombatBehavior().setActivityPreferences(activityPrefs);

                String jsonSkillPrefs = rs.getString("skill_prefs");
                if (jsonSkillPrefs != null && !jsonSkillPrefs.isEmpty()) {
                    SkillPreferences skillPrefs = mapper.readValue(jsonSkillPrefs, player.getCombatBehavior().getSkillPreferences().getClass());
                    player.getCombatBehavior().setSkillPreferences(skillPrefs);
                }

                String jsonSocialPrefs = rs.getString("social_prefs");
                SocialPreferences socialPrefs;
                if (jsonSocialPrefs == null || jsonSocialPrefs.isEmpty()) {
                    socialPrefs = new SocialPreferences();
                } else {
                    socialPrefs = mapper.readValue(jsonSocialPrefs, SocialPreferences.class);
                }

                player.getSocialBehavior().setSocialPreferences(socialPrefs);
                EventDispatcher.getInstance().notifyEvent(new OnPlayerEnterWorld(player), player);

                return player;
            }


        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveCombatPreferences(@NotNull Autobot player) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE autobots SET combat_prefs=? WHERE obj_Id=?")) {
                ps.setString(1, mapper.writeValueAsString(player.getCombatBehavior().getCombatPreferences()));
                ps.setInt(2, player.getObjectId());
                ps.executeUpdate();
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void saveSocialPreferences(Autobot player) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE autobots SET social_prefs=? WHERE obj_Id=?")) {
                ps.setString(1, mapper.writeValueAsString(player.getSocialBehavior().getSocialPreferences()));
                ps.setInt(2, player.getObjectId());
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void saveSkillPreferences(Autobot player) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE autobots SET skill_prefs=? WHERE obj_Id=?")) {
                ps.setString(1, mapper.writeValueAsString(player.getCombatBehavior().getSkillPreferences()));
                ps.setInt(2, player.getObjectId());
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void saveActivityPreferences(Autobot player) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE autobots SET activity_prefs=? WHERE obj_Id=?")) {
                ps.setString(1, mapper.writeValueAsString(player.getCombatBehavior().getActivityPreferences()));
                ps.setInt(2, player.getObjectId());
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void saveAutobot(Autobot player) {

        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(Update)) {
                ps.setInt(1, player.getLevel());
                ps.setInt(2, player.getMaxHp());
                ps.setDouble(3, player.getCurrentHp());
                ps.setInt(4, player.getMaxCp());
                ps.setDouble(5, player.getCurrentCp());
                ps.setInt(6, player.getMaxMp());
                ps.setDouble(7, player.getCurrentMp());
                ps.setInt(8, player.getAppearance().getFace());
                ps.setInt(9, player.getAppearance().getHairStyle());
                ps.setInt(10, player.getAppearance().getHairColor());
                ps.setInt(11, player.getAppearance().getSex() ? 1 : 0);
                ps.setInt(12, player.getHeading());
                ps.setInt(13, player.getX());
                ps.setInt(14, player.getY());
                ps.setInt(15, player.getZ());
                ps.setLong(16, player.getExp());
                ps.setLong(17, player.getExpBeforeDeath());
                ps.setInt(18, player.getSp());
                ps.setInt(19, player.getKarma());
                ps.setInt(20, player.getPvpKills());
                ps.setInt(21, player.getPkKills());
                ps.setInt(22, player.getClanId());
                ps.setInt(23, player.getRace().ordinal());
                ps.setInt(24, player.getClassId().getId());
                ps.setLong(25, player.getDeleteTimer());
                ps.setString(26, player.getTitle());
                ps.setInt(27, player.getAccessLevel().getLevel());
                ps.setInt(28, player.isOnlineInt());
                ps.setInt(29, player.isIn7sDungeon() ? 1 : 0);
                ps.setInt(30, player.getClanPrivileges().getBitmask());
                ps.setInt(31, player.getWantsPeace());
                ps.setInt(32, player.getBaseClass());
                ps.setLong(33, player.getPowerGrade());
                ps.setInt(34, player.getPledgeType());
                ps.setInt(35, player.getLvlJoinedAcademy());
                ps.setLong(36, player.getApprentice());
                ps.setLong(37, player.getSponsor());
                ps.setLong(38, player.getClanJoinExpiryTime());
                ps.setLong(39, player.getClanCreateExpiryTime());
                ps.setString(40, player.getName());
                ps.setLong(41, player.getDeathPenaltyBuffLevel());
                ps.setLong(42, System.currentTimeMillis());
                ps.setString(43, mapper.writeValueAsString(player.getCombatBehavior().getCombatPreferences()));
                ps.setString(44, mapper.writeValueAsString(player.getCombatBehavior().getActivityPreferences()));
                ps.setString(45, mapper.writeValueAsString(player.getCombatBehavior().getSkillPreferences()));
                ps.setString(46, mapper.writeValueAsString(player.getSocialBehavior().getSocialPreferences()));
                ps.setInt(47, player.getObjectId());
                ps.executeUpdate();
            }
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void deleteBot(int objectId) {

        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(DeleteById)) {
                ps.setInt(1, objectId);
                ps.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ScheduledSpawnInfo> loadScheduleSpawns() {
        List<ScheduledSpawnInfo> list = new ArrayList<>();
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(ScheduledBotSpawn)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(new ScheduledSpawnInfo(rs.getString("char_name").trim(), rs.getString("loginTime").trim(), rs.getString("logoutTime").trim()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AutobotInfo> searchForAutobots() {
        return searchForAutobots("", 1, 10, IndexBotOrdering.None);
    }

    public List<AutobotInfo> searchForAutobots(String nameSearch, int pageNumber, int pageSize, IndexBotOrdering ordering) {
        List<AutobotInfo> list = new ArrayList<>();

        String query = nameSearch.equals("") ? Search : SearchByName;

        String replaceOrdering = switch (ordering) {
            case None -> "creationDate DESC";
            case LevelAsc -> "level ASC";
            case LevelDesc -> "level DESC";
            case OnAsc -> "online ASC";
            case OnDesc -> "online DESC";
        };
        query = query.replace("{{orderterm}}", replaceOrdering);

        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(query)) {

                if (!nameSearch.equals("")) {
                    ps.setString(1, "%" + nameSearch + "%");
                    ps.setInt(2, (pageNumber - 1) * pageSize);
                    ps.setInt(3, pageSize);
                } else {
                    ps.setInt(1, (pageNumber - 1) * pageSize);
                    ps.setInt(2, pageSize);
                }
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AutobotInfo info = new AutobotInfo(
                            rs.getString("char_name"),
                            rs.getInt("level"),
                            rs.getInt("online") == 1,
                            ClassId.values()[rs.getInt("classid")],
                            rs.getInt("obj_Id"),
                            rs.getInt("clanid")
                    );
                    list.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public AutobotInfo getInfoByName(String name) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(GetBotInfoByName)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) return null;

                return new AutobotInfo(
                        rs.getString("char_name"),
                        rs.getInt("level"),
                        rs.getInt("online") == 1,
                        ClassId.values()[rs.getInt("classid")],
                        rs.getInt("obj_Id"),
                        rs.getInt("clanid"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean botWithNameExists(String name) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(BotNameExists)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                rs.next();
                return rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<AutobotInfo> getAllInfo() {
        List<AutobotInfo> list = new ArrayList<>();
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(GetAllBotInfo)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    AutobotInfo info = new AutobotInfo(
                            rs.getString("char_name"),
                            rs.getInt("level"),
                            rs.getInt("online") == 1,
                            ClassId.values()[rs.getInt("classid")],
                            rs.getInt("obj_Id"),
                            rs.getInt("clanid")
                    );
                    list.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateAllBotOnlineStatus(boolean online) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(UpdateOnlineStatus)) {
                ps.setInt(1, online ? 1 : 0);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeClanMember(Autobot player, L2Clan clan) {
        L2ClanMember exMember = clan.getClanMember(player.getObjectId());
        clan.removeClanMember(player.getObjectId(), 0);
        int subPledgeId = clan.getLeaderSubPledge(player.getObjectId());
        if (subPledgeId != 0) {
            L2Clan.SubPledge pledge = clan.getSubPledge(subPledgeId);
            if (pledge != null) {
                pledge.setLeaderId(0);
                clan.updateSubPledgeInDB(pledge.getId());
            }
        }
        if (exMember != null && exMember.isOnline()) {
            L2PcInstance pc = exMember.getPlayerInstance();
            if (!pc.isNoble()) pc.setTitle("");
            if (pc.getActiveWarehouse() != null) pc.setActiveWarehouse(null);

            pc.setApprentice(0);
            pc.setSponsor(0);
            pc.setSiegeState((byte) 0);
            if (pc.isClanLeader()) {
                for (Skill sk : SkillData.getInstance().getSiegeSkills(pc.isNoble(), pc.getClan().getCastleId() > 0)) {
                    pc.removeSkill(sk);
                }
                pc.setClanCreateExpiryTime(0);
            }
            for (Skill s : clan.getAllSkills()) {
                pc.removeSkill(s.getId(), false);
            }
            pc.sendSkillList();
            pc.setClan(null);
            if (exMember.getPledgeType() != L2Clan.SUBUNIT_ACADEMY) player.setClanJoinExpiryTime(0);
            pc.setPledgeClass(L2ClanMember.calculatePledgeClass(pc));
            pc.broadcastUserInfo();

        } else if (exMember != null) {
            try (Connection con = ConnectionFactory.getInstance().getConnection()) {

                try (PreparedStatement ps = con.prepareStatement("UPDATE autobots SET clanid=0, title='', clan_join_expiry_time=0, clan_create_expiry_time=0, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0 WHERE obj_Id=?")) {
                    ps.setInt(1, exMember.getObjectId());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTotalBotCount(String filter) {
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {

            try (PreparedStatement ps = con.prepareStatement(CountBots)) {
                ps.setString(1, "%" + filter + "%");
                ResultSet rs = ps.executeQuery();
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static AutobotsDao getInstance() {
        return AutobotsDao.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotsDao INSTANCE = new AutobotsDao();
    }
}
