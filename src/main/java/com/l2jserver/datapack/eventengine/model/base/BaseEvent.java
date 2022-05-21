/*
 * Copyright (C) 2015-2016 L2J EventEngine
 *
 * This file is part of L2J EventEngine.
 *
 * L2J EventEngine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J EventEngine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.eventengine.model.base;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.l2jserver.datapack.eventengine.EventEngineManager;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.config.BaseConfigLoader;
import com.l2jserver.datapack.eventengine.datatables.BuffListData;
import com.l2jserver.datapack.eventengine.datatables.MessageData;
import com.l2jserver.datapack.eventengine.dispatcher.ListenerDispatcher;
import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.*;
import com.l2jserver.datapack.eventengine.events.listeners.EventEngineListener;
import com.l2jserver.datapack.eventengine.events.schedules.AnnounceNearEndEvent;
import com.l2jserver.datapack.eventengine.events.schedules.AnnounceTeleportEvent;
import com.l2jserver.datapack.eventengine.events.schedules.ChangeToEndEvent;
import com.l2jserver.datapack.eventengine.events.schedules.ChangeToFightEvent;
import com.l2jserver.datapack.eventengine.events.schedules.ChangeToStartEvent;
import com.l2jserver.datapack.eventengine.interfaces.IListenerSubscriber;
import com.l2jserver.datapack.eventengine.managers.*;
import com.l2jserver.datapack.eventengine.model.config.AbstractEventConfig;
import com.l2jserver.datapack.eventengine.model.config.MainEventConfig;
import com.l2jserver.datapack.eventengine.model.entity.*;
import com.l2jserver.datapack.eventengine.model.entity.Character;
import com.l2jserver.datapack.eventengine.model.instance.WorldInstance;
import com.l2jserver.datapack.eventengine.model.template.ItemTemplate;
import com.l2jserver.datapack.eventengine.model.template.SkillTemplate;
import com.l2jserver.datapack.eventengine.util.EventUtil;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerEventParticipated;
import com.l2jserver.gameserver.model.holders.Participant;
import com.l2jserver.gameserver.model.zone.type.L2EventZone;
import com.l2jserver.gameserver.network.serverpackets.*;

/**
 * @author fissban
 */
public abstract class BaseEvent<T extends AbstractEventConfig> implements IListenerSubscriber {
    // Logger
    private static final Logger LOGGER = Logger.getLogger(BaseEvent.class.getName());

    private T _config;

    protected L2EventZone zone;
    protected WorldInstance world;

    public void initialize() {
        chooseRandomZone();
        // Add every player registered for the event
        getPlayerEventManager().createEventPlayers();

        if (getMainConfig().isAntiAfkEnabled()) {
            _antiAfkManager = new AntiAfkManager();
        }
        initScheduledEvents();
        // Starts the clock to control the sequence of internal events of the event
        getScheduledEventsManager().startTaskControlTime();
    }

    public void setConfig(T config) {
        _config = config;
    }

    protected T getConfig() {
        return _config;
    }

    private MainEventConfig getMainConfig() {
        return BaseConfigLoader.getInstance().getMainConfig();
    }

    /**
     * Necessary to handle the event states.
     *
     * @param state
     */
    public final void runEventState(EventState state) {
        switch (state) {
            case START -> {
                prepareToStart();
                onEventStart();
            }
            case FIGHT -> {
                prepareToFight();
                onEventFight();
            }
            case END -> {
                ListenerDispatcher.getInstance().removeSuscriber(this);
                onEventEnd();
                prepareToEnd();
            }
        }
    }


    protected void chooseRandomZone() {
        Collections.shuffle(getConfig().getZoneIds());
        zone = ZoneManager.getInstance().getZoneById(_config.getZoneIds().get(0), L2EventZone.class);
        zone.setEnabled(true);
//		zone.getPlayersInside().forEach(p -> p.teleToLocation(TeleportWhereType.TOWN));
    }

