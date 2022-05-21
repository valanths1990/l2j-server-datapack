package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class ShillienTemplarBehavior extends CombatBehavior implements RequiresMiscItem {

    public ShillienTemplarBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(shieldBash, (player, skill, target) -> target instanceof L2PcInstance && target.getTarget() == player),
                new BotSkill(arrest, (player, skill, target) -> target instanceof L2PcInstance && !target.isRooted() && player.calculateDistance(target.getLocation(), false, false) > 300),
                new BotSkill(touchOfDeath, (player, skill, target) -> target instanceof L2PcInstance && player.getHpPercentage() < 75),
                new BotSkill(judgement)
                //TODO hex
        );
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(summonVampiricCubic, (player, skill, target) -> !player.isInCombat() && player.getCubicById(summonVampiricCubicNpcId) == null),
                new BotSkill(summonPhantomCubic, (player, skill, target) -> !player.isInCombat() && player.getCubicById(summonPhantomCubicNpcId) == null),
                new BotSkill(summonViperCubic, (player, skill, target) -> !player.isInCombat() && player.getCubicById(summonViperCubicNpcId) == null),
                new BotSkill(magicalMirror, (player, skill, target) -> player.getHpPercentage() < 60),
                new BotSkill(ultimateDefense, (player, skill, target) -> player.getHpPercentage() < 20)
        );
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(p -> 1458, 100, 50));
    }
}