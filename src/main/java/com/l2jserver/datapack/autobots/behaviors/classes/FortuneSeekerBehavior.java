package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.List;
import java.util.Optional;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class FortuneSeekerBehavior extends CombatBehavior {

    public FortuneSeekerBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new SkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(spoilCrush, (player, skill, target) -> target instanceof L2MonsterInstance && ((L2MonsterInstance) target).getSpoilerObjectId() != player.getObjectId() && ((L2MonsterInstance) target).getSpoilerObjectId() == 0),
                new BotSkill(spoil, (player, skill, target) -> target instanceof L2MonsterInstance && ((L2MonsterInstance) target).getSpoilerObjectId() != player.getObjectId() && ((L2MonsterInstance) target).getSpoilerObjectId() == 0),
                new BotSkill(hammerCrush, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(stunAttack, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(armorCrush));
    }

    @Override
    public void afterAttack() {

        if (player.getTarget() == null || !(player.getTarget() instanceof L2MonsterInstance)) return;
        Skill sweeperSkill = player.getSkills().get(42);
        if (sweeperSkill == null) return;
        L2MonsterInstance mob = (L2MonsterInstance) player.getTarget();
        if (mob.getHpPercentage() < 10 && mob.getSpoilerObjectId() == player.getObjectId()) Util.sleep(2000);
        if (mob.isDead() && mob.getSpoilerObjectId() == player.getObjectId()) {
            player.useMagicSkill(sweeperSkill, false);
            Util.sleep(1000);
        }
        Optional<L2Object> object = player.getClosestEntityInRadius(300, c -> c instanceof L2MonsterInstance && c.isDead() && ((L2MonsterInstance) c).getSpoilerObjectId() == player.getObjectId());
        if (object.isPresent()) {
            L2Object previousTarget = player.getTarget();
            player.setTarget(object.get());
            player.useMagicSkill(sweeperSkill, false);
            Util.sleep(1500);
            player.setTarget(previousTarget);
        }
    }
}