    protected void updateScore(Player player) {

        switch (_config.getType()) {
            case SINGLE -> {
                List<Participant> participants = getPlayerEventManager().getAllEventPlayers()
                        .stream()
                        .map(p -> new Participant(p.getName(), p.getPoints(ScoreType.KILL)))
                        .sorted(Comparator.comparing(Participant::getPoints, Comparator.reverseOrder())).
                        collect(Collectors.toList());
                getPlayerEventManager().getAllEventPlayers().forEach(p -> p.getPcInstance().sendPacket(new ExShowPVPMatchRecord(participants)));
            }
            case TEAM -> {
                int redPoints = _teamsManagers.getTeam(TeamType.RED).getPoints(getConfig().getScoreType());
                int bluePoints = _teamsManagers.getTeam(TeamType.BLUE).getPoints(getConfig().getScoreType());
                ExCubeGameChangePoints pointsChange = new ExCubeGameChangePoints(EventEngineManager.getInstance().getTime(), bluePoints, redPoints);
                ExCubeGameExtendedChangePoints pointsChangeExtended = new ExCubeGameExtendedChangePoints(EventEngineManager.getInstance().getTime(), bluePoints, redPoints, player.getTeamType() == TeamType.RED, player.getPcInstance(), player.getPoints(getConfig().getScoreType()));
                getPlayerEventManager().getAllEventPlayers().stream().map(Player::getPcInstance).forEach(pc -> {
                    pc.sendPacket(pointsChange);
                    pc.sendPacket(pointsChangeExtended);
                    pc.sendPacket(ActionFailed.STATIC_PACKET);
                });
            }
        }
    }

    protected abstract String getInstanceFile();

    protected abstract TeamsBuilder onCreateTeams();

    protected abstract void onEventStart();

    protected abstract void onEventFight();

    protected abstract void onEventEnd();

    // XXX ANTI AFK SYSTEM -------------------------------------------------------------------------------
    private AntiAfkManager _antiAfkManager;

    public AntiAfkManager getAntiAfkManager() {
        return _antiAfkManager;
    }

    // XXX TEAMS -----------------------------------------------------------------------------------------
    private final TeamsManagers _teamsManagers = new TeamsManagers();

    public TeamsManagers getTeamsManager() {
        return _teamsManagers;
    }

    // XXX DYNAMIC INSTANCE ------------------------------------------------------------------------------
    private final InstanceWorldManager _instanceWorldManager = InstanceWorldManager.newInstance();

    public InstanceWorldManager getInstanceWorldManager() {
        return _instanceWorldManager;
    }

    // XXX SCHEDULED AND UNSCHEDULED EVENTS --------------------------------------------------------------
    private final ScheduledEventsManager _scheduledEventsManager = new ScheduledEventsManager();

    public ScheduledEventsManager getScheduledEventsManager() {
        return _scheduledEventsManager;
    }

    // XXX TELEPORT --------------------------------------------------------------
    protected int _radius = 50;

    /**
     * Init the scheduled events.
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Step 1: Announce participants will be teleported.</li>
     * <li>Wait 3 secs.</li>
     * <li>Step 2: Adjust the status of the event -> START.</li>
     * <li>We hope 1 sec to actions within each event is executed.</li>
     * <li>Step 3: Adjust the status of the event -> FIGHT.</li>
     * <li>Step 4: We sent a message that they are ready to fight.</li>
     * <li>We wait until the event ends.</li>
     * <li>Step 5: Adjust the status of the event -> END.</li>
     * <li>Step 6: We sent a message warning that term event.</li>
     * <li>Wait for 1 sec.</li>
     * <li>Step 7: Alert the event has ended.</li>
     */
    private void initScheduledEvents() {
        int time = 1000;
        getScheduledEventsManager().addScheduledEvent(new AnnounceTeleportEvent(time));
        time += 3000;
        getScheduledEventsManager().addScheduledEvent(new ChangeToStartEvent(time));
        time += 1000;
        getScheduledEventsManager().addScheduledEvent(new ChangeToFightEvent(time));
        time += getMainConfig().getRunningTime() * 60 * 1000;
        getScheduledEventsManager().addScheduledEvent(new ChangeToEndEvent(time));
        // Announce near end event
        int timeLeftAnnounce = getMainConfig().getTextTimeForEnd() * 1000;
        getScheduledEventsManager().addScheduledEvent(new AnnounceNearEndEvent(time - timeLeftAnnounce, getMainConfig().getTextTimeForEnd()));
        getScheduledEventsManager().addScheduledEvent(new AnnounceNearEndEvent(time - (timeLeftAnnounce / 2), getMainConfig().getTextTimeForEnd() / 2));
    }

    // REVIVE --------------------------------------------------------------------------------------- //
    private final List<ScheduledFuture<?>> _revivePending = new CopyOnWriteArrayList<>();

    private void stopAllPendingRevive() {
        for (ScheduledFuture<?> scheduledFuture : _revivePending) {
            scheduledFuture.cancel(true);
        }
        _revivePending.clear();
    }

