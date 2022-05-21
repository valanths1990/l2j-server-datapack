package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.PetOwner;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.SoultakerSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.Collections;
import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class SoultakerBehavior extends CombatBehavior implements RequiresMiscItem, PetOwner {

    public SoultakerBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new SoultakerSkillPreferences(true);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.BLESSED_SPIRITSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(curseGloom, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(curseGloom) && (/*(target is Monster) || */(target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0)) && !target.getEffectList().isAffectedBySkill(curseGloom))),
                new BotSkill(anchor, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(anchor) && target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && !target.getEffectList().isAffectedBySkill(anchor)),
                new BotSkill(curseOfAbyss, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(curseOfAbyss) && target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && !target.getEffectList().isAffectedBySkill(curseOfAbyss)),
                new BotSkill(curseOfDoom, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(curseOfDoom) && target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && !target.getEffectList().isAffectedBySkill(curseOfDoom)),
                new BotSkill(silence, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(silence) && target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && !target.getEffectList().isAffectedBySkill(silence)),
                new BotSkill(slow, (player, skill, target) -> target != null && !target.getEffectList().isAffectedBySkill(slow) && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(darkVortex),
                new BotSkill(vampiricClaw, (player, skill, target) -> player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(deathSpike));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(arcanePower, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(arcanePower) && !player.getEffectList().isAffectedBySkill(arcanePower) && player.isInCombat(), true),
                new BotSkill(transferPain, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(transferPain) && !player.getEffectList().isAffectedBySkill(transferPain) && player.hasServitor(), true)
        );
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(slow, vampiricClaw);
    }

    @Override
    public SummonInfo getSummonInfo() {
        return new SummonInfo(new BotSkill(1129, (player, skill, target) -> target != null && target.isDead()), new MiscItem(p -> 1459, 50, 10));
    }

    @Override
    public List<SkillHolder> getPetBuffs() {
        return Collections.emptyList();
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(p -> 2508));
    }
}