package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.template.SkillTemplate;
import com.l2jserver.datapack.eventengine.model.entity.Character;
import com.l2jserver.datapack.eventengine.model.entity.Playable;

public class OnUseSkillEvent extends ListenerEvent {

    private final Playable mCaster;
    private final SkillTemplate mSkill;
    private final Character mTarget;

    public OnUseSkillEvent(Playable caster, SkillTemplate skill, Character target) {
        mCaster = caster;
        mSkill = skill;
        mTarget = target;
    }

    public Playable getCaster() {
        return mCaster;
    }

    public SkillTemplate getSkill() {
        return mSkill;
    }

    public Character getTarget() {
        return mTarget;
    }

    public ListenerType getType() {
        return ListenerType.ON_USE_SKILL;
    }
}