    // NPC IN EVENT --------------------------------------------------------------------------------- //
    private final SpawnManager _spawnManager = new SpawnManager();

    public SpawnManager getSpawnManager() {
        return _spawnManager;
    }

    // PLAYERS IN EVENT ----------------------------------------------------------------------------- //
    private final PlayersManager _playerEventManager = new PlayersManager();

    public PlayersManager getPlayerEventManager() {
        return _playerEventManager;
    }

    // LISTENERS ------------------------------------------------------------------------------------ //

    /**
     * @param event
     */
    @Override
    public final void listenerOnInteract(OnInteractEvent event) {
        Player player = event.getPlayer();
        Npc target = event.getNpc();

        if (!getPlayerEventManager().isPlayableInEvent(player) || !getSpawnManager().isNpcInEvent(target)) {
            return;
        }
        // Get the player involved in our event
        Player activePlayer = getPlayerEventManager().getEventPlayer(player);
        // Exclude the player from the next Anti Afk control
        if (getAntiAfkManager() != null) {
            getAntiAfkManager().excludePlayer(activePlayer);
        }
        onInteract(event);
    }

    /**
     * @param event
     */
    protected void onInteract(OnInteractEvent event) {
    }

    protected void onUnequippedItem(OnUnequipItem event) {

    }

    @Override
    public final void listenerOnDlgAnswer(OnDlgAnswer event) {
        onDlgAnswer(event);
    }

    protected void onDlgAnswer(OnDlgAnswer event) {

    }

    @Override
    public final void listenerOnTowerCaptured(OnTowerCapturedEvent event) {
        if (!_playerEventManager.isPlayableInEvent(event.getPlayer())) return;
        onTowerCaptured(event);
    }

    protected void onTowerCaptured(OnTowerCapturedEvent event) {

    }

    @Override
    public final void listenerOnDoorAction(OnDoorActionEvent event) {
        if (!_playerEventManager.isPlayableInEvent(event.getPlayer())) return;
        onDoorAction(event);
    }

    protected void onDoorAction(OnDoorActionEvent event) {

    }

    @Override
    public void listenerOnUnequipItem(OnUnequipItem event) {
        onUnequippedItem(event);
    }

    @Override
    public void listenerOnPlayableHit(OnPlayableHitEvent event) {
        onPlayableHit(event);
    }

    protected void onPlayableHit(OnPlayableHitEvent event) {

    }

    /**
     * @param event
     */
    @Override
    public final void listenerOnKill(OnKillEvent event) {
        Playable playable = event.getAttacker();
        Character target = event.getTarget();

        if (!getPlayerEventManager().isPlayableInEvent(playable)) {
            return;
        }
        // We ignore if they kill any summon
        // XXX It could be used in some event...analyze!
        if (target instanceof Summon) {
            return;
        }
        // Get the player involved in our event
        Player activePlayer = getPlayerEventManager().getEventPlayer(playable);
        // Exclude the player from the next Anti Afk control
        if (getAntiAfkManager() != null) {
            getAntiAfkManager().excludePlayer(activePlayer);
        }
        onKill(event);
    }

    /**
     * @param event
     */
    protected void onKill(OnKillEvent event) {
    }

    /**
     * @param event
     */
    @Override
    public final void listenerOnDeath(OnDeathEvent event) {
        Player player = event.getTarget();

        if (!getPlayerEventManager().isPlayableInEvent(player)) {
            return;
        }
        onDeath(event);
    }

    /**
     * @param event
     */
    protected void onDeath(OnDeathEvent event) {

    }

    public void listenerOnUseTeleport(OnUseTeleport event) {

        onUseTeleport(event);
    }

    protected void onUseTeleport(OnUseTeleport event) {
        if (this.zone.isInsideZone(event.getLocation()) && event.getLocation().getInstanceId() == world.getInstanceId()) {
            return;
        }
        event.setCancel(true);
    }

    @Override
    public void listenerOnPlayerMove(OnPlayerMoveEvent event) {
        onPlayerMove(event);
    }

    protected void onPlayerMove(OnPlayerMoveEvent event) {
        if (!zone.isInsideZone(event.getDestination().getLocation())) {
            event.setCancel(true);
        }
    }

