package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.SwordMuseSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class SwordMuseBehavior extends CombatBehavior {


    public SwordMuseBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new SwordMuseSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(arrest, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0
                        || ((L2PcInstance) target).getKarma() > 0)))
                        && target != null && !target.isRooted() && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(songOfSilence, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && player.getCombatBehavior().validateConditionalSkill(skill) && target != null && !target.getEffectList().isAffectedBySkill(songOfSilence)));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(ultimateDefense, true, false, false, false),
                new BotSkill(deflectArrow, false, true, true, false),
                new BotSkill(holyBlade, false, true, true, false)
        );
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(ultimateDefense, songOfSilence);
    }
}