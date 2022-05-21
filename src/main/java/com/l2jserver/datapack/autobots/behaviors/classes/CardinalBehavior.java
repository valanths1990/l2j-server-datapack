package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.Healer;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class CardinalBehavior extends CombatBehavior implements Healer {
    public CardinalBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new SkillPreferences(true);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.BLESSED_SPIRITSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(surrenderToFire, (player, skill, target) -> skillPreferences.togglableSkills.containsKey(surrenderToFire) && ((target instanceof L2MonsterInstance) || (target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && !target.getEffectList().isAffectedBySkill(surrenderToFire)),
                new BotSkill(cancellation, (player, skill, target) -> skillPreferences.togglableSkills.containsKey(cancellation) && target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && target.getEffectList().getBuffCount() > 5),
                new BotSkill(fireVortex),
                new BotSkill(slow, (player, skill, target) -> target != null && !target.getEffectList().isAffectedBySkill(slow) && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(prominence)
        );
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(new BotSkill(arcanePower, (player, skill, taget) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(arcanePower) && !player.getEffectList().isAffectedBySkill(arcanePower) && player.isInCombat(), true));
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(slow);
    }
}