    @Override
    public final void listenerOnAttack(OnAttackEvent event) {
        Playable playable = event.getAttacker();
        Character target = event.getTarget();

        if (!getPlayerEventManager().isPlayableInEvent(playable)) {
            event.setCancel(true);
            return;
        }
        // We get the player involved in our event
        Player activePlayer = getPlayerEventManager().getEventPlayer(playable);

        // Remove the spawn protection time
        if (activePlayer.getProtectionTimeEnd() > 0) {
            activePlayer.setProtectionTimeEnd(0);
            activePlayer.sendMessage(MessageData.getInstance().getMsgByLang(activePlayer, "spawnprotection_ended", false));
        }

        // Exclude the player from the next Anti Afk control
        if (getAntiAfkManager() != null) {
            getAntiAfkManager().excludePlayer(activePlayer);
        }

        // If our target is L2Playable type and we do this in the event control
        Player activeTarget = getPlayerEventManager().getEventPlayer(target);

        if (activeTarget != null) {
            if (activeTarget.isProtected()) {
                activePlayer.sendMessage(MessageData.getInstance().getMsgByLang(activePlayer, "spawnprotection_protected", false));
                return;
            }
            // Check Friendly Fire
            if (!getMainConfig().isFriendlyFireEnabled()) {
                if (activePlayer.getTeamType() == activeTarget.getTeamType()) {
                    if ((activePlayer.getTeamType() != TeamType.WHITE) || (activeTarget.getTeamType() != TeamType.WHITE)) {
                        return;
                    }
                }
            }
        }
        onAttack(event);
    }

    /**
     * @param event
     */
    protected void onAttack(OnAttackEvent event) {
    }

    /**
     * @param event
     * @return true only in the event that an skill not want that continue its normal progress.
     */
    @Override
    public final void listenerOnUseSkill(OnUseSkillEvent event) {
        Playable playable = event.getCaster();
        Character target = event.getTarget();
        SkillTemplate skill = event.getSkill();

        if (!getPlayerEventManager().isPlayableInEvent(playable)) {
            event.setCancel(true);
            return;
        }
        // If the character has no target to finish the listener.
        // XXX Perhaps in any event it is required to use skills without target... check!
        if (target == null) {
            event.setCancel(true);
            return;
        }
        // If the character is using a skill on itself end the listener
        if (playable.equals(target)) {
//			event.setCancel(true);
            return;
        }
        // We get the player involved in our event
        Player activePlayer = getPlayerEventManager().getEventPlayer(playable);
        // Exclude the player from the next Anti Afk control
        if (getAntiAfkManager() != null) {
            getAntiAfkManager().excludePlayer(activePlayer);
        }
        // If our target is L2Playable type, and we do this in the event control
        Player activeTarget = getPlayerEventManager().getEventPlayer(target);
        if (activeTarget != null) {
            if (activeTarget.isProtected()) {
                activePlayer.sendMessage(MessageData.getInstance().getMsgByLang(activePlayer, "spawnprotection_protected", false));
                return;
            }

            if ((skill.isDamage() || skill.isDebuff())) {
                // Remove the spawn protection time
                if (activePlayer.getProtectionTimeEnd() > 0) {
                    activePlayer.setProtectionTimeEnd(0);
                    activePlayer.sendMessage(MessageData.getInstance().getMsgByLang(activePlayer, "spawnprotection_ended", false));
                }

                // Check Friendly Fire
                if (!getMainConfig().isFriendlyFireEnabled() && (activePlayer.getTeamType() == activeTarget.getTeamType())) {
                    if ((activePlayer.getTeamType() != TeamType.WHITE) || (activeTarget.getTeamType() != TeamType.WHITE)) {
                        return;
                    }
                }
            }
        }
        onUseSkill(event);
    }

    /**
     * @param event
     */
    protected void onUseSkill(OnUseSkillEvent event) {
    }

    /**
     * @param event
     * @return Only in the event that an skill not want that continue its normal progress.
     */
    @Override
    public final void listenerOnUseItem(OnUseItemEvent event) {
        Player player = event.getPlayer();
        ItemTemplate item = event.getItem();

        if (!getPlayerEventManager().isPlayableInEvent(player)) {
            event.setCancel(true);
            return;
        }
        // We will not allow the use of pots or scroll
        // XXX It could be set as a theme config pots
        if (item.isScroll() || item.isPotion()) {
            return;
        }
        Player activePlayer = getPlayerEventManager().getEventPlayer(player);
        // Exclude the player from the next Anti Afk control
        if (getAntiAfkManager() != null) {
            getAntiAfkManager().excludePlayer(activePlayer);
        }
        onUseItem(event);
    }

    /**
     * @param event
     */
    protected void onUseItem(OnUseItemEvent event) {
    }

