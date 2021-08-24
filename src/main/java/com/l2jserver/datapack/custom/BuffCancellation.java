package com.l2jserver.datapack.custom;

import com.l2jserver.datapack.handlers.communityboard.Ranking;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.BuffInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuffCancellation implements Runnable {

	private L2PcInstance player;
	private L2PcInstance target;
	private List<BuffInfo> buffs;
	private boolean isCancel; // true = cancel false = steal

	public BuffCancellation(L2PcInstance player, L2PcInstance target, List<BuffInfo> buffs, boolean isCancel) {

		this.player = player;
		this.target = target;
		this.buffs = buffs;
		this.isCancel = isCancel;
	}

	@Override public void run() {

		buffs.forEach(b -> b.getSkill().applyEffects(target, target, true, b.getTime()));
//		if (!isCancel) {
//			buffs.forEach(b -> player.getEffectList().stopSkillEffects(true, b.getSkill()));
//		}
	}
}
