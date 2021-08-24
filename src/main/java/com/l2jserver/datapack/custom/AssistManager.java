package com.l2jserver.datapack.custom;

import com.l2jserver.datapack.custom.reward.RewardManager;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureSkillUse;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;

import static com.l2jserver.gameserver.config.Configuration.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AssistManager {
	private final Map<L2PcInstance, Map<L2Playable, Long>> supportedPlayers = new ConcurrentHashMap<>();

	private AssistManager() {
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_CREATURE_SKILL_USE, this::onSkillUse, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, this::onPlayerKill, this));
		Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_LOGOUT, event -> supportedPlayers.remove(((OnPlayerLogout) event).getActiveChar()), this));
	}

	public void onSkillUse(IBaseEvent event) {
		OnCreatureSkillUse onSkillUse = (OnCreatureSkillUse) event;
		if (onSkillUse.getCaster().getParty() == null || !(onSkillUse.getCaster() instanceof L2PcInstance) || !customs().getSupportSkills().contains(onSkillUse.getSkill().getId()) || (onSkillUse.getTargets() == null && onSkillUse.getCaster() == onSkillUse.getTarget())) {
			return;
		}
		supportedPlayers.putIfAbsent((L2PcInstance) onSkillUse.getCaster(), new ConcurrentHashMap<>());
		supportedPlayers.get(onSkillUse.getCaster()).put((L2Playable) onSkillUse.getTarget(), System.currentTimeMillis());
		Stream.ofNullable(onSkillUse.getTargets()).flatMap(Arrays::stream).forEach(o -> supportedPlayers.get(onSkillUse.getCaster()).put((L2Playable) o, System.currentTimeMillis()));

	}

	public void onPlayerKill(IBaseEvent event) {
		L2PcInstance killer = ((OnPlayerPvPKill) event).getActiveChar();
		L2Playable target = ((OnPlayerPvPKill) event).getTarget();

		if (killer.getParty() == null) {
			return;
		}
		List<L2PcInstance> supporters = killer.getParty().getMembers().stream().filter(p -> {
			if (p == killer) {
				return false;
			}
			return (supportedAttacker(p, killer) || attackedTarget(p, target)) && p.isInsideRadius(killer.getLocation(), 6000, false, false);
		}).collect(Collectors.toList());
		supporters.forEach(s -> RewardManager.getInstance().rewardPlayer(s, "assist"));
	}

	private boolean supportedAttacker(L2PcInstance player, L2Playable killer) {

		if (!supportedPlayers.containsKey(player)) {
			return false;
		}
		if (!supportedPlayers.get(player).containsKey(killer)) {
			return false;
		}
		return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - supportedPlayers.get(player).remove(killer)) <= 30;
	}

	private boolean attackedTarget(L2PcInstance player, L2Playable target) {
		return target.getAttackByList().contains(player);
	}

	public void onPlayerLogout(IBaseEvent event) {
		supportedPlayers.remove(((OnPlayerLogout) event).getActiveChar());
	}

	public static AssistManager getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final AssistManager instance = new AssistManager();
	}

	public static void main(String[] args) {
		AssistManager.getInstance();
	}

}
