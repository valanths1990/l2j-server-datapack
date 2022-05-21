package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class SpectralDancerBehavior extends CombatBehavior {


    public SpectralDancerBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(arrest, (player, skill, target) -> target instanceof L2PcInstance && !target.isRooted()),
                new BotSkill(judgement),
                new BotSkill(demonicBladeDance, (player, skill, target) -> target instanceof L2PcInstance && player.calculateDistance(target.getLocation(), false, false) < 100 && Rnd.get(1, 10) < 4)
        );
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(ultimateDefense, (player, skill, taget) -> player.getHpPercentage() < 20)
        );
    }
}