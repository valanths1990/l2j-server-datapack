package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;

import java.util.List;


public class TitanBehavior extends CombatBehavior {
    public TitanBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(362),
                new BotSkill(315, (player, skill, target) -> Rnd.nextDouble() < 0.8),
                new BotSkill(190, (player, skill, target) -> Rnd.nextDouble() < 0.2));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(139, (player, skill, target) -> player.getHpPercentage() < 30),
                new BotSkill(176, (player, skill, target) -> player.getHpPercentage() < 15)
        );
    }
}