package com.l2jserver.datapack.custom.achievement;

import com.l2jserver.gameserver.data.sql.impl.ClanTable;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.OnDoorAction;
import com.l2jserver.gameserver.model.events.impl.character.*;
import com.l2jserver.gameserver.model.events.impl.character.npc.*;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.*;
import com.l2jserver.gameserver.model.events.impl.character.playable.OnPlayableExpChanged;
import com.l2jserver.gameserver.model.events.impl.character.player.*;
import com.l2jserver.gameserver.model.events.impl.character.player.clan.*;
import com.l2jserver.gameserver.model.events.impl.character.player.clanwh.OnPlayerClanWHItemAdd;
import com.l2jserver.gameserver.model.events.impl.character.player.clanwh.OnPlayerClanWHItemDestroy;
import com.l2jserver.gameserver.model.events.impl.character.player.clanwh.OnPlayerClanWHItemTransfer;
import com.l2jserver.gameserver.model.events.impl.character.player.inventory.*;
import com.l2jserver.gameserver.model.events.impl.character.trap.OnTrapAction;
import com.l2jserver.gameserver.model.events.impl.clan.OnClanReputationChanged;
import com.l2jserver.gameserver.model.events.impl.clan.OnClanWarFinish;
import com.l2jserver.gameserver.model.events.impl.clan.OnClanWarStart;
import com.l2jserver.gameserver.model.events.impl.events.OnTvTEventFinish;
import com.l2jserver.gameserver.model.events.impl.events.OnTvTEventKill;
import com.l2jserver.gameserver.model.events.impl.events.OnTvTEventRegistrationStart;
import com.l2jserver.gameserver.model.events.impl.events.OnTvTEventStart;
import com.l2jserver.gameserver.model.events.impl.item.OnItemBypassEvent;
import com.l2jserver.gameserver.model.events.impl.item.OnItemCreate;
import com.l2jserver.gameserver.model.events.impl.item.OnItemTalk;
import com.l2jserver.gameserver.model.events.impl.item.OnItemUse;
import com.l2jserver.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import com.l2jserver.gameserver.model.events.impl.sieges.castle.OnCastleSiegeFinish;
import com.l2jserver.gameserver.model.events.impl.sieges.castle.OnCastleSiegeOwnerChange;
import com.l2jserver.gameserver.model.events.impl.sieges.castle.OnCastleSiegeStart;
import com.l2jserver.gameserver.model.events.impl.sieges.fort.OnFortSiegeFinish;
import com.l2jserver.gameserver.model.events.impl.sieges.fort.OnFortSiegeStart;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.util.List;
import java.util.stream.Collectors;

public class EventDispatcher implements EventListenerInterface<IBaseEvent> {
    private final AchievementManager achievementManager;

    public EventDispatcher(AchievementManager achievementManager) {
        this.achievementManager = achievementManager;
    }

    @Override
    public void execute(IBaseEvent event) {
        hookOnEvent(event);
    }

    private void executeEventOnPlayer(L2PcInstance player, IBaseEvent event) {
        achievementManager.progressAchievement(player, event);
    }

    public void onAttackableAggroRangeEnter(OnAttackableAggroRangeEnter onAttackableAggroRangeEnter) {
        executeEventOnPlayer(onAttackableAggroRangeEnter.getActiveChar(), onAttackableAggroRangeEnter);
    }

    public void onAttackableAttack(OnAttackableAttack onAttackableAttack) {
        executeEventOnPlayer(onAttackableAttack.getAttacker(), onAttackableAttack);
    }

    public void onAttackableFactionCall(OnAttackableFactionCall onAttackableFactionCall) {
        if (onAttackableFactionCall.getAttacker().getParty() != null) {
            onAttackableFactionCall.getAttacker().getParty().getMembers().forEach(p -> executeEventOnPlayer(p, onAttackableFactionCall));
            return;
        }
        executeEventOnPlayer(onAttackableFactionCall.getAttacker(), onAttackableFactionCall);
    }

    public void onAttackableKill(OnAttackableKill onAttackableKill) {
        if (onAttackableKill.getAttacker().getParty() != null) {
            onAttackableKill.getAttacker().getParty().getMembers().forEach(p -> executeEventOnPlayer(p, onAttackableKill));
            return;
        }
        executeEventOnPlayer(onAttackableKill.getAttacker(), onAttackableKill);
    }

    public void onGrandBossKill(OnGrandBossKill onGrandBossKill) {
        if (onGrandBossKill.getActiveChar().getParty() != null) {
            onGrandBossKill.getActiveChar().getParty().getMembers().forEach(p -> executeEventOnPlayer(p, onGrandBossKill));
            return;
        }
        executeEventOnPlayer(onGrandBossKill.getActiveChar().getActingPlayer(), onGrandBossKill);
    }

