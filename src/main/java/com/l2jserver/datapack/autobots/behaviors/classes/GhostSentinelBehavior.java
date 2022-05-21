package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.Kiter;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;


public class GhostSentinelBehavior extends CombatBehavior implements RequiresMiscItem, Kiter {

    public GhostSentinelBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(101, (player, skill, target) -> target instanceof L2PcInstance && ((L2PcInstance) target).getPvpFlag() > 0),
                new BotSkill(343, (player, skill, target) -> target instanceof L2PcInstance),
                new BotSkill(354, (player, skill, target) -> target instanceof L2PcInstance),
                new BotSkill(369, (player, skill, target) -> target instanceof L2PcInstance));
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(AutobotHelpers::getArrowId));
    }
}