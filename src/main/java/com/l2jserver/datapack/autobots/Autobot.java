package com.l2jserver.datapack.autobots;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.SocialBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.ActivityPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SocialPreferences;
import com.l2jserver.datapack.autobots.behaviors.sequences.EquipGearRealisticallySequence;
import com.l2jserver.datapack.autobots.behaviors.sequences.Sequence;
import com.l2jserver.datapack.autobots.dao.AutobotsDao;
import com.l2jserver.datapack.autobots.models.AutobotLocation;
import com.l2jserver.datapack.autobots.models.BotChat;
import com.l2jserver.datapack.autobots.models.BotDebugAction;
import com.l2jserver.datapack.autobots.models.RespawnAction;
import com.l2jserver.datapack.autobots.ui.states.BotDetailsViewState;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlEvent;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.ai.NextAction;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.json.ExperienceData;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.enums.PrivateStoreType;
import com.l2jserver.gameserver.handler.ChatHandler;
import com.l2jserver.gameserver.handler.IChatHandler;
import com.l2jserver.gameserver.handler.IItemHandler;
import com.l2jserver.gameserver.handler.ItemHandler;
import com.l2jserver.gameserver.instancemanager.ClanHallManager;
import com.l2jserver.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jserver.gameserver.instancemanager.SiegeManager;
import com.l2jserver.gameserver.model.*;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PetInstance;
import com.l2jserver.gameserver.model.actor.instance.L2TeleporterInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.entity.Siege;
import com.l2jserver.gameserver.model.entity.clanhall.AuctionableHall;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.interfaces.ILocational;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.ActionType;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.EtcItemType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.olympiad.OlympiadManager;
import com.l2jserver.gameserver.model.skills.CommonSkill;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.ZoneId;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.*;
import com.l2jserver.gameserver.taskmanager.AttackStanceTaskManager;
import com.l2jserver.gameserver.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.l2jserver.gameserver.config.Configuration.character;

public class Autobot extends L2PcInstance {
    private Sequence activeSequence = null;
    private final Map<Integer, Map<BotDebugAction, BiConsumer<L2PcInstance, Autobot>>> devActions = new HashMap<>();
    private final AtomicBoolean isBusyThinking = new AtomicBoolean(false);
    private CombatBehavior combatBehavior = AutobotHelpers.getBehaviorByClassId(getClassId(), this, AutobotHelpers.supportedCombatPrefs.get(getClassId()).get());
    private SocialBehavior socialBehavior = new SocialBehavior(this, new SocialPreferences(SocialPreferences.TownAction.TeleToRandomLocation));
    private final RespawnAction respawnAction = RespawnAction.ReturnToDeathLocation;
    private final List<BotChat> chatMessages = new ArrayList<>();
    private long spawnTime = 0;
    private L2PcInstance controller = null;
    private final AutobotLocation previousIterationLocation = new AutobotLocation(getX(), getY(), getZ());
    private CrystalType currentGrade = null;

    /**
     * Creates a player.
     *
     * @param objectId    the object ID
     * @param classId     the player's class ID
     * @param accountName the account name
     * @param app         the player appearance
     */
    public Autobot(int objectId, int classId, String accountName, PcAppearance app) {
        super(objectId, classId, accountName, app);
    }

    public boolean isInMotion() {
        return previousIterationLocation.getX() != getX() || previousIterationLocation.getY() != getY() || previousIterationLocation.getZ() != getZ();
    }

    public void addChat(BotChat chat) {
        chatMessages.add(chat);
        if (chatMessages.size() > 30) {
            chatMessages.remove(0);
        }
    }

    public void onUpdate() {
        updateLocation();
        devDebug();
        if (controller != null) return;
        if (activeSequence != null) return;
        if (isBusyThinking.get()) return;
        isBusyThinking.set(true);
        handleScheduledLogout();
        combatBehavior.applyBuffs();
        if (isInsideZone(ZoneId.TOWN)) {
            socialBehavior.onUpdate();
        } else {
            combatBehavior.onUpdate();
        }
        updateLocation();
        isBusyThinking.set(false);
    }

    private void updateLocation() {
        previousIterationLocation.setX(getX());
        previousIterationLocation.setY(getY());
        previousIterationLocation.setZ(getZ());
    }

