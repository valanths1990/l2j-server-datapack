package com.l2jserver.datapack.autobots.behaviors.classes.pre;


import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.SecretManaRegen;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;

import java.util.List;

public class DarkElvenWizardBehavior extends CombatBehavior implements SecretManaRegen {

    public DarkElvenWizardBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(1266),
                new BotSkill(1172, (player, skill, target) -> target != null && player.calculateDistance(target.getLocation(), false, false) < 200),
                new BotSkill(1178),
                new BotSkill(1175));
    }

}