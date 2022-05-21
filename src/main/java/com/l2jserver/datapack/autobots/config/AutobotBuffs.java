package com.l2jserver.datapack.autobots.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutobotBuffs {
    private final List<SkillHolder> buffsContent;
    private final ClassId classId;
    private final String buffsAsString;

    public AutobotBuffs(@JsonProperty("classid") @NotNull ClassId classId, @JsonProperty("buffs") @NotNull String buffsAsString) {
        this.classId = classId;
        this.buffsAsString = buffsAsString;
        buffsContent = Arrays.stream(buffsAsString.split(";")).map(s -> {
            String[] buff = s.split(",");
            return new SkillHolder(Integer.parseInt(buff[0]), Integer.parseInt(buff[1]));
        }).collect(Collectors.toList());
    }


    @NotNull
    public final AutobotBuffs copy(@JsonProperty("classid") @NotNull ClassId classId, @JsonProperty("buffs") @NotNull String buffsAsString) {
        return new AutobotBuffs(classId, buffsAsString);
    }

    public List<SkillHolder> getBuffsContent() {
        return buffsContent;
    }

    public ClassId getClassId() {
        return classId;
    }

    public String getBuffsAsString() {
        return buffsAsString;
    }
}
