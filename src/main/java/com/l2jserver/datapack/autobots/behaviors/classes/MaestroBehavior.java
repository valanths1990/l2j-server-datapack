package com.l2jserver.datapack.autobots.behaviors.classes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.attributes.MiscItem;
import com.l2jserver.datapack.autobots.behaviors.attributes.PetOwner;
import com.l2jserver.datapack.autobots.behaviors.preferences.CombatPreferences;
import com.l2jserver.datapack.autobots.behaviors.preferences.SkillPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;

import java.util.List;

import static com.l2jserver.datapack.autobots.utils.AutobotSkills.*;


public class MaestroBehavior extends CombatBehavior implements PetOwner {

    public MaestroBehavior(Autobot player, CombatPreferences combatPreferences) {
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
                new BotSkill(hammerCrush, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(stunAttack, (player, skill, target) -> target instanceof L2PcInstance && !target.isStunned()),
                new BotSkill(armorCrush)
        );
    }

    @Override
    public SummonInfo getSummonInfo() {
        return new SummonInfo(new BotSkill(25, (player, skill, target) -> !player.isInCombat()), new MiscItem(p -> 1459, 50, 10));
    }

    @Override
    public List<SkillHolder> getPetBuffs() {
        return AutobotHelpers.getDefaultFighterBuffs();
    }
}