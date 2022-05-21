package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.PhoenixKnightSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class PhoenixKnightBehavior extends CombatBehavior implements RequiresMiscItem {

    public PhoenixKnightBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new PhoenixKnightSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(shieldSlam, (player, skill, target) -> target instanceof L2PcInstance && !target.isMuted()),
                new BotSkill(shieldStun, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(shackle, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && target != null && !target.isRooted() && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(tribunal)
        );
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(angelicIcon, true, false, false, false),
                new BotSkill(touchOfLife, true, false, false, false),
                new BotSkill(ultimateDefense, true, false, false, false),
                new BotSkill(holyBlessing, true, false, false, false),
                new BotSkill(deflectArrow, false, true, true, false),
                new BotSkill(aegisStance, false, true, true, false),
                new BotSkill(shieldFortress, false, true, true, false),
                new BotSkill(fortitude, false, true, true, false),
                new BotSkill(holyBlade, false, true, true, false),
                new BotSkill(holyArmor, false, true, true, false),
                new BotSkill(physicalMirror, true, false, false, false),
                new BotSkill(vengeance, true, false, false, false)
        );

    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(shackle, ultimateDefense, holyBlessing, physicalMirror, vengeance, touchOfLife, angelicIcon);
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(p -> 1459, 50, 10));
    }
}