    private void handleScheduledLogout() {
        if (combatBehavior.getActivityPreferences().getActivityType() == ActivityPreferences.ActivityType.Uptime) {
            long minutesOnline = ((System.currentTimeMillis() - spawnTime) / 1000) / 60;
            if (minutesOnline >= combatBehavior.getActivityPreferences().getUptimeMinutes()) {
                despawn();
            }
        }
    }

    private void devDebug() {
        if (devActions.isEmpty()) return;
        devActions.entrySet().removeIf(e -> {
            L2PcInstance gm = L2World.getInstance().getPlayer(e.getKey());
            if (gm == null) {
                return true;
            }
            e.getValue().forEach((key, value) -> value.accept(gm, this));
            return false;
        });
    }


    public void addDevAction(L2PcInstance player, BotDebugAction actionType, BiConsumer<L2PcInstance, Autobot> action) {
        if (!devActions.containsKey(player.getObjectId())) {
            devActions.put(player.getObjectId(), new HashMap<>());
        }
        devActions.get(player.getObjectId()).put(actionType, action);
    }

    public void removeDevAction(L2PcInstance player, BotDebugAction actionType) {
        if (!devActions.containsKey(player.getObjectId())) {
            devActions.put(player.getObjectId(), new HashMap<>());
        }
        devActions.get(player.getObjectId()).remove(actionType);
    }

    public boolean hasDevAction(L2PcInstance player, BotDebugAction actionType) {
        if (!devActions.containsKey(player.getObjectId())) {
            return false;
        }
        return devActions.get(player.getObjectId()).containsKey(actionType);
    }

    public void clearDevActions() {
        devActions.forEach((key, value) -> {
            L2PcInstance gm = L2World.getInstance().getPlayer(key);
            value.forEach((actionType, action) -> {
                if (gm != null && actionType == BotDebugAction.VisualizeVision) {
                    AutobotHelpers.clearCircle(gm, getName() + " " + BotDebugAction.VisualizeVision.name());
                }
            });
        });
    }

    @Override
    public void onPlayerEnter() {
        super.onPlayerEnter();
        spawnTime = System.currentTimeMillis();
    }

    public void mySetActiveClass(int classId) {
        setActiveClass(classId);
    }


    public void heal() {
        setCurrentHp(getMaxHp());
        setCurrentMp(getMaxMp());
        setCurrentCp(getMaxCp());
    }

    @Override
    public void setLevel(int level) {
        super.setLevel(level);
        if (level >= 1 && level <= Configuration.character().getMaxPlayerLevel()) {
            long pXp = getExp();
            long tXp = ExperienceData.getInstance().getExpForLevel(level);
            if (pXp > tXp) {
                removeExpAndSp(pXp - tXp, 0);
            } else if (pXp < tXp) {
                addExpAndSp(tXp - pXp, 0);
            }
        }
        onGradeChange();
    }


    public void onGradeChange() {
        CrystalType grade = AutobotHelpers.getGradeType(this);
        if (grade == null || grade != currentGrade) {
            new EquipGearRealisticallySequence(this).execute();
            currentGrade=grade;
        }
    }