    public void onCastleSiegeFinish(OnCastleSiegeFinish onCastleSiegeFinish) {
        List<L2PcInstance> defenders = onCastleSiegeFinish.getSiege().getDefenderClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.CASTLE))
                .collect(Collectors.toList());
        List<L2PcInstance> attackers = onCastleSiegeFinish.getSiege().getAttackerClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.CASTLE))
                .collect(Collectors.toList());

        defenders.forEach(d -> executeEventOnPlayer(d, onCastleSiegeFinish));
        attackers.forEach(a -> executeEventOnPlayer(a, onCastleSiegeFinish));

    }

    public void onCastleSiegeOwnerChange(OnCastleSiegeOwnerChange onCastleSiegeOwnerChange) {
    }

    public void onCastleSiegeStart(OnCastleSiegeStart onCastleSiegeStart) {
        List<L2PcInstance> defenders = onCastleSiegeStart.getSiege().getDefenderClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.CASTLE))
                .collect(Collectors.toList());
        List<L2PcInstance> attackers = onCastleSiegeStart.getSiege().getAttackerClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.CASTLE))
                .collect(Collectors.toList());

        defenders.forEach(d -> executeEventOnPlayer(d, onCastleSiegeStart));
        attackers.forEach(a -> executeEventOnPlayer(a, onCastleSiegeStart));
    }

    public void onClanWarFinish(OnClanWarFinish onClanWarFinish) {
        onClanWarFinish.getClan1().getOnlineMembers(-1).forEach(p -> executeEventOnPlayer(p, onClanWarFinish));
        onClanWarFinish.getClan2().getOnlineMembers(-1).forEach(p -> executeEventOnPlayer(p, onClanWarFinish));
    }

    public void onClanWarStart(OnClanWarStart onClanWarStart) {
        onClanWarStart.getClan1().getOnlineMembers(-1).forEach(p -> executeEventOnPlayer(p, onClanWarStart));
        onClanWarStart.getClan2().getOnlineMembers(-1).forEach(p -> executeEventOnPlayer(p, onClanWarStart));
    }

    public void onClanReputationChanged(OnClanReputationChanged onClanReputationChanged) {
        if (!onClanReputationChanged.getClan().getLeader().isOnline()) {
            return;
        }
        executeEventOnPlayer(onClanReputationChanged.getClan().getLeader().getPlayerInstance(), onClanReputationChanged);
    }

    public void onCreatureAttack(OnCreatureAttack onCreatureAttack) {
        if (onCreatureAttack.getAttacker() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureAttack.getAttacker().getActingPlayer(), onCreatureAttack);
        }
    }

    public void onCreatureAttackAvoid(OnCreatureAttackAvoid onCreatureAttackAvoid) {
        if (onCreatureAttackAvoid.getAttacker() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureAttackAvoid.getAttacker().getActingPlayer(), onCreatureAttackAvoid);
        }
    }

    public void onCreatureAttacked(OnCreatureAttacked onCreatureAttacked) {
        if (onCreatureAttacked.getAttacker() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureAttacked.getAttacker().getActingPlayer(), onCreatureAttacked);
        }
    }

    public void onCreatureDamageReceived(OnCreatureDamageReceived onCreatureDamageReceived) {
        if (onCreatureDamageReceived.getAttacker() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureDamageReceived.getAttacker().getActingPlayer(), onCreatureDamageReceived);
        }
    }

    public void onCreatureDamageDealt(OnCreatureDamageDealt onCreatureDamageDealt) {
        if (onCreatureDamageDealt.getAttacker() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureDamageDealt.getAttacker().getActingPlayer(), onCreatureDamageDealt);
        }
    }

    public void onCreatureKill(OnCreatureKill onCreatureKill) {
        if (onCreatureKill.getAttacker() instanceof L2Playable) {
            if (onCreatureKill.getAttacker().getParty() != null) {
                onCreatureKill.getAttacker().getParty().getMembers().forEach(p -> executeEventOnPlayer(p.getActingPlayer(), onCreatureKill));
                return;
            }
            executeEventOnPlayer(onCreatureKill.getAttacker().getActingPlayer(), onCreatureKill);
        }
    }

    public void onCreatureSkillUse(OnCreatureSkillUse onCreatureSkillUse) {
        if (onCreatureSkillUse.getCaster() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureSkillUse.getCaster().getActingPlayer(), onCreatureSkillUse);
        }

    }

    public void onCreatureTeleported(OnCreatureTeleported onCreatureTeleported) {
        if (onCreatureTeleported.getCreature() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureTeleported.getCreature().getActingPlayer(), onCreatureTeleported);
        }
    }

    public void onCreatureZoneEnter(OnCreatureZoneEnter onCreatureZoneEnter) {
        if (onCreatureZoneEnter.getCreature() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureZoneEnter.getCreature().getActingPlayer(), onCreatureZoneEnter);
        }
    }

    public void onCreatureZoneExit(OnCreatureZoneExit onCreatureZoneExit) {
        if (onCreatureZoneExit.getCreature() instanceof L2Playable) {
            executeEventOnPlayer(onCreatureZoneExit.getCreature().getActingPlayer(), onCreatureZoneExit);
        }
    }

    public void onFortSiegeFinish(OnFortSiegeFinish onFortSiegeFinish) {
        List<L2PcInstance> defenders = onFortSiegeFinish.getSiege().getDefenderClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.FORT))
                .collect(Collectors.toList());
        List<L2PcInstance> attackers = onFortSiegeFinish.getSiege().getAttackerClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.FORT))
                .collect(Collectors.toList());
        defenders.forEach(p -> executeEventOnPlayer(p, onFortSiegeFinish));
        attackers.forEach(p -> executeEventOnPlayer(p, onFortSiegeFinish));

    }

    public void onFortSiegeStart(OnFortSiegeStart onFortSiegeStart) {
        List<L2PcInstance> defenders = onFortSiegeStart.getSiege().getDefenderClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.FORT))
                .collect(Collectors.toList());
        List<L2PcInstance> attackers = onFortSiegeStart.getSiege().getAttackerClans().stream()
                .flatMap(c -> ClanTable.getInstance().getClan(c.getClanId()).getOnlineMembers(-1).stream())
                .filter(p -> p.isInsideZone(ZoneId.FORT))
                .collect(Collectors.toList());
        defenders.forEach(p -> executeEventOnPlayer(p, onFortSiegeStart));
        attackers.forEach(p -> executeEventOnPlayer(p, onFortSiegeStart));
    }

    public void onItemBypassEvent(OnItemBypassEvent onItemBypassEvent) {
        executeEventOnPlayer(onItemBypassEvent.getActiveChar(), onItemBypassEvent);
    }

    public void onItemCreate(OnItemCreate onItemCreate) {
        executeEventOnPlayer(onItemCreate.getActiveChar(), onItemCreate);
    }

    public void onItemTalk(OnItemTalk onItemTalk) {
        executeEventOnPlayer(onItemTalk.getActiveChar(), onItemTalk);
    }

    public void onItemUse(OnItemUse onItemUse) {
        executeEventOnPlayer(onItemUse.getPlayer(), onItemUse);
    }

    public void onNpcCanBeSeen(OnNpcCanBeSeen onNpcCanBeSeen) {
        executeEventOnPlayer(onNpcCanBeSeen.getActiveChar(), onNpcCanBeSeen);
    }

    public void onNpcCreatureSee(OnNpcCreatureSee onNpcCreatureSee) {
        if (onNpcCreatureSee.getCreature() instanceof L2Playable) {
            executeEventOnPlayer(onNpcCreatureSee.getCreature().getActingPlayer(), onNpcCreatureSee);
        }
    }

    public void onNpcEventReceived(OnNpcEventReceived onNpcEventReceived) {
    }

    public void onNpcFirstTalk(OnNpcFirstTalk onNpcFirstTalk) {
        executeEventOnPlayer(onNpcFirstTalk.getActiveChar(), onNpcFirstTalk);
    }

    public void onAttackableHate(OnAttackableHate onAttackableHate) {
        executeEventOnPlayer(onAttackableHate.getActiveChar(), onAttackableHate);
    }

    public void onNpcMoveFinished(OnNpcMoveFinished onNpcMoveFinished) {
    }

    public void onNpcMoveNodeArrived(OnNpcMoveNodeArrived onNpcMoveNodeArrived) {
    }

    public void onNpcMoveRouteFinished(OnNpcMoveRouteFinished onNpcMoveRouteFinished) {
    }

    public void onNpcSkillFinished(OnNpcSkillFinished onNpcSkillFinished) {
        executeEventOnPlayer(onNpcSkillFinished.getTarget(), onNpcSkillFinished);
    }

    public void onNpcSkillSee(OnNpcSkillSee onNpcSkillSee) {
        executeEventOnPlayer(onNpcSkillSee.getCaster(), onNpcSkillSee);
    }

    public void onNpcSpawn(OnNpcSpawn onNpcSpawn) {
    }

    public void onNpcTeleport(OnNpcTeleport onNpcTeleport) {
    }

    public void onNpcManorBypass(OnNpcManorBypass onNpcManorBypass) {
        executeEventOnPlayer(onNpcManorBypass.getActiveChar(), onNpcManorBypass);
    }

    public void onDoorAction(OnDoorAction onDoorAction) {
        executeEventOnPlayer(onDoorAction.getPlayer(), onDoorAction);
    }

    public void onOlympiadMatchResult(OnOlympiadMatchResult onOlympiadMatchResult) {
        executeEventOnPlayer(onOlympiadMatchResult.getWinner().getPlayer(), onOlympiadMatchResult);
        executeEventOnPlayer(onOlympiadMatchResult.getLoser().getPlayer(), onOlympiadMatchResult);
    }

    public void onPlayableExpChanged(OnPlayableExpChanged onPlayableExpChanged) {
        executeEventOnPlayer(onPlayableExpChanged.getActiveChar().getActingPlayer(), onPlayableExpChanged);
    }

    public void onPlayerAugment(OnPlayerAugment onPlayerAugment) {
        executeEventOnPlayer(onPlayerAugment.getActiveChar(), onPlayerAugment);
    }

    public void onPlayerBypass(OnPlayerBypass onPlayerBypass) {
        executeEventOnPlayer(onPlayerBypass.getActiveChar(), onPlayerBypass);
    }

    public void onPlayerChat(OnPlayerChat onPlayerChat) {
        executeEventOnPlayer(onPlayerChat.getActiveChar(), onPlayerChat);
    }

    public void onPlayerTutorialEvent(OnPlayerTutorialEvent onPlayerTutorialEvent) {
        executeEventOnPlayer(onPlayerTutorialEvent.getActiveChar(), onPlayerTutorialEvent);
    }

    public void onPlayerTutorialCmd(OnPlayerTutorialCmd onPlayerTutorialCmd) {
        executeEventOnPlayer(onPlayerTutorialCmd.getActiveChar(), onPlayerTutorialCmd);
    }

    public void onPlayerTutorialClientEvent(OnPlayerTutorialClientEvent onPlayerTutorialClientEvent) {
        executeEventOnPlayer(onPlayerTutorialClientEvent.getActiveChar(), onPlayerTutorialClientEvent);
    }

    public void onPlayerTutorialQuestionMark(OnPlayerTutorialQuestionMark onPlayerTutorialQuestionMark) {
        executeEventOnPlayer(onPlayerTutorialQuestionMark.getActiveChar(), onPlayerTutorialQuestionMark);
    }

    public void onPlayerClanCreate(OnPlayerClanCreate onPlayerClanCreate) {
        executeEventOnPlayer(onPlayerClanCreate.getActiveChar(), onPlayerClanCreate);
    }

    public void onPlayerClanDestroy(OnPlayerClanDestroy onPlayerClanDestroy) {
        executeEventOnPlayer(onPlayerClanDestroy.getActiveChar().getPlayerInstance(), onPlayerClanDestroy);
    }

    public void onPlayerClanJoin(OnPlayerClanJoin onPlayerClanJoin) {
        executeEventOnPlayer(onPlayerClanJoin.getActiveChar().getPlayerInstance(), onPlayerClanJoin);
    }

    public void onPlayerClanLeaderChange(OnPlayerClanLeaderChange onPlayerClanLeaderChange) {
        executeEventOnPlayer(onPlayerClanLeaderChange.getNewLeader().getPlayerInstance(), onPlayerClanLeaderChange);
        executeEventOnPlayer(onPlayerClanLeaderChange.getOldLeader().getPlayerInstance(), onPlayerClanLeaderChange);
    }

    public void onPlayerClanLeft(OnPlayerClanLeft onPlayerClanLeft) {
        executeEventOnPlayer(onPlayerClanLeft.getActiveChar().getPlayerInstance(), onPlayerClanLeft);
    }

    public void onPlayerClanLvlUp(OnPlayerClanLvlUp onPlayerClanLvlUp) {
        executeEventOnPlayer(onPlayerClanLvlUp.getClan().getLeader().getPlayerInstance(), onPlayerClanLvlUp);
    }

    public void onPlayerClanWHItemAdd(OnPlayerClanWHItemAdd onPlayerClanWHItemAdd) {
        executeEventOnPlayer(onPlayerClanWHItemAdd.getActiveChar(), onPlayerClanWHItemAdd);
    }

    public void onPlayerClanWHItemDestroy(OnPlayerClanWHItemDestroy onPlayerClanWHItemDestroy) {
        executeEventOnPlayer(onPlayerClanWHItemDestroy.getActiveChar(), onPlayerClanWHItemDestroy);
    }

    public void onPlayerClanWHItemTransfer(OnPlayerClanWHItemTransfer onPlayerClanWHItemTransfer) {
        executeEventOnPlayer(onPlayerClanWHItemTransfer.getActiveChar(), onPlayerClanWHItemTransfer);
    }

    public void onPlayerCreate(OnPlayerCreate onPlayerCreate) {
//        executeEventOnPlayer(onPlayerCreate.getActiveChar(), onPlayerCreate);
    }

    public void onPlayerDelete(OnPlayerDelete onPlayerDelete) {
//        executeEventOnPlayer(onPlayerDelete.getClient().getActiveChar(), onPlayerDelete);
    }

    public void onPlayerDlgAnswer(OnPlayerDlgAnswer onPlayerDlgAnswer) {
        executeEventOnPlayer(onPlayerDlgAnswer.getActiveChar(), onPlayerDlgAnswer);
    }

    public void onPlayerEquipItem(OnPlayerEquipItem onPlayerEquipItem) {
        executeEventOnPlayer(onPlayerEquipItem.getActiveChar(), onPlayerEquipItem);
    }

    public void onPlayerUnequipItem(OnPlayerUnequipItem onPlayerUnequipItem) {
        executeEventOnPlayer(onPlayerUnequipItem.getActiveChar(), onPlayerUnequipItem);
    }

    public void onPlayerFameChanged(OnPlayerFameChanged onPlayerFameChanged) {
        executeEventOnPlayer(onPlayerFameChanged.getActiveChar(), onPlayerFameChanged);
    }

    public void onPlayerHennaAdd(OnPlayerHennaAdd onPlayerHennaAdd) {
        executeEventOnPlayer(onPlayerHennaAdd.getActiveChar(), onPlayerHennaAdd);
    }

    public void onPlayerHennaRemove(OnPlayerHennaRemove onPlayerHennaRemove) {
        executeEventOnPlayer(onPlayerHennaRemove.getActiveChar(), onPlayerHennaRemove);
    }

    public void onPlayerItemAdd(OnPlayerItemAdd onPlayerItemAdd) {
        executeEventOnPlayer(onPlayerItemAdd.getActiveChar(), onPlayerItemAdd);
    }

    public void onPlayerItemDestroy(OnPlayerItemDestroy onPlayerItemDestroy) {
        executeEventOnPlayer(onPlayerItemDestroy.getActiveChar(), onPlayerItemDestroy);
    }

    public void onPlayerItemDrop(OnPlayerItemDrop onPlayerItemDrop) {
        executeEventOnPlayer(onPlayerItemDrop.getActiveChar(), onPlayerItemDrop);
    }

    public void onPlayerItemPickup(OnPlayerItemPickup onPlayerItemPickup) {
        executeEventOnPlayer(onPlayerItemPickup.getActiveChar(), onPlayerItemPickup);
    }

    public void onPlayerItemTransfer(OnPlayerItemTransfer onPlayerItemTransfer) {
        executeEventOnPlayer(onPlayerItemTransfer.getActiveChar(), onPlayerItemTransfer);
    }

    public void onPlayerKarmaChanged(OnPlayerKarmaChanged onPlayerKarmaChanged) {
        executeEventOnPlayer(onPlayerKarmaChanged.getActiveChar(), onPlayerKarmaChanged);
    }

    public void onPlayerLevelChanged(OnPlayerLevelChanged onPlayerLevelChanged) {
        executeEventOnPlayer(onPlayerLevelChanged.getActiveChar(), onPlayerLevelChanged);
        achievementManager.onLevelChanged(onPlayerLevelChanged.getActiveChar());
    }

    public void onPlayerLogin(OnPlayerLogin onPlayerLogin) {
        executeEventOnPlayer(onPlayerLogin.getActiveChar(), onPlayerLogin);
        achievementManager.onPlayerLogin(onPlayerLogin.getActiveChar());
    }

    public void onPlayerLogout(OnPlayerLogout onPlayerLogout) {
        executeEventOnPlayer(onPlayerLogout.getActiveChar(), onPlayerLogout);
        achievementManager.onPlayerLogout(onPlayerLogout.getActiveChar());
    }

    public void onPlayerPKChanged(OnPlayerPKChanged onPlayerPKChanged) {
        executeEventOnPlayer(onPlayerPKChanged.getActiveChar(), onPlayerPKChanged);
    }

    public void onPlayerProfessionChange(OnPlayerProfessionChange onPlayerProfessionChange) {
        executeEventOnPlayer(onPlayerProfessionChange.getActiveChar(), onPlayerProfessionChange);
        achievementManager.onProfessionChange(onPlayerProfessionChange.getActiveChar());
    }

    public void onPlayerProfessionCancel(OnPlayerProfessionCancel onPlayerProfessionCancel) {
        executeEventOnPlayer(onPlayerProfessionCancel.getActiveChar(), onPlayerProfessionCancel);
        achievementManager.onProfessionCancel(onPlayerProfessionCancel.getActiveChar());
    }

    public void onPlayerPvPChanged(OnPlayerPvPChanged onPlayerPvPChanged) {
        executeEventOnPlayer(onPlayerPvPChanged.getActiveChar(), onPlayerPvPChanged);
    }

    public void onPlayerPvPKill(OnPlayerPvPKill onPlayerPvPKill) {
        executeEventOnPlayer(onPlayerPvPKill.getActiveChar(), onPlayerPvPKill);
    }

    public void onPlayerRestore(OnPlayerRestore onPlayerRestore) {
    }

    public void onPlayerSelect(OnPlayerSelect onPlayerSelect) {
    }

    public void onPlayerSkillLearn(OnPlayerSkillLearn onPlayerSkillLearn) {
        executeEventOnPlayer(onPlayerSkillLearn.getActiveChar(), onPlayerSkillLearn);
    }

    public void onPlayerSit(OnPlayerSit onPlayerSit) {
        executeEventOnPlayer(onPlayerSit.getActiveChar(), onPlayerSit);
    }

    public void onPlayerSummonSpawn(OnPlayerSummonSpawn onPlayerSummonSpawn) {
        executeEventOnPlayer(onPlayerSummonSpawn.getSummon().getActingPlayer(), onPlayerSummonSpawn);
    }

    public void onPlayerSummonTalk(OnPlayerSummonTalk onPlayerSummonTalk) {
        executeEventOnPlayer(onPlayerSummonTalk.getSummon().getActingPlayer(), onPlayerSummonTalk);
    }

    public void onPlayerTransform(OnPlayerTransform onPlayerTransform) {
        executeEventOnPlayer(onPlayerTransform.getActiveChar(), onPlayerTransform);
    }

    public void onPlayerAssistKill(OnPlayerAssistKill onPlayerAssistKill) {
        executeEventOnPlayer(onPlayerAssistKill.getActiveChar(), onPlayerAssistKill);
    }

    public void onPlayerPkKill(OnPlayerPkKill onPlayerPkKill) {
        executeEventOnPlayer(onPlayerPkKill.getActiveChar(), onPlayerPkKill);
    }

    public void onPlayerPartyRequest(OnPlayerPartyRequest onPlayerPartyRequest) {
        executeEventOnPlayer(onPlayerPartyRequest.getPlayer(), onPlayerPartyRequest);
    }

    public void onPlayerTowerCapture(OnPlayerTowerCapture onPlayerTowerCapture) {
        executeEventOnPlayer(onPlayerTowerCapture.getActiveChar(), onPlayerTowerCapture);
    }

    public void onPlayerPartyJoin(OnPlayerPartyJoin onPlayerPartyJoin) {
        executeEventOnPlayer(onPlayerPartyJoin.getActiveChar(), onPlayerPartyJoin);
    }

    public void onPlayerPartyLeave(OnPlayerPartyLeave onPlayerPartyLeave) {
        executeEventOnPlayer(onPlayerPartyLeave.getActiveChar(), onPlayerPartyLeave);
    }

    public void onPlayerUseTeleportToLocation(OnPlayerUseTeleportToLocation onPlayerUseTeleportToLocation) {
        executeEventOnPlayer(onPlayerUseTeleportToLocation.getPlayer(), onPlayerUseTeleportToLocation);
    }

    public void onPlayerTitleChange(OnPlayerTitleChange onPlayerTitleChange) {
        if (onPlayerTitleChange.getPlayer() instanceof L2PcInstance) {
            executeEventOnPlayer(onPlayerTitleChange.getPlayer().getActingPlayer(), onPlayerTitleChange);
        }
    }
    public void onPlayerEventParticipated(OnPlayerEventParticipated onPlayerEventParticipated){
        executeEventOnPlayer(onPlayerEventParticipated.getActiveChar(),onPlayerEventParticipated);
    }
    public void onTrapAction(OnTrapAction onTrapAction) {
        executeEventOnPlayer(onTrapAction.getTrap().getActingPlayer(), onTrapAction);
    }

    public void onTvTEventFinish(OnTvTEventFinish onTvTEventFinish) {
    }

    public void onTvTEventKill(OnTvTEventKill onTvTEventKill) {
        executeEventOnPlayer(onTvTEventKill.getKiller(), onTvTEventKill);
    }

    public void onTvTEventRegistrationStart(OnTvTEventRegistrationStart onTvTEventRegistrationStart) {
    }

    public void onTvTEventStart(OnTvTEventStart onTvTEventStart) {
    }

    private void hookOnEvent(IBaseEvent event) {
        switch (event.getType()) {
            case ON_ATTACKABLE_AGGRO_RANGE_ENTER -> onAttackableAggroRangeEnter((OnAttackableAggroRangeEnter) event);
            case ON_ATTACKABLE_ATTACK -> onAttackableAttack((OnAttackableAttack) event);
            case ON_ATTACKABLE_FACTION_CALL -> onAttackableFactionCall((OnAttackableFactionCall) event);
            case ON_ATTACKABLE_KILL -> onAttackableKill((OnAttackableKill) event);
            case ON_GRANDBOSS_KILL -> onGrandBossKill((OnGrandBossKill) event);
            case ON_CASTLE_SIEGE_FINISH -> onCastleSiegeFinish((OnCastleSiegeFinish) event);
            case ON_CASTLE_SIEGE_OWNER_CHANGE -> onCastleSiegeOwnerChange((OnCastleSiegeOwnerChange) event);
            case ON_CASTLE_SIEGE_START -> onCastleSiegeStart((OnCastleSiegeStart) event);
            case ON_CLAN_WAR_FINISH -> onClanWarFinish((OnClanWarFinish) event);
            case ON_CLAN_WAR_START -> onClanWarStart((OnClanWarStart) event);
            case ON_CLAN_REPUTATION_CHANGED -> onClanReputationChanged((OnClanReputationChanged) event);
            case ON_CREATURE_ATTACK -> onCreatureAttack((OnCreatureAttack) event);
            case ON_CREATURE_ATTACK_AVOID -> onCreatureAttackAvoid((OnCreatureAttackAvoid) event);
            case ON_CREATURE_ATTACKED -> onCreatureAttacked((OnCreatureAttacked) event);
            case ON_CREATURE_DAMAGE_RECEIVED -> onCreatureDamageReceived((OnCreatureDamageReceived) event);
            case ON_CREATURE_DAMAGE_DEALT -> onCreatureDamageDealt((OnCreatureDamageDealt) event);
            case ON_CREATURE_KILL -> onCreatureKill((OnCreatureKill) event);
            case ON_CREATURE_SKILL_USE -> onCreatureSkillUse((OnCreatureSkillUse) event);
            case ON_CREATURE_TELEPORTED -> onCreatureTeleported((OnCreatureTeleported) event);
            case ON_CREATURE_ZONE_ENTER -> onCreatureZoneEnter((OnCreatureZoneEnter) event);
            case ON_CREATURE_ZONE_EXIT -> onCreatureZoneExit((OnCreatureZoneExit) event);
            case ON_FORT_SIEGE_FINISH -> onFortSiegeFinish((OnFortSiegeFinish) event);
            case ON_FORT_SIEGE_START -> onFortSiegeStart((OnFortSiegeStart) event);
            case ON_ITEM_BYPASS_EVENT -> onItemBypassEvent((OnItemBypassEvent) event);
            case ON_ITEM_CREATE -> onItemCreate((OnItemCreate) event);
            case ON_ITEM_TALK -> onItemTalk((OnItemTalk) event);
            case ON_ITEM_USE -> onItemUse((OnItemUse) event);
            case ON_NPC_CAN_BE_SEEN -> onNpcCanBeSeen((OnNpcCanBeSeen) event);
            case ON_NPC_CREATURE_SEE -> onNpcCreatureSee((OnNpcCreatureSee) event);
            case ON_NPC_EVENT_RECEIVED -> onNpcEventReceived((OnNpcEventReceived) event);
            case ON_NPC_FIRST_TALK -> onNpcFirstTalk((OnNpcFirstTalk) event);
            case ON_NPC_HATE -> onAttackableHate((OnAttackableHate) event);
            case ON_NPC_MOVE_FINISHED -> onNpcMoveFinished((OnNpcMoveFinished) event);
            case ON_NPC_MOVE_NODE_ARRIVED -> onNpcMoveNodeArrived((OnNpcMoveNodeArrived) event);
            case ON_NPC_MOVE_ROUTE_FINISHED -> onNpcMoveRouteFinished((OnNpcMoveRouteFinished) event);
            case ON_NPC_SKILL_FINISHED -> onNpcSkillFinished((OnNpcSkillFinished) event);
            case ON_NPC_SKILL_SEE -> onNpcSkillSee((OnNpcSkillSee) event);
            case ON_NPC_SPAWN -> onNpcSpawn((OnNpcSpawn) event);
            case ON_NPC_TELEPORT -> onNpcTeleport((OnNpcTeleport) event);
            case ON_NPC_MANOR_BYPASS -> onNpcManorBypass((OnNpcManorBypass) event);
            case ON_DOOR_ACTION -> onDoorAction((OnDoorAction) event);
            case ON_OLYMPIAD_MATCH_RESULT -> onOlympiadMatchResult((OnOlympiadMatchResult) event);
            case ON_PLAYABLE_EXP_CHANGED -> onPlayableExpChanged((OnPlayableExpChanged) event);
            case ON_PLAYER_AUGMENT -> onPlayerAugment((OnPlayerAugment) event);
            case ON_PLAYER_BYPASS -> onPlayerBypass((OnPlayerBypass) event);
            case ON_PLAYER_CHAT -> onPlayerChat((OnPlayerChat) event);
            case ON_PLAYER_TUTORIAL_EVENT -> onPlayerTutorialEvent((OnPlayerTutorialEvent) event);
            case ON_PLAYER_TUTORIAL_CMD -> onPlayerTutorialCmd((OnPlayerTutorialCmd) event);
            case ON_PLAYER_TUTORIAL_CLIENT_EVENT -> onPlayerTutorialClientEvent((OnPlayerTutorialClientEvent) event);
            case ON_PLAYER_TUTORIAL_QUESTION_MARK -> onPlayerTutorialQuestionMark((OnPlayerTutorialQuestionMark) event);
            case ON_PLAYER_CLAN_CREATE -> onPlayerClanCreate((OnPlayerClanCreate) event);
            case ON_PLAYER_CLAN_DESTROY -> onPlayerClanDestroy((OnPlayerClanDestroy) event);
            case ON_PLAYER_CLAN_JOIN -> onPlayerClanJoin((OnPlayerClanJoin) event);
            case ON_PLAYER_CLAN_LEADER_CHANGE -> onPlayerClanLeaderChange((OnPlayerClanLeaderChange) event);
            case ON_PLAYER_CLAN_LEFT -> onPlayerClanLeft((OnPlayerClanLeft) event);
            case ON_PLAYER_CLAN_LVLUP -> onPlayerClanLvlUp((OnPlayerClanLvlUp) event);
            case ON_PLAYER_CLAN_WH_ITEM_ADD -> onPlayerClanWHItemAdd((OnPlayerClanWHItemAdd) event);
            case ON_PLAYER_CLAN_WH_ITEM_DESTROY -> onPlayerClanWHItemDestroy((OnPlayerClanWHItemDestroy) event);
            case ON_PLAYER_CLAN_WH_ITEM_TRANSFER -> onPlayerClanWHItemTransfer((OnPlayerClanWHItemTransfer) event);
            case ON_PLAYER_CREATE -> onPlayerCreate((OnPlayerCreate) event);
            case ON_PLAYER_DELETE -> onPlayerDelete((OnPlayerDelete) event);
            case ON_PLAYER_DLG_ANSWER -> onPlayerDlgAnswer((OnPlayerDlgAnswer) event);
            case ON_PLAYER_EQUIP_ITEM -> onPlayerEquipItem((OnPlayerEquipItem) event);
            case ON_PLAYER_UNEQUIP_ITEM -> onPlayerUnequipItem((OnPlayerUnequipItem) event);
            case ON_PLAYER_FAME_CHANGED -> onPlayerFameChanged((OnPlayerFameChanged) event);
            case ON_PLAYER_HENNA_ADD -> onPlayerHennaAdd((OnPlayerHennaAdd) event);
            case ON_PLAYER_HENNA_REMOVE -> onPlayerHennaRemove((OnPlayerHennaRemove) event);
            case ON_PLAYER_ITEM_ADD -> onPlayerItemAdd((OnPlayerItemAdd) event);
            case ON_PLAYER_ITEM_DESTROY -> onPlayerItemDestroy((OnPlayerItemDestroy) event);
            case ON_PLAYER_ITEM_DROP -> onPlayerItemDrop((OnPlayerItemDrop) event);
            case ON_PLAYER_ITEM_PICKUP -> onPlayerItemPickup((OnPlayerItemPickup) event);
            case ON_PLAYER_ITEM_TRANSFER -> onPlayerItemTransfer((OnPlayerItemTransfer) event);
            case ON_PLAYER_KARMA_CHANGED -> onPlayerKarmaChanged((OnPlayerKarmaChanged) event);
            case ON_PLAYER_LEVEL_CHANGED -> onPlayerLevelChanged((OnPlayerLevelChanged) event);
            case ON_PLAYER_LOGIN -> onPlayerLogin((OnPlayerLogin) event);
            case ON_PLAYER_LOGOUT -> onPlayerLogout((OnPlayerLogout) event);
            case ON_PLAYER_PK_CHANGED -> onPlayerPKChanged((OnPlayerPKChanged) event);
            case ON_PLAYER_PROFESSION_CHANGE -> onPlayerProfessionChange((OnPlayerProfessionChange) event);
            case ON_PLAYER_PROFESSION_CANCEL -> onPlayerProfessionCancel((OnPlayerProfessionCancel) event);
            case ON_PLAYER_PVP_CHANGED -> onPlayerPvPChanged((OnPlayerPvPChanged) event);
            case ON_PLAYER_PVP_KILL -> onPlayerPvPKill((OnPlayerPvPKill) event);
            case ON_PLAYER_RESTORE -> onPlayerRestore((OnPlayerRestore) event);
            case ON_PLAYER_SELECT -> onPlayerSelect((OnPlayerSelect) event);
            case ON_PLAYER_SIT -> onPlayerSit((OnPlayerSit) event);
            case ON_PLAYER_SKILL_LEARN -> onPlayerSkillLearn((OnPlayerSkillLearn) event);
            case ON_PLAYER_SUMMON_SPAWN -> onPlayerSummonSpawn((OnPlayerSummonSpawn) event);
            case ON_PLAYER_SUMMON_TALK -> onPlayerSummonTalk((OnPlayerSummonTalk) event);
            case ON_PLAYER_TRANSFORM -> onPlayerTransform((OnPlayerTransform) event);
            case ON_PLAYER_ASSIST_KILL -> onPlayerAssistKill((OnPlayerAssistKill) event);
            case ON_PLAYER_PK_KILL -> onPlayerPkKill((OnPlayerPkKill) event);
            case ON_PLAYER_PARTY_REQUEST -> onPlayerPartyRequest((OnPlayerPartyRequest) event);
            case ON_PLAYER_TOWER_CAPTURE -> onPlayerTowerCapture((OnPlayerTowerCapture) event);
            case ON_PLAYER_PARTY_JOIN -> onPlayerPartyJoin((OnPlayerPartyJoin) event);
            case ON_PLAYER_PARTY_LEAVE -> onPlayerPartyLeave((OnPlayerPartyLeave) event);
            case ON_PLAYER_USE_TELEPORT_TO_LOCATION -> onPlayerUseTeleportToLocation((OnPlayerUseTeleportToLocation) event);
            case ON_PLAYER_TITLE_CHANGE -> onPlayerTitleChange((OnPlayerTitleChange) event);
            case ON_PLAYER_EVENT_PARTICIPATED -> onPlayerEventParticipated((OnPlayerEventParticipated) event);
            case ON_TRAP_ACTION -> onTrapAction((OnTrapAction) event);
            case ON_TVT_EVENT_FINISH -> onTvTEventFinish((OnTvTEventFinish) event);
            case ON_TVT_EVENT_KILL -> onTvTEventKill((OnTvTEventKill) event);
            case ON_TVT_EVENT_REGISTRATION_START -> onTvTEventRegistrationStart((OnTvTEventRegistrationStart) event);
            case ON_TVT_EVENT_START -> onTvTEventStart((OnTvTEventStart) event);
        }
    }
}
































