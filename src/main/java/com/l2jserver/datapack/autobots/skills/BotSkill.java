package com.l2jserver.datapack.autobots.skills;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;


public class BotSkill {
    private final int skillId;
    private Function4<Autobot, Skill, L2Character, Boolean> condition = (one, two, three) -> true;

    public boolean forceTargetSelf;
    private final boolean isTogglableSkill;

    public BotSkill(int skillid) {
        this.skillId = skillid;
        this.forceTargetSelf = false;
        this.isTogglableSkill = false;
    }

    public BotSkill(int skillId, boolean isTogglableSkill, boolean forceTargetSelf, Function4<Autobot, Skill, L2Character, Boolean> condition) {
        this.skillId = skillId;
        this.isTogglableSkill = isTogglableSkill;
        this.forceTargetSelf = forceTargetSelf;
        this.condition = condition;
    }

    public BotSkill(int skillId, boolean isConditionalSkill, boolean isTogglableSkill, boolean useWhenEffectIsNotPresent, boolean forceTargetSelf) {

        this(skillId, isTogglableSkill, forceTargetSelf, (player, skill, character) ->
                (!isConditionalSkill || player.getCombatBehavior().validateConditionalSkill(skill)) && (!isTogglableSkill
                        || player.getCombatBehavior().getSkillPreferences().togglableSkills.containsKey(skillId)) && (!useWhenEffectIsNotPresent
                        || !player.getEffectList().isAffectedBySkill(skillId)));
    }

    public BotSkill(int skillId, Function4<Autobot, Skill, L2Character, Boolean> condition) {
        this(skillId, false, false, condition);
    }

    public BotSkill(int skillId, Function4<Autobot, Skill, L2Character, Boolean> condition, boolean isTogglableSkill) {
        this(skillId, isTogglableSkill, false, condition);
    }

    public BotSkill(int skillId, boolean forceTargetSelf, Function4<Autobot, Skill, L2Character, Boolean> condition) {
        this(skillId, false, forceTargetSelf, condition);
    }

    public int getSkillId() {
        return skillId;
    }

    public Function4<Autobot, Skill, L2Character, Boolean> getCondition() {
        return condition;
    }

    public boolean isForceTargetSelf() {
        return forceTargetSelf;
    }

    public boolean isTogglableSkill() {
        return isTogglableSkill;
    }
}
