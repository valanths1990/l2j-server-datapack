/*
 * Copyright © 2004-2021 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.handlers.effecthandlers.instant;

import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.StatsSet;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.effects.AbstractEffect;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTargetCancel;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.stats.Formulas;

import java.util.concurrent.TimeUnit;

/**
 * Target Cancel effect implementation.
 *
 * @author -Nemesiss-, Adry_85
 */
public final class TargetCancel extends AbstractEffect {
    private final int _chance;

    public TargetCancel(Condition attachCond, Condition applyCond, StatsSet set, StatsSet params) {
        super(attachCond, applyCond, set, params);

        _chance = params.getInt("chance", 100);
    }

    @Override
    public boolean calcSuccess(BuffInfo info) {
        boolean succeed = Formulas.calcProbability(_chance, info.getEffector(), info.getEffected(), info.getSkill());
        if (succeed) {
            if (info.getEffected() instanceof L2PcInstance && info.getEffector() instanceof L2PcInstance) {
                EventDispatcher.getInstance().notifyEventAsyncDelayed(new OnPlayerTargetCancel((L2PcInstance) info.getEffector(), (L2PcInstance) info.getEffected()), info.getEffector(),25, TimeUnit.MILLISECONDS);
            }
        }
        return succeed;
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void onStart(BuffInfo info) {
        info.getEffected().setTarget(null);
        info.getEffected().abortAttack();
        info.getEffected().abortCast();
        info.getEffected().getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE, info.getEffector());
    }
}
