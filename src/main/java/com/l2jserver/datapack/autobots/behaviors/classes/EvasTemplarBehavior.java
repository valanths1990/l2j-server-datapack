package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.RequiresMiscItem;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.skills.EvasTemplarSkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class EvasTemplarBehavior extends CombatBehavior implements RequiresMiscItem {

    public EvasTemplarBehavior(Autobot player, CombatPreferences combatPreferences) {
        super(player, combatPreferences);
        this.skillPreferences = new EvasTemplarSkillPreferences(false);
    }

    @Override
    public ShotType getShotType() {
        return ShotType.SOULSHOTS;

    }

    @Override
    protected List<BotSkill> getOffensiveSkills() {
        return List.of(
                new BotSkill(tribunal),
                new BotSkill(shieldBash, (player, skill, target) -> target instanceof L2PcInstance && target.getTarget() == player),
                new BotSkill(arrest, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && target != null && !target.isRooted() && player.getCombatBehavior().validateConditionalSkill(skill)),
                new BotSkill(songOfSilence, (player, skill, target) -> (!(target instanceof L2PcInstance) || ((((L2PcInstance) target).getPvpFlag() > 0 || ((L2PcInstance) target).getKarma() > 0))) && target != null && !target.isRooted() && player.getCombatBehavior().validateConditionalSkill(skill))
        );
    }

    @Override
    protected List<BotSkill> getSelfSupportSkills() {
        return List.of(
                new BotSkill(summonStormCubic, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(summonStormCubic) && !player.isInCombat() && player.getCubicById(summonStormCubicNpcId) == null),
                //BotSkill(summonLifeCubic) { player, _, _ -> !player.isInCombat && player.getCubic(summonLifeCubicNpcId) == null },
                new BotSkill(summonAttractiveCubic, (player, skill, target) -> player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(summonAttractiveCubic) && !player.isInCombat() && player.getCubicById(summonAttractiveCubicNpcId) == null),
                new BotSkill(touchOfLife, true, false, false, true),
                new BotSkill(magicalMirror, true, false, true, false),
                new BotSkill(ultimateDefense, true, false, false, false),
                new BotSkill(vengeance, true, false, false, false),
                new BotSkill(shieldFortress, false, true, true, false),
                new BotSkill(fortitude, false, true, true, false),
                new BotSkill(deflectArrow, false, true, true, false),
                new BotSkill(guardStance, false, true, true, false),
                new BotSkill(holyArmor, false, true, true, false)
        );
    }

    @Override
    public List<Integer> getConditionalSkills() {
        return List.of(arrest, ultimateDefense, touchOfLife, magicalMirror, vengeance);
    }

    @Override
    public List<MiscItem> getMiscItems() {
        return List.of(new MiscItem(p -> 1458, 100, 50));
    }
}