    @Override
    public void listenerOnLogin(OnLogInEvent event) {
        Player player = event.getPlayer();
        if (getPlayerEventManager().isPlayableInEvent(player)) {
            onLogin(event);
        }
    }

    protected void onLogin(OnLogInEvent event) {
        Player player = _playerEventManager.getEventPlayer(event.getPlayer().getObjectId());
        Optional<WorldInstance> world = _instanceWorldManager.getAllInstances().stream().filter(wi -> wi.getInstanceId() == player.getWorldInstanceId()).findFirst();
        world.ifPresent(w -> {
            player.setInstanceWorld(world.get());
            player.addToEvent(_teamsManagers.getPlayerTeam(player));
            player.setProtectionTimeEnd(System.currentTimeMillis() + (getMainConfig().getSpawnProtectionTime() * 1000L));
            player.teleportTo(player.getTeam().getRndSpawn(), true);
            player.getPcInstance().addEventListener(new EventEngineListener(player.getPcInstance()));
            getScoreboardPackets().forEach(p -> player.getPcInstance().sendPacket(p));
            updateScore(player);
        });
    }

    @Override
    public final void listenerOnLogout(OnLogOutEvent event) {
        Player player = event.getPlayer();

        if (getPlayerEventManager().isPlayableInEvent(player)) {
            try {
                Player activePlayer = getPlayerEventManager().getEventPlayer(player);
                EventEngineManager.getInstance().addPlayerDisconnected(activePlayer);
                // Listener
                onLogout(event);
                activePlayer.getOriginalTitle();
                //removePlayerFromEvent(activePlayer, false);
            } catch (Exception e) {
                LOGGER.warning(EventEngineManager.class.getSimpleName() + ": listenerOnLogout() " + e);
                e.printStackTrace();
            }
        }
    }

    protected void onLogout(OnLogOutEvent event) {
    }

    // VARIOUS METHODS. -------------------------------------------------------------------------------- //

    /**
     * Prepare players, teams and the instance to start.
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Cancel any player attack in progress.</li>
     * <li>Cancel any player skill in progress.</li>
     * <li>Paralyzed the player.</li>
     * <li>Cancel all character effects.</li>
     * <li>Cancel summon pet.</li>
     * <li>Cancel all character cubics.</li>
     * <li>Save the return player location.</li>
     * <li>Create the teams.</li>
     * <li>Create the instance world.</li>
     */
    public void prepareToStart() {
        world = _instanceWorldManager.createNewInstanceWorld(getInstanceFile());
        _teamsManagers.createTeams(onCreateTeams(), world.getInstanceId());

        for (Player player : getPlayerEventManager().getAllEventPlayers()) {
            player.cancelAllActions();
            player.cancelAllEffects();
            player.addToEvent(_teamsManagers.getPlayerTeam(player));
            player.setInstanceWorld(world);
            player.teleportTo(_teamsManagers.getPlayerTeam(player).getRndSpawn(), true);
            player.setProtectionTimeEnd(System.currentTimeMillis() + (getMainConfig().getSpawnProtectionTime() * 1000L)); // Milliseconds
            getScoreboardPackets().forEach(packet -> player.getPcInstance().sendPacket(packet));
        }
        addSuscription(ListenerType.ON_PLAYER_MOVE);
    }

    private List<L2GameServerPacket> getScoreboardPackets() {
        List<L2GameServerPacket> packets = new ArrayList<>();

        switch (_config.getType()) {
            case TEAM -> {
                packets.add(new ExCubeGameCloseUI());
                packets.add(new ExCubeGameChangePoints(EventEngineManager.getInstance().getTime(), 0, 0));
                Map<Boolean, List<Player>> redAndBlueTeam = getPlayerEventManager().getAllEventPlayers().stream().collect(Collectors.partitioningBy(player -> player.getTeamType() == TeamType.RED));
                packets.add(new ExCubeGameTeamList(redAndBlueTeam.get(true).stream().map(Player::getPcInstance).collect(Collectors.toList()), redAndBlueTeam.get(false).stream().map(Player::getPcInstance).collect(Collectors.toList()), 0));
                //packets.add(new ExCubeGameCloseUI());
                packets.add(ExBasicActionList.STATIC_PACKET);
            }
            case SINGLE -> {
                List<Participant> participants = getPlayerEventManager().getAllEventPlayers().stream().map(p -> new Participant(p.getName(), 0)).collect(Collectors.toList());
                packets.add(new ExShowPVPMatchRecord(participants));
            }
        }
        return packets;
    }