    @Override
    public void onActionShift(L2PcInstance player) {
        if (player.isGM()) {
            AutobotsManager.getInstance().loadAutobotDashboard(player, this);
            if (player.getTarget() != this) {
                player.setTarget(this);
            }
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    @Override
    public void teleToLocation(int x, int y, int z, boolean randomOffset) {
        super.teleToLocation(x, y, z, randomOffset);
        onTeleported();
    }

    @Override
    public int getWeightPenalty() {
        return 0;
    }

    @Override
    public int getMaxLoad() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getInventoryLimit() {
        return Integer.MAX_VALUE;
    }


    public void handlePlayerClanOnSpawn() {
        if (getClassId() == null) return;
        if (getClan() == null) return;
        sendPacket(new PledgeSkillList(getClan()));
        getClan().getClanMember(getObjectId()).setPlayerInstance(this);
        SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN);
        PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(this);
        getClan().getOnlineMembers(getObjectId()).forEach(m -> {
            m.sendPacket(update);
            m.sendPacket(msg);
        });
        if (getSponsor() != 0) {
            L2PcInstance sponsor = L2World.getInstance().getPlayer(getSponsor());
            if (sponsor == null) return;
            sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_C1_HAS_LOGGED_IN).addCharName(this));
        } else if (getApprentice() != 0) {
            L2PcInstance apprentice = L2World.getInstance().getPlayer(getApprentice());
            if (apprentice == null) return;
            apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addCharName(this));
        }
        AuctionableHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(getClan());
        if (clanHall != null && !clanHall.getPaid()) {
            sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
        }
        for (Siege siege : SiegeManager.getInstance().getSieges()) {
            if (!siege.isInProgress()) {
                continue;
            }
            if (siege.checkIsAttacker(getClan())) {
                setSiegeState((byte) 1);
                setSiegeSide(siege.getCastle().getResidenceId());
            } else if (siege.checkIsDefender(getClan())) {
                setSiegeState((byte) 2);
                setSiegeSide(siege.getCastle().getResidenceId());
            }
        }
        sendPacket(new PledgeShowMemberListAll(getClan(), this));
        for (L2Clan.SubPledge sp : getClan().getAllSubPledges()) {
            L2PcInstance spLeader = L2World.getInstance().getPlayer(sp.getLeaderId());
            if (spLeader == null) return;
            sendPacket(new PledgeShowMemberListAll(getClan(), spLeader));
        }
        sendPacket(new UserInfo(this));
        sendPacket(new PledgeStatusChanged(getClan()));

    }

    public synchronized void despawn() {
        if (!isInGame()) return;
        try {
            clearDevActions();
            setOnlineStatus(false, true);
            abortAttack();
            abortCast();
            stopMove(null);
            setTarget(null);
            PartyMatchWaitingList.getInstance().removePlayer(this);
            if (isFlying()) removeSkill(CommonSkill.WYVERN_BREATH.getId(), false);
            if (isMounted()) dismount();
            else if (getSummon() != null) getSummon().unSummon(this);
            stopHpMpRegeneration();
            stopChargeTask();
            //TODO PUNISHMENT
//                    PunishmentManager.getInstance().
            stopWaterTask();
            AttackStanceTaskManager.getInstance().removeAttackStanceTask(this);
            stopPvPFlag();
            getKnownList().getKnownCharacters().stream().filter(c -> c instanceof L2Attackable).forEach(L2Character::abortCast);

            getEffectList().stopAllEffects();
            decayMe();
            if (getParty() != null) getParty().removePartyMember(this, L2Party.messageType.Disconnected);
            if (OlympiadManager.getInstance().isRegistered(this) || getOlympiadGameId() != -1)
                OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
            if (getClan() != null) {
                L2ClanMember clanMember = getClan().getClanMember(getObjectId());
                if (clanMember != null) clanMember.setPlayerInstance(null);

            }
            if (getActiveRequester() != null) {
                setActiveRequester(null);
                cancelActiveTrade();
            }
            if (isGM()) AdminData.getInstance().deleteGm(this);
            //TODO isInObservemode
//                if(getBoat()!=null)getBoat().oustPlayer(this,true,);
            getInventory().deleteMe();
            clearWarehouse();
            //clear freight
            //clear depositfreight
            if (isCursedWeaponEquipped())
                CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
            if (getClanId() > 0) getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
            //throne seated
            AutobotsDao.getInstance().saveAutobot(this);
            L2World.getInstance().removeObject(this);
            AutobotsManager.getInstance().getActiveBots().remove(getName());
            getBlockList().playerLogout();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (hasAI()) getAI().stopAITask();

    }

    @Override
    public boolean doDie(L2Character killer) {
        return super.doDie(killer);
        //TODO?
    }

    @Override
    public void rewardSkills() {
        // Give all normal skills if activated Auto-Learn is activated, included AutoGet skills.
        giveAvailableSkills(character().autoLearnForgottenScrollSkills(), true);

        checkPlayerSkills();
        checkItemRestriction();
        sendSkillList();
    }

    @Override
    public int isOnlineInt() {
        return isOnline() ? 1 : 0;
    }

    @Override
    public void setOnlineStatus(boolean isOnline, boolean updateInDb) {
        super.setOnlineStatus(isOnline, updateInDb);
        try (Connection con = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement st = con.prepareStatement("UPDATE autobots SET online=? WHERE obj_Id=?")) {
                st.setInt(1, isOnlineInt());
                st.setInt(2, getObjectId());
                st.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addExpAndSp(long addToExp, int addToSp) {
        int prelevel = getLevel();
        super.addExpAndSp(addToExp, addToSp);
        int postLevel = getLevel();
        if (postLevel != prelevel) {
            onLevelChange(true);
            combatBehavior.onLevelUp(prelevel, postLevel);
            rewardSkills();
        }
    }

    @Override
    public void addExpAndSp(long addToExp, int addToSp, boolean useBonuses) {
        int prelevel = getLevel();
        super.addExpAndSp(addToExp, addToSp, useBonuses);
        int postLevel = getLevel();
        if (postLevel != prelevel) {
            onLevelChange(true);
            combatBehavior.onLevelUp(prelevel, postLevel);
            rewardSkills();
        }
    }

    @Override
    public void removeExpAndSp(long removeFromExp, int removeFromSp) {
        int prelevel = getLevel();
        super.removeExpAndSp(removeFromExp, removeFromSp);
        int postLevel = getLevel();
        if (postLevel != prelevel) {
            onLevelChange(true);
            combatBehavior.onLevelUp(prelevel, postLevel);
        }
    }

    @Override
    public void onLevelChange(boolean levelIncreased) {
        super.onLevelChange(levelIncreased);
        int classLevel = getClassLevelForPlayerLevel(getLevel());
        boolean triggerClassChange = (classLevel - getClassId().level()) > 0;
        if (triggerClassChange) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                ClassId newClassId = getNewClassId();
                if (newClassId != null && newClassId != getClassId()) {
                    com.l2jserver.datapack.autobots.utils.Util.sleep(2500);
                    setClassId(newClassId.getId());
                    rewardSkills();
                }
            }, 0);
        }
        onGradeChange();
    }

    public void startTradeRequest(L2PcInstance target) {
        if (!getAccessLevel().allowTransaction()) return;
        if (!getKnownList().getKnownPlayers().containsKey(target.getObjectId()) || target == this) return;
        if (target.isInOlympiadMode() || isInOlympiadMode()) return;
        if (!Configuration.character().karmaPlayerCanTrade() && (getKarma() > 0 || target.getKarma() > 0)) return;
        if (isInStoreMode() || target.isInStoreMode()) return;
        if (isProcessingTransaction()) return;
        if (target.isProcessingRequest() || target.isProcessingTransaction()) return;
        if (target.getTradeRefusal()) return;
        if (BlockList.isBlocked(target, this)) return;
        if (Util.calculateDistance(this.getLocation(), target.getLocation(), true, true) > L2NpcInstance.INTERACTION_DISTANCE)
            return;
        onTransactionRequest(target);
        target.sendPacket(new SendTradeRequest(getObjectId()));
    }

    public void answerTradeRequest(boolean accept) {
        L2PcInstance partner = getActiveRequester();
        if (partner == null || L2World.getInstance().getPlayer(partner.getObjectId()) == null) {
            setActiveRequester(null);
            return;
        }
        if (accept && !partner.isRequestExpired()) startTrade(partner);
        else
            partner.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_DENIED_TRADE_REQUEST).addCharName(this));
        setActiveRequester(null);
        partner.onTransactionResponse();
    }

    public boolean hasActiveTradeRequest() {
        L2PcInstance partner = getActiveRequester();
        if (partner == null || L2World.getInstance().getPlayer(partner.getObjectId()) == null) {
            setActiveRequester(null);
            return false;
        }
        return !partner.isRequestExpired();
    }

    public boolean hasActiveTrade() {
        return getActiveTradeList() != null;
    }


    public void say(String message) {
        IChatHandler handler = ChatHandler.getInstance().getHandler(0);
        handler.handleChat(0, this, null, message);
    }

    public void shout(String message) {
        IChatHandler handler = ChatHandler.getInstance().getHandler(1);
        handler.handleChat(0, this, null, message);
    }

    @Override
    public void onTradeFinish(boolean successfull) {
        super.onTradeFinish(successfull);
        if (successfull) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> this.say("ty"), 20000);
        }
    }

    public void addTradeItem(L2ItemInstance item) {
        if (getActiveTradeList() == null) return;
        L2PcInstance partner = getActiveTradeList().getPartner();
        if (partner == null || L2World.getInstance().getPlayer(partner.getObjectId()) == null) {
            cancelActiveTrade();
            return;
        }
        if (getActiveTradeList().isConfirmed() || partner.getActiveTradeList().isConfirmed()) return;
        if (!getAccessLevel().allowTransaction()) {
            cancelActiveTrade();
            return;
        }
        if (validateItemManipulation(item.getObjectId(), "bot trading")) return;
        TradeItem addedItem = getActiveTradeList().addItem(item.getObjectId(), item.getCount());
        if (addedItem != null) {
            getActiveTradeList().getPartner().sendPacket(new TradeOtherAdd(addedItem));
        }
    }


    public boolean isInGame() {
        return AutobotsManager.getInstance().getActiveBots().containsKey(getName());
    }

    public void createPrivateSellStore(List<BotDetailsViewState.StoreList> items, String message, boolean packageSell) {
        if (items.isEmpty()) {
            setPrivateStoreType(PrivateStoreType.NONE);
            broadcastUserInfo();
            sendMessage("Cannot create store without items");
            return;
        }
        if (!getAccessLevel().allowTransaction()) {
            sendMessage("Access level does not allow transaction");
            return;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) || isCastingNow() || isCastingSimultaneouslyNow() || isInDuel()) {
            sendMessage("Cannot create store in comabt");
            return;
        }
        if (isInsideZone(ZoneId.NO_STORE)) {
            sendMessage("Cannot create store in NOSTORE zone");
            return;
        }
        if (items.size() > this.getPrivateBuyStoreLimit()) {
            sendMessage("Cannot create wuth more than " + getPrivateSellStoreLimit() + " items");
            return;
        }
        getSellList().clear();
        getSellList().setPackaged(packageSell);
        getSellList().setTitle(message);
        for (BotDetailsViewState.StoreList item : items) {
            boolean hasEnoughItems = getInventory().getItemByItemId(item.first) != null && getInventory().getItemByItemId(item.first).getCount() >= item.second;
            if (!hasEnoughItems) {
                getInventory().addItem("botsell", item.first, item.second, this, null);
            } else {
                getInventory().getItemByObjectId(item.first);
            }
            if (!addToTradeList(item.first, item.second, item.third, getSellList())) {
                sendMessage("Item failed to be added to store");
                return;
            }
        }
        sitDown();
        PrivateStoreType storeType;
        if (packageSell) {
            storeType = PrivateStoreType.PACKAGE_SELL;
        } else {
            storeType = PrivateStoreType.SELL;
        }
        setPrivateStoreType(storeType);
        broadcastUserInfo();
        broadcastPacket(new PrivateStoreMsgSell(this));
    }

    public void createPrivateBuyStore(List<BotDetailsViewState.StoreList> items, String message) {
        if (items.isEmpty()) {
            setPrivateStoreType(PrivateStoreType.NONE);
            broadcastUserInfo();
            sendMessage("Cannot create store without items");
            return;
        }
        if (!getAccessLevel().allowTransaction()) {
            sendMessage("Access level does not allow transaction");
            return;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) || isCastingNow() || isCastingSimultaneouslyNow() || isInDuel()) {
            sendMessage("Cannot create store in combat");
            return;
        }
        if (isInsideZone(ZoneId.NO_STORE)) {
            sendMessage("Cannot create store in NOSTORE zone");
            return;
        }
        getBuyList().clear();
        if (items.size() > getPrivateBuyStoreLimit()) {
            sendMessage("Cannot create store with more than " + getPrivateBuyStoreLimit() + "items");
            return;
        }

        long totalcost = 0;
        for (BotDetailsViewState.StoreList item : items) {
            if (!addToTradeList(item.first, item.second, item.third, getSellList())) {
                sendMessage("Cannot add item with id " + item.first);
                return;
            }
            totalcost += item.third;
        }
        if (totalcost > getAdena()) {
            long missingAmount = totalcost - getAdena();
            getInventory().addAdena("add bot adena", missingAmount, this, null);
        }
        getBuyList().setTitle(message);
        sitDown();
        setPrivateStoreType(PrivateStoreType.BUY);
        broadcastUserInfo();
        broadcastPacket(new PrivateStoreMsgBuy(this));
    }

    public void createPrivateCraftStore(List<CraftStore> recipes, String message) {
        if (isInDuel() || isInCombat()) {
            sendMessage("Cannot create a store while in combat");
            return;
        }
        if (isInsideZone(ZoneId.NO_STORE)) {
            sendMessage("Cannot create store in NOSTORE zone");
            return;
        }
        if (recipes.isEmpty()) {
            standUp();
            return;
        }
        Map<Integer, L2ManufactureItem> manufactureItems = getManufactureItems();
        for (CraftStore recipe : recipes) {
            L2RecipeList newRecipe = recipe.recipe;
            if (!hasRecipeList(newRecipe.getItemId())) {
                if (newRecipe.isDwarvenRecipe()) {
                    registerDwarvenRecipeList(newRecipe, true);
                } else {
                    registerCommonRecipeList(newRecipe, true);
                }
            }
            long cost = recipe.price;
            manufactureItems.put(newRecipe.getRecipeId(), new L2ManufactureItem(newRecipe.getRecipeId(), cost));
        }
        setPrivateStoreType(PrivateStoreType.MANUFACTURE);
        sitDown();
        broadcastUserInfo();
        broadcastPacket(new RecipeShopMsg(this));
    }

    public CombatBehavior getCombatBehavior() {
        return combatBehavior;
    }

    public Sequence getActiveSequence() {
        return activeSequence;
    }

    public void setCombatBehavior(CombatBehavior combatBehavior) {
        this.combatBehavior = combatBehavior;
    }

    public void setActiveSequence(Sequence sequence) {
        this.activeSequence = sequence;
    }

    public void setSocialBehavior(SocialBehavior socialBehavior) {
        this.socialBehavior = socialBehavior;
    }

    public CombatBehavior getCombatBehaviorForClass() {
        CombatPreferences combatPreferences = AutobotHelpers.supportedCombatPrefs.get(getClassId()).get();
        if (combatPreferences == null)
            throw new UnsupportedOperationException("Unsupported class with id" + getClassId());
        return AutobotHelpers.getBehaviorByClassId(getClassId(), this, combatPreferences);
    }

    public boolean isClassSupported(ClassId classId) {
        if (classId == null) return false;
        return AutobotHelpers.supportedCombatPrefs.containsKey(classId);
    }

    public void attack(boolean forceAttack) {
        if (getTarget() == null) return;
        if (getTarget() instanceof L2PcInstance && (((L2PcInstance) getTarget()).isCursedWeaponEquipped() && getLevel() < 21 || isCursedWeaponEquipped() && getLevel() < 21)) {
            sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (getTarget() != null) {
            CombatBehavior combatBehavior = getCombatBehavior();
            if (GeoData.getInstance().canSeeTarget(this, getTarget())) {
                if (forceAttack) {
                    getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                }
                getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, getTarget());
                if (combatBehavior.isMovingTowardsTarget()) {
                    combatBehavior.setIsMovingTowardsTarget(false);
                }
                onActionRequest();
            } else {
                getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getTarget());
                combatBehavior.setIsMovingTowardsTarget(true);
            }
        }
    }

    public Optional<L2Object> getClosestEntityInRadius(int radius) {
        return getClosestEntityInRadius(radius, c -> !c.isDead());
    }

    public Optional<L2Object> getClosestEntityInRadius(int radius, Function<L2Character, Boolean> condition) {
        L2WorldRegion region = getWorldRegion();
        if (region == null) {
            setWorldRegion(L2World.getInstance().getRegion(getLocation()));
            region = getWorldRegion();
        }

        return region.getSurroundingRegions().stream().flatMap(r -> r.getVisibleObjects().values().stream())
                .filter(o -> o instanceof L2Character
                        && condition.apply((L2Character) o)
                        && !((L2Character) o).isGM()
                        && o.getObjectId() != getObjectId()
                        && Util.checkIfInRange(radius, this, o, true))
                .min((p1, p2) -> (int) p1.calculateDistance(p2.getLocation(), false, false));
    }

    public void useMagicSkill(Skill skill, boolean forceAttack) {
        if (skill.hasEffectType(L2EffectType.TELEPORT) && !Configuration.character().karmaPlayerCanTeleport() && getKarma() > 0) {
            return;
        }
        if (skill.isToggle() && isMounted()) {
            return;
        }
        if (isOutOfControl()) {
            return;
        }
        CombatBehavior combatBehavior = getCombatBehavior();
        if (getTarget() != null && !GeoData.getInstance().canSeeTarget(this, getTarget())) {
            getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, getTarget());
            combatBehavior.setIsMovingTowardsTarget(true);

        } else {
            if (isAttackingNow()) {
                getAI().setNextAction(new NextAction(CtrlEvent.EVT_READY_TO_ACT, CtrlIntention.AI_INTENTION_CAST, () -> {
                    if (combatBehavior.isMovingTowardsTarget()) {
                        combatBehavior.setIsMovingTowardsTarget(false);
                    }
                    useMagic(skill, forceAttack, false);
                }));
            } else if (isMoving()) {
                getAI().setNextAction(new NextAction(CtrlEvent.EVT_READY_TO_ACT, CtrlIntention.AI_INTENTION_CAST, () -> {
                    if (combatBehavior.isMovingTowardsTarget()) {
                        combatBehavior.setIsMovingTowardsTarget(false);
                    }
                    useMagic(skill, forceAttack, false);
                }));
            } else {
                if (combatBehavior.isMovingTowardsTarget()) {
                    combatBehavior.setIsMovingTowardsTarget(false);
                }
                useMagic(skill, forceAttack, false);
            }
        }
    }

    public void useItem(int itemId) {
        L2ItemInstance item = getInventory().getItemByItemId(itemId);
        if (item == null) return;
        if (isInStoreMode()) return;
        if (getActiveTradeList() != null) return;
        if (isAlikeDead() || isStunned() || isSleeping() || isParalyzed() || isAfraid()) return;
        if (!Configuration.character().karmaPlayerCanTeleport() && getKarma() > 0) {
            SkillHolder[] s = item.getItem().getSkills();
            if (s != null) {
                for (SkillHolder sh : s) {
                    Skill skill = sh.getSkill();
                    if (skill != null && (skill.hasEffectType(L2EffectType.TELEPORT))) return;
                }
            }
        }
        if (isFishing() && item.getItem().getDefaultAction() != ActionType.FISHINGSHOT) return;
        if (item.isPet()) {
            if (hasPet()) return;
            L2PetInstance pet = (L2PetInstance) getSummon();
            if (pet.isDead()) return;
            if (!pet.getInventory().validateCapacity(item)) return;
            if (!pet.getInventory().validateWeight(item, 1)) return;
            transferItem("Transfer", item.getObjectId(), 1, pet.getInventory(), pet);
            if (item.isEquipped()) {
                pet.getInventory().unEquipItemInSlot(item.getLocationSlot());
            } else {
                pet.getInventory().equipItem(item);
            }
            pet.updateAndBroadcastStatus(1);
            return;
        }
        if (!item.isEquipped()) {
            if (!item.getItem().checkCondition(this, this, true)) return;
        }
        if (item.isEquipable()) {
            if (isCastingNow() || isCastingSimultaneouslyNow()) return;
            switch (item.getItem().getBodyPart()) {
                case L2Item.SLOT_LR_HAND, L2Item.SLOT_L_HAND, L2Item.SLOT_R_HAND -> {
                    if (isMounted()) return;
                    if (isCursedWeaponEquipped()) return;
                }

            }
            if (isCursedWeaponEquipped() && item.getId() == 6408) return;
            if (isAttackingNow()) {
                ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                    L2ItemInstance itemToTest = getInventory().getItemByItemId(item.getObjectId());
                    if (itemToTest == null) return;
                    useEquippableItem(itemToTest.getObjectId(), false);
                }, getAttackEndTime() - System.currentTimeMillis());
            } else {
                useEquippableItem(item.getObjectId(), true);
            }
        } else {
            if (isCastingNow() && !(item.isPotion() || item.isElixir())) return;
            if (getAttackType() == WeaponType.FISHINGROD && item.getItem().getItemType() == EtcItemType.LURE) {
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, item);
                broadcastUserInfo();
                return;
            }
            IItemHandler handler = ItemHandler.getInstance().getHandler(item.getEtcItem());
            handler.useItem(this, item, false);

        }
    }

    public void moveto(ILocational location) {
        getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, location);
    }

    public void moveto(int x, int y, int z) {
        getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(x, y, z));
    }

    public Optional<L2TeleporterInstance> findClosestGatekeeper() {
        return L2World.getInstance().getVisibleObjects()
                .stream()
                .filter(o -> o instanceof L2TeleporterInstance)
                .map(o -> (L2TeleporterInstance) o)
                .min(Comparator.comparingInt(t -> (int) t.calculateDistance(this, false, false)));
    }

    public void controlBot(L2PcInstance player) {
        if (controller != null) {
            if (player.getObjectId() == getObjectId()) {
                sendMessage("You are already controlling this bot");
            } else {
                sendMessage("Bot " + player.getName() + " is already being controller by " + controller.getName());
            }
            return;
        }
        controller = player;
        sendMessage("You are now controlling " + this.getName());
        getSkills().clear();
        player.getSkills().values().forEach(s -> addSkill(s, false));
        sendSkillList();
    }

    public void uncontrollBot() {
        if (controller == null || controller.getObjectId() != getObjectId()) {
            sendMessage("You are not in control of " + getName());
            return;
        }

        controller = null;
        getSkills().clear();
        rewardSkills();
        sendMessage("You are no longer controlling " + this.getName());
    }


    public Location location() {
        return new Location(getX(), getY(), getZ());
    }

    private boolean addToTradeList(int itemObjectId, int count, int price, TradeList list) {
        if (Integer.MAX_VALUE / count < price) return false;
        list.addItem(itemObjectId, count, price);
        return true;
    }

    //    items: List<Triple<Int, Int, Int>>, message: String = "", isPackageSale: Boolean = false, creator: this? = null){
