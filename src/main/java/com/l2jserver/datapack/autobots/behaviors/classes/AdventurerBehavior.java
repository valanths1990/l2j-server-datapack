package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.AdventurerSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;

public class AdventurerBehavior extends CombatBehavior {

    public AdventurerBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new AdventurerSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(bluff, (player, skill, target) -> target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0)),
                new BotSkill(trick, (player, skill, target) -> target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(trick)),
                new BotSkill(_switch, (player, skill, target) -> target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(_switch)),
                new BotSkill(backstab, (player, skill, target) -> player.isBehindTarget()),
                new BotSkill(lethalBlow),
                new BotSkill(deadlyBlow),
                new BotSkill(criticalBlow, false, false, true, false));
    }

}