    /**
     * We prepare the player for the fight.
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>We canceled the paralysis made in <u>prepareToTeleport().</u></li>
     * <li>We deliver buffs defined in configs.</li>
     */
    public void prepareToFight() {
        for (Player ph : getPlayerEventManager().getAllEventPlayers()) {
            ph.giveBuffs(BuffListData.getInstance().getBuffsPlayer(ph));
        }
    }

    /**
     * We prepare the player for the end of the event.
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Cancel any attack in progress.</li>
     * <li>Cancel any skill in progress.</li>
     * <li>Cancel all effects.</li>
     * <li>Recover the title and color of the participants.</li>
     * <li>We canceled the Team.</li>
     * <li>It out of the world we created for the event.</li>
     */
    public void prepareToEnd() {
        stopAllPendingRevive();
        final L2GameServerPacket packet;

        switch (_config.getType()) {
            case TEAM -> {
                boolean isRedWinner = getTeamsManager().getAllTeams().stream().max(Comparator.comparingInt(p -> p.getPoints(getConfig().getScoreType()))).stream().anyMatch(t -> t.getTeamType() == TeamType.RED);
                packet = new ExCubeGameEnd(isRedWinner);
            }
            case SINGLE -> {
                List<Participant> participants = _playerEventManager.getAllEventPlayers().stream().map(p -> new Participant(p.getName(), p.getPoints(ScoreType.KILL))).collect(Collectors.toList());
                packet = new ExShowPVPMatchRecord(participants);
            }
            default -> packet = null;
        }
        _playerEventManager.getAllEventPlayers().stream().filter(p -> p.getPcInstance() != null).forEach(p -> {
            p.revive(getMainConfig().getSpawnProtectionTime());
            p.cancelAllActions();
            p.cancelAllEffects();
            removePlayerFromEvent(p, true);
            if (packet != null) {
                p.getPcInstance().sendPacket(packet);
            }
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerEventParticipated(p.getPcInstance(), true), p.getPcInstance());
        });
        getScheduledEventsManager().cancelTaskControlTime();
        getInstanceWorldManager().destroyAllInstances();
    }

    /**
     * We generated a task to revive a character.
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Generate a pause before executing any action.</li>
     * <li>Revive the character.</li>
     * <li>We give you the buff depending on the event in which this.</li>
     * <li>Teleport the character depending on the event in this.</li>
     * <li>We do invulnerable for 5 seconds and not allow it to move.</li>
     * <li>We canceled the invul and let you move.</li>
     *
     * @param player
     * @param time
     * @param radiusTeleport
     */
    public void scheduleRevivePlayer(final Player player, int time, int radiusTeleport) {
        try {
            EventUtil.sendEventMessage(player, MessageData.getInstance().getMsgByLang(player, "revive_in", true).replace("%time%", time + ""));
            _revivePending.add(ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                player.revive(getMainConfig().getSpawnProtectionTime());
                player.giveBuffs(BuffListData.getInstance().getBuffsPlayer(player));
                player.teleportTo(_teamsManagers.getPlayerTeam(player).getRndSpawn(), true);
            }, time * 1000L));
        } catch (Exception e) {
            LOGGER.warning(BaseEvent.class.getSimpleName() + ": " + e);
            e.printStackTrace();
        }
    }

    public void scheduleRevivePlayer(final Player player, int time) {
        scheduleRevivePlayer(player, time, 0);
    }

    /**
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Recover original title.</li>
     * <li>Recover original color title.</li>
     * <li>Remove from instance and back 0</li>
     *
     * @param ph
     * @param forceRemove
     */
    public void removePlayerFromEvent(Player ph, boolean forceRemove) {
        ph.teleportTo(ph.getReturnLocation(), false);
        ph.getTeam().removeMember(ph);
        ph.getOriginalTitle();
        ph.removeFromEvent();

        if (forceRemove) {
            getPlayerEventManager().getAllEventPlayers().remove(ph);
        }
    }

    /**
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Add a suscription to one type of events</li>
     *
     * @param type: ListenerType
     */
    protected final void addSuscription(ListenerType type) {
        ListenerDispatcher.getInstance().addSuscription(type, this);
    }

    /**
     * <ul>
     * <b>Actions:</b>
     * </ul>
     * <li>Remove the suscription to one type of events</li>
     *
     * @param type: ListenerType
     */
    protected final void removeSubscription(ListenerType type) {
        ListenerDispatcher.getInstance().removeSuscription(type, this);
    }
}