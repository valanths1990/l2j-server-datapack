package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.Kiter;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.SagittariusSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class SagittariusBehavior extends CombatBehavior implements RequiresMiscItem, Kiter {


    public SagittariusBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new SagittariusSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(stunningShot, (player, skill, target) -> target instanceof L2PcInstance && (((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0) && !target.isStunned()),
                new BotSkill(lethalShot, (player, skill, target) -> target instanceof L2PcInstance),
                new BotSkill(hamstringShot, (player, skill, target) -> target instanceof L2PcInstance && !target.getEffectList().isAffectedBySkill(hamstringShot)));
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(rapidShot, false, false, true, false),
                new BotSkill(dash, true, false, true, false),
                new BotSkill(ultimateEvasion, true, false, false, false),
                new BotSkill(hawkEye, false, true, true, false),
                new BotSkill(viciousStance, false, true, true, false),
                new BotSkill(accuracy, false, true, true, false));
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(dash, ultimateEvasion);
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(AutobotHelpers::getArrowId));
    }
}