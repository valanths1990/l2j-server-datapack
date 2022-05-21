package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.DreadnoughtSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class DreadnoughtBehavior extends CombatBehavior {
    public DreadnoughtBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new DreadnoughtSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(361),
                new BotSkill(347, (player, skill, target) -> target != null && !target.isDead() && player.calculateDistance(target.getLocation(), false, false) < 100),
                new BotSkill(48, (player, skill, target) -> target != null && !target.isDead() && player.calculateDistance(target.getLocation(), false, false) < 100),
                new BotSkill(452, (player, skill, target) -> target != null && !target.isDead() && player.calculateDistance(target.getLocation(), false, false) < 100),
                new BotSkill(36, (player, skill, target) -> target != null && !target.isDead() && player.calculateDistance(target.getLocation(), false, false) < 100));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(braveheart, true, false, false, false),
                new BotSkill(revival, true, false, false, false),
                new BotSkill(battleroar, true, false, false, false),
                new BotSkill(fellSwoop, false, true, true, false),
                new BotSkill(viciousStance, false, true, true, false),
                new BotSkill(warFrenzy, false, true, true, false),
                new BotSkill(thrillFight, false, true, true, false)

        );
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(braveheart, revival, battleroar);
    }
}