//    public static boolean shouldChangeClass( int newLevel) {
//        return oldLevel < 20 && newLevel >= 20 || oldLevel < 40 && newLevel >= 40 || oldLevel < 76 && newLevel >= 76;
//    }

    public static int getClassLevelForPlayerLevel(int level) {
        if (level >= 1 && level <= 19) {
            return 0;
        }
        if (level >= 20 && level <= 39) {
            return 1;
        }
        if (level >= 40 && level <= 75) {
            return 2;
        }
        return 3;
    }

    public ClassId getNewClassId() {
        int classLevels = getClassLevelForPlayerLevel(getLevel()) - getClassId().level();
        if (classLevels == 0) {
            return getClassId();
        }
        ClassId newClassId;

        if (getClassId().level() < 2) {
            List<ClassId> classIds = Arrays.stream(ClassId.values())
                    .filter(c -> c.getParent() == getClassId() && AutobotHelpers.supportedCombatPrefs.containsKey(c))
                    .collect(Collectors.toList());
            newClassId = classIds.get(Rnd.get(classIds.size()));
        } else {
            newClassId = Arrays
                    .stream(ClassId.values())
                    .filter(c -> c.getParent() == getClassId())
                    .findFirst()
                    .orElse(null);
        }
        if (newClassId != null && newClassId.level() == getClassLevelForPlayerLevel(getLevel())) {
            return newClassId;
        }
        return null;
    }

    private static final class CraftStore {
        L2RecipeList recipe;
        int price;
    }

    public Map<Integer, Map<BotDebugAction, BiConsumer<L2PcInstance, Autobot>>> getDevActions() {
        return devActions;
    }

    public AtomicBoolean getIsBusyThinking() {
        return isBusyThinking;
    }

    public void setIsBusyThinking(boolean busyThinking) {
        this.isBusyThinking.set(busyThinking);
    }

    public SocialBehavior getSocialBehavior() {
        return socialBehavior;
    }

    public RespawnAction getRespawnAction() {
        return respawnAction;
    }

    public List<BotChat> getChatMessages() {
        return chatMessages;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public L2PcInstance getController() {
        return controller;
    }

    public void setController(L2PcInstance player) {
        this.controller = player;
    }

    public AutobotLocation getPreviousIterationLocation() {
        return previousIterationLocation;
    }
}
