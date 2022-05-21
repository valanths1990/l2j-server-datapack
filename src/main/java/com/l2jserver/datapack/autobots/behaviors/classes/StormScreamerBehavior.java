package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;

import java.util.List;


public class StormScreamerBehavior extends CombatBehavior {


    public StormScreamerBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(1341),
                new BotSkill(1343),
                new BotSkill(1234),
                new BotSkill(1239))
                ;
    }
}