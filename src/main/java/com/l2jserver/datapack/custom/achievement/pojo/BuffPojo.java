package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.skills.Skill;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class BuffPojo implements IRewardOperation {

    private final int buffId;
    private int level = 1;

    @ConstructorProperties({"buffId", "level"})
    public BuffPojo(int buffId, int level) {
        this.buffId = buffId;
        this.level = level == 0 ? 1 : level;
    }


    @Override
    public void executeOperation(L2PcInstance pc) {
        Skill skill = SkillData.getInstance().getSkill(buffId, level);
        if (skill != null) {
            skill.applyEffects(pc, pc);
        }
    }

    @Override
    public String getRewardIcon() {
        return Optional.ofNullable(SkillData.getInstance().getSkill(buffId, level)).map(Skill::getIcon).orElse("");
    }

    @Override
    public Long getCount() {
        return 1L;
    }
}
