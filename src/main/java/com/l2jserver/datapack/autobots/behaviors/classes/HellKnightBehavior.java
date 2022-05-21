package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.PetOwner;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.HellKnightSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class HellKnightBehavior extends CombatBehavior implements PetOwner {


    public HellKnightBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new HellKnightSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(shieldStun, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(shieldSlam, (player, skill, target) -> target instanceof L2PcInstance && !target.isMuted()),
                new BotSkill(touchOfDeath, (player, skill, target) -> target instanceof L2PcInstance && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(shackle, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && target != null && target.isRooted() && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(judgement));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(ultimateDefense, true, false, false, false),
                new BotSkill(physicalMirror, true, false, false, false),
                new BotSkill(vengeance, true, false, false, false),
                new BotSkill(shieldFortress, false, true, true, false),
                new BotSkill(fortitude, false, true, true, false),
                new BotSkill(deflectArrow, false, true, true, false)
        );
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(shackle, ultimateDefense, touchOfDeath, physicalMirror, vengeance, shieldOfRevenge);
    }

    @Override
    public SummonInfo getSummonInfo() {
        return new SummonInfo(new BotSkill(summonDarkPanther, (player, skill, target) -> !player.isInCombat()), new MiscItem(p -> 1459, 50, 10));
    }

    @Override
    public List<SkillHolder> getPetBuffs() {
        return AutobotHelpers.getDefaultFighterBuffs();
    }
}