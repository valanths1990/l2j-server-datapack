package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.DuelistSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class DuelistBehavior extends CombatBehavior {

    public DuelistBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new DuelistSkillPreferences(false);
    }


    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(sonicRage, (player, skill, target) -> shouldUseSkillOnTarget(target, player.getCombatBehavior().typedSkillPreferences()) && player.getCharges() < 7),
                new BotSkill(tripleSonicSlash, (player, skill, target) -> shouldUseSkillOnTarget(target, player.getCombatBehavior().typedSkillPreferences())),
                new BotSkill(doubleSonicSlash, (player, skill, target) -> shouldUseSkillOnTarget(target, player.getCombatBehavior().typedSkillPreferences())),
                new BotSkill(sonicBlaster, (player, skill, target) -> shouldUseSkillOnTarget(target, player.getCombatBehavior().typedSkillPreferences())),
                new BotSkill(tripleSlash, (player, skill, target) -> shouldUseSkillOnTarget(target, player.getCombatBehavior().typedSkillPreferences())));
    }

    private boolean shouldUseSkillOnTarget(L2Character target, DuelistSkillPreferences duelistSkillPreferences) {
        return (!(target instanceof L2MonsterInstance) || ((DuelistSkillPreferences) skillPreferences).isUseSkillsOnMobs());
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(braveheart, battleroar, sonicBarrier, sonicMove);
    }
}