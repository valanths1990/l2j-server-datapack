package com.l2jserver.datapack.autobots;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.admincommands.AdminAutobots;
import com.l2jserver.datapack.autobots.autofarm.AutofarmCommandHandler;
import com.l2jserver.datapack.autobots.autofarm.AutofarmManager;
import com.l2jserver.datapack.autobots.behaviors.preferences.ActivityPreferences;
import com.l2jserver.datapack.autobots.config.AutobotSymbol;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.models.AutobotInfo;
import com.l2jserver.datapack.autobots.models.BotChat;
import com.l2jserver.datapack.autobots.models.ChatType;
import com.l2jserver.datapack.autobots.ui.AutobotsUi;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.BotZoneService;
import com.l2jserver.datapack.autobots.utils.IconsTable;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.HennaData;
import com.l2jserver.gameserver.data.xml.impl.PlayerTemplateData;
import com.l2jserver.gameserver.handler.AdminCommandHandler;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.*;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2PcTemplate;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureAttack;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureSkillUse;
import com.l2jserver.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import com.l2jserver.gameserver.model.events.impl.character.player.*;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.items.L2Henna;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class AutobotsManager {

    private final Map<String, Autobot> activeBots = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);
    private AutofarmCommandHandler autofarmCommandHandler;

    private AutobotsManager() {
        load();
    }

    public void load() {
        AutobotData.getInstance();
        AutobotNameService.getInstance();
        IconsTable.getInstance();
        AutobotsDao.getInstance().updateAllBotOnlineStatus(false);
        AutobotScheduler.getInstance();
        AutofarmManager.getInstance();

        autofarmCommandHandler = new AutofarmCommandHandler();
        BypassHandler.getInstance().registerHandler(autofarmCommandHandler);
        AdminAutobots adminAutobots = new AdminAutobots();
        AdminCommandHandler.getInstance().registerHandler(adminAutobots);
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
            activeBots.values().stream().parallel().forEach(b -> {
                try {
                    b.onUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                    b.setIsBusyThinking(false);
                }
            });
        }, 1000, AutobotData.getInstance().getSettings().iterationDelay);

        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_GAME_SHUTDOWN, this::onServerShutDown, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, this::onPlayerLogin, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_ENTER_WORLD, this::onEnterWorld, this));

    }

    private void onPlayerLogin(IBaseEvent event) {
        OnPlayerLogin onLogin = (OnPlayerLogin) event;
        if (!onLogin.getActiveChar().isGM()) return;
        registerListenersOnGm(onLogin.getActiveChar());
    }

    private void registerListenersOnGm(L2PcInstance player) {
        player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_CHAT, this::onChat, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_MOVE_REQUEST, this::onMove, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_CREATURE_SKILL_USE, this::onSkillUse, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_NPC_FIRST_TALK, this::onAction, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_BYPASS, this::onBypass, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_PLAYER_LOGOUT, this::onLogout, this));
        player.addListener(new ConsumerEventListener(player, EventType.ON_CREATURE_ATTACK, this::onAttack, this));

    }

    private void onEnterWorld(IBaseEvent event) {
        OnPlayerEnterWorld onEnterWorld = (OnPlayerEnterWorld) event;
        if (onEnterWorld.getActiveChar() instanceof Autobot) {
            AutofarmManager.getInstance().onEnterWorld((Autobot) onEnterWorld.getActiveChar());
        }
    }

    private void onAttack(IBaseEvent event) {
        OnCreatureAttack onCreatureAttack = (OnCreatureAttack) event;
        if (!onCreatureAttack.getAttacker().isGM()) return;

        if (AutobotHelpers.isControllingBot((L2PcInstance) onCreatureAttack.getAttacker())) {
            Autobot bot = AutobotHelpers.getControllingBot((L2PcInstance) onCreatureAttack.getAttacker());
            L2Character target = onCreatureAttack.getTarget();
            target.onAction(bot);
            if (bot.getTarget() != target) target.onAction(bot);
            else {
                if (target.getObjectId() != bot.getObjectId() && !bot.isInStoreMode() && bot.getActiveRequester() == null)
                    target.onForcedAttack(bot);
                else onCreatureAttack.getAttacker().sendPacket(ActionFailed.STATIC_PACKET);
            }
            onCreatureAttack.getAttacker().sendPacket(ActionFailed.STATIC_PACKET);
        }
    }


    private void onLogout(IBaseEvent event) {
        OnPlayerLogout onLogout = (OnPlayerLogout) event;
        if (!onLogout.getActiveChar().isGM()) return;
        AutofarmManager.getInstance().onLogout(onLogout.getActiveChar());
    }

    private void onBypass(IBaseEvent event) {
        OnPlayerBypass onBypass = (OnPlayerBypass) event;
        if (!onBypass.getActiveChar().isGM()) return;
        autofarmCommandHandler.useBypass(onBypass.getCommand(), onBypass.getActiveChar(), null);
    }

    private void onAction(IBaseEvent event) {
        OnNpcFirstTalk onNpcFirstTalk = (OnNpcFirstTalk) event;
        if (!onNpcFirstTalk.getActiveChar().isGM()) return;
        //TODO
    }

    private void onSkillUse(IBaseEvent event) {
        OnCreatureSkillUse onSKillUse = (OnCreatureSkillUse) event;

        if (!onSKillUse.getCaster().isGM() || !(onSKillUse.getCaster() instanceof L2PcInstance)) return;
        L2PcInstance player = onSKillUse.getCaster().getActingPlayer();
        if (AutobotHelpers.isControllingBot(player)) {
            Autobot bot = AutobotHelpers.getControllingBot(player);
            Skill skill = onSKillUse.getSkill();
            bot.useMagicSkill(skill, false);
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    private void onMove(IBaseEvent event) {
        OnPlayerMoveRequest onMove = (OnPlayerMoveRequest) event;
        if (!onMove.getPlayer().isGM()) return;
        Location l = onMove.getLocation();
        if (BotZoneService.player == onMove.getPlayer()) {
            BotZoneService.graph.points.add(new BotZoneService.BotZonePoint(l.getX(), l.getY(), l.getZ() + 5));
            BotZoneService.sendZone(onMove.getPlayer());
            onMove.getPlayer().sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (AutobotHelpers.isControllingBot(onMove.getPlayer())) {
            Autobot bot = AutobotHelpers.getControllingBot(onMove.getPlayer());
            bot.moveto(l);
            onMove.getPlayer().sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    private void onChat(IBaseEvent event) {
        OnPlayerChat chat = (OnPlayerChat) event;
        switch (chat.getChatType()) {
            case Say2.ALL -> activeBots.forEach((key, value) -> {
                if (Util.checkIfInRange(1250, value, chat.getActiveChar(), true) && !BlockList.isBlocked(value, chat.getActiveChar())) {
                    value.addChat(new BotChat(ChatType.All, chat.getActiveChar().getName(), chat.getText()));
                }
            });
            case Say2.SHOUT -> {
                L2MapRegion region = MapRegionManager.getInstance().getMapRegion(chat.getActiveChar());
                activeBots.forEach((key, value) -> {
                    if (BlockList.isBlocked(value, chat.getActiveChar()) && region == MapRegionManager.getInstance().getMapRegion(value)) {
                        value.addChat(new BotChat(ChatType.Shout, chat.getActiveChar().getName(), chat.getText()));
                    }
                });
            }
            case Say2.TELL -> {
                if (chat.getActiveChar().getTarget() == null || !activeBots.containsKey(chat.getActiveChar().getTarget().getName()))
                    return;
                Autobot bot = activeBots.get(chat.getActiveChar().getTarget().getName());

                L2World.getInstance().getPlayers()
                        .stream()
                        .filter(L2PcInstance::isGM).forEach(p -> p.sendPacket(new CreatureSay(0, 15, chat.getActiveChar().getName() + " " + bot.getName(), chat.getText())));
            }
            case Say2.HERO_VOICE -> {
                if (!chat.getActiveChar().isHero()) return;
                activeBots.forEach((key, value) -> value.addChat(new BotChat(ChatType.Hero, chat.getActiveChar().getName(), chat.getText())));
            }
        }
    }


    public void onServerShutDown(IBaseEvent event) {
        AutobotsDao.getInstance().updateAllBotOnlineStatus(false);
    }

    public void loadAutobotDashboard(L2PcInstance player, Autobot autobot) {
        AutobotsUi.getInstance().loadBotDetails(player, autobot);
    }

    public void spawnAutobot(Autobot autobot) {
        if (autobot.isInGame()) return;
        if (autobot.getCombatBehavior().getActivityPreferences().getActivityType() == ActivityPreferences.ActivityType.Schedule && autobot.getCombatBehavior().getActivityPreferences().logoutTimeIsInThePast()) {
            autobot.getCombatBehavior().getActivityPreferences().setActivityType(ActivityPreferences.ActivityType.None);
        }
        autobot.setClient(new AutobotClient(autobot));
        CharNameTable.getInstance().addName(autobot);
        autobot.rewardSkills();
        AutobotHelpers.giveItemsByClassAndLevel(autobot, false);

        AutobotSymbol symbol = AutobotData.getInstance().getSymbols().stream().filter(s -> s.getClassId() == autobot.getClassId() || s.getClassId() == autobot.getClassId().getParent() || (autobot.getClassId().getParent() != null && autobot.getClassId().getParent() == s.getClassId())).findFirst().orElse(null);
        if (symbol != null && symbol.getSymbolIds().length != 0) {
            Arrays.stream(symbol.getSymbolIds()).forEach(s -> {
                L2Henna henna = HennaData.getInstance().getHenna(s);
                if (henna != null) {
                    autobot.addHenna(henna);
                }
            });
        }

        L2World.getInstance().addPlayerToWorld(autobot);
        activeBots.put(autobot.getName(), autobot);
        autobot.handlePlayerClanOnSpawn();

        autobot.setOnlineStatus(true, true);

        if (Configuration.character().getPlayerSpawnProtection() > 0) {
            autobot.setProtection(true);
        }

        autobot.spawnMe();
        autobot.onPlayerEnter();
        if (!autobot.isGM() && (!autobot.isInSiege() || autobot.getSiegeState() < 2) && autobot.isInsideZone(ZoneId.SIEGE))
            autobot.teleToLocation(TeleportWhereType.TOWN);

        autobot.heal();

    }

    public Autobot createRandomFakePlayer(int x, int y, int z) {
        return createRandomFakePlayer(x, y, z, 85, true);
    }

    public Autobot createRandomFakePlayer(int x, int y, int z, int level, boolean saveInDb) {
        int objectId = IdFactory.getInstance().getNextId();
        String playerName = AutobotNameService.getInstance().getRandomAvailableName();

        List<ClassId> classIds = AutobotHelpers.getSupportedClassesForLevel(level);
        ClassId classId = classIds.get(Rnd.get(classIds.size()));
        L2PcTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
        PcAppearance app = AutobotHelpers.getRandomAppearance();
        Autobot player = new Autobot(objectId, classId.getId(), playerName, app);
        player.setName(playerName);
        player.setIsRunning(true);
        player.setAccessLevel(0);
        player.setXYZ(x, y, z);
        player.setBaseClass(player.getClassId());
        player.setLevel(level);
        AutobotHelpers.giveItemsByClassAndLevel(player, false);
        player.heal();

        if (saveInDb) AutobotsDao.getInstance().createAutobot(player);

        CharNameTable.getInstance().addName(player);
        EventDispatcher.getInstance().notifyEvent(new OnPlayerEnterWorld(player), player);

        return player;
    }

    public Autobot createAndSpawnAutobot(int x, int y, int z, boolean saveInDb) {
        Autobot autobot = createRandomFakePlayer(x, y, z, Rnd.get(1, 81), saveInDb);
        spawnAutobot(autobot);
        return autobot;
    }

    public Autobot createAutobot(L2PcInstance creator, String name, int level, ClassId classId, PcAppearance appearance, int x, int y, int z, int weaponEnchant, int armorEnchant, int jewelEnchant) {
        if (!AutobotNameService.getInstance().nameIsValid(name)) {
            creator.sendMessage("Bot name is not available");
            return null;
        }

        int objectId = IdFactory.getInstance().getNextId();
        L2PcTemplate template = PlayerTemplateData.getInstance().getTemplate(classId);
        Autobot player = new Autobot(objectId, classId.getId(), name, appearance);
        player.setName(name);
        player.setIsRunning(true);
        player.setTitle(AutobotData.getInstance().getSettings().defaultTitle);
        player.setAccessLevel(0);
        player.setXYZ(x, y, z);
        player.setBaseClass(player.getClassId());
        player.setLevel(level);
//        AutobotHelpers.giveItemsByClassAndLevel(player, weaponEnchant, armorEnchant, jewelEnchant, false);
        player.heal();
        AutobotsDao.getInstance().createAutobot(player);
        CharNameTable.getInstance().addName(player);
        EventDispatcher.getInstance().notifyEvent(new OnPlayerEnterWorld(player), player);

        return player;
    }

    public boolean deleteAutobot(Autobot autobot) {
        return deleteAutobot(autobot, () -> {
        });
    }

    public boolean deleteAutobot(Autobot autobot, Runnable onBotIsLeader) {
        if (autobot.isClanLeader()) {
            onBotIsLeader.run();
            return false;
        }
        if (autobot.getClan() != null) {
            AutobotsDao.getInstance().removeClanMember(autobot, autobot.getClan());
        }
        if (autobot.isInGame()) {
            autobot.despawn();
        }
        AutobotsDao.getInstance().deleteBot(autobot.getObjectId());
        return true;
    }

    public AutobotInfo getBotInfoFromOnlineOrDb(String name) {
        if (activeBots.containsKey(name)) {
            Autobot bot = activeBots.get(name);
            return new AutobotInfo(bot.getName(), bot.getLevel(), bot.isOnline(), bot.getClassId(), bot.getObjectId(), bot.getClanId());
        }
        return AutobotsDao.getInstance().getInfoByName(name);
    }

    public Autobot getBotFromOnlineOrDb(String name) {
        if (activeBots.containsKey(name)) {
            return activeBots.get(name);
        }
        return AutobotsDao.getInstance().loadByName(name);
    }

    public Map<String, Autobot> getActiveBots() {
        return activeBots;
    }

    public static AutobotsManager getInstance() {
        return AutobotsManager.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotsManager INSTANCE = new AutobotsManager();
    }

    private static final String DATAPACK = "-dp";

    private static final String SCRIPT = "-s";

    private static final String GEODATA = "-gd";

    public static void main(String[] args) {
        final String datapackRoot = com.l2jserver.commons.util.Util.parseArg(args, DATAPACK, true);
        if (datapackRoot != null) {
            Configuration.server().setProperty("DatapackRoot", datapackRoot);
        }

        final String scriptRoot = com.l2jserver.commons.util.Util.parseArg(args, SCRIPT, true);
        if (scriptRoot != null) {
            Configuration.server().setProperty("ScriptRoot", scriptRoot);
        }

        final String geodata = com.l2jserver.commons.util.Util.parseArg(args, GEODATA, true);
        if (geodata != null) {
            Configuration.geodata().setProperty("GeoDataPath", geodata);
        }
        ConnectionFactory.builder() //
                .withDriver(Configuration.database().getDriver()) //
                .withUrl(Configuration.database().getURL()) //
                .withUser(Configuration.database().getUser()) //
                .withPassword(Configuration.database().getPassword()) //
                .withConnectionPool(Configuration.database().getConnectionPool()) //
                .withMaxIdleTime(Configuration.database().getMaxIdleTime()) //
                .withMaxPoolSize(Configuration.database().getMaxConnections()) //
                .build();

        DAOFactory.getInstance();
        AutobotsManager.getInstance();
    }
}
