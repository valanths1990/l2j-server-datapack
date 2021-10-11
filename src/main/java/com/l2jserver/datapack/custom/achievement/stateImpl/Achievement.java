package com.l2jserver.datapack.custom.achievement.stateImpl;

import static java.util.Objects.requireNonNull;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.l2jserver.datapack.custom.achievement.pojo.IRewardOperation;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;

public class Achievement implements Comparable<Achievement> {

    private final Integer id;
    private final String title;
    private final String desc;
    private final List<IState<? extends Number>> states;
    private final LocalDateTime time;
    private final Set<ClassId> classId;
    private final List<Condition> condition;
    private final boolean repeating;
    private final List<IRewardOperation> rewards;
    private final Integer[] unlock;
    private final Integer[] require;
    private L2PcInstance owner;
    private final int minLevel;
    private final Builder builder;

    private Achievement(Builder builder) {
        this.id = requireNonNull(builder.id);
        this.title = requireNonNull(builder.title);
        this.desc = requireNonNull(builder.desc);
        this.states = requireNonNull(builder.states);
        this.rewards = requireNonNull(builder.rewards);
        this.time = builder.time == null ? LocalDateTime.MAX : builder.time;
        this.classId = builder.classId == null ? Collections.emptySet() : builder.classId;
        this.condition = builder.condition == null ? Collections.emptyList() : builder.condition;
        this.repeating = builder.repeating;
        this.unlock = builder.unlock;
        this.require = builder.require;
        this.minLevel = builder.minLevel;
        this.builder = builder;
    }

    @SuppressWarnings("unchecked")
    public static Achievement getCopy(Achievement a) {

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(a.getStates());
            oos.flush();
            oos.close();
            bos.close();
            byte[] byteData = bos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
            List<IState<? extends Number>> states = (List<IState<? extends Number>>) new ObjectInputStream(bais).readObject();
            a.builder.setStates(states);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Achievement(a.builder);
    }

    public void reset() {
        this.states.forEach(IState::reset);
    }

    public int getProgress() {

        return (int) this.getStates().stream()
                .mapToDouble(state -> (state.getCurrent().doubleValue() / state.getEnd().doubleValue()) * 100)
                .reduce(0.0, Double::sum) / this.states.size();
    }

    public boolean transit(IBaseEvent event) {

        if (owner == null) {
            return false;
        }
        boolean conditionResult = testConditions();
        if (!conditionResult) {
            return false;
        }
        return states.stream().allMatch(s -> s.transit(event));
    }

    public boolean assignAchievementToPlayer(L2PcInstance player) {
        Predicate<L2PcInstance> checkMatchingClassId = (p) -> {
            if (classId == null || classId.isEmpty()) {
                return true;
            }
            return classId.contains(p.getClassId());
        };
        return checkMatchingClassId.test(player) && player.getLevel() >= minLevel;
    }

    private boolean testConditions() {
        if (this.condition == null) {
            return true;
        }
        if (owner.getTarget() == null) {
            return this.condition.stream().allMatch(c -> c.testImpl(owner, null, owner.getLastSkillCast(), null));
        }
        return this.condition.stream()
                .allMatch(c -> c.testImpl(owner, owner.getTarget().getActingPlayer(), owner.getLastSkillCast(), null));
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public List<IState<? extends Number>> getStates() {
        return Collections.unmodifiableList(states);
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Set<ClassId> getClassId() {
        return Collections.unmodifiableSet(this.classId);
    }

    public List<Condition> getCondition() {
        return Collections.unmodifiableList(condition);
    }

    public boolean isRepeating() {
        return repeating;
    }

    public List<IRewardOperation> getRewardOperations() {
        return Collections.unmodifiableList(rewards);
    }

    public Integer[] getUnlock() {
        return unlock;
    }

    public Integer[] getRequire() {
        return require;
    }

    public int getMinLevel() {

        return minLevel;
    }

    public final static class Builder implements Serializable {
        @Serial
        private static final long serialVersionUID = 5503368619074573231L;
        private Integer id;
        private String title;
        private String desc;
        private List<IState<? extends Number>> states;
        private LocalDateTime time;
        private Set<ClassId> classId;
        private List<Condition> condition;
        private boolean repeating;
        private List<IRewardOperation> rewards;
        private Integer[] unlock;
        private Integer[] require;
        private int minLevel;

        public Achievement build() {
            return new Achievement(this);
        }

        private Builder() {

        }

        public Builder with() {
            return this;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder setId(Integer id) {

            this.id = id;
            return this;
        }

        public Builder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder setStates(List<IState<? extends Number>> states) {
            this.states = states;
            return this;
        }

        public Builder setTime(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public Builder setClassId(Set<ClassId> ClassId) {
            this.classId = ClassId;
            return this;
        }

        public Builder setCondition(List<Condition> condition) {
            this.condition = condition;
            return this;
        }

        public Builder setRepeating(boolean repeating) {
            this.repeating = repeating;
            return this;
        }

        public Builder setRewardItems(List<IRewardOperation> rewardItems) {
            this.rewards = rewardItems;
            return this;
        }

        public Builder setUnlock(Integer[] unlock) {
            this.unlock = unlock;
            return this;
        }

        public Builder setRequire(Integer[] require) {
            this.require = require;
            return this;
        }

        public Builder setMinlevel(int minlevel) {
            this.minLevel = minlevel;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

    }

    public L2PcInstance getOwner() {
        return owner;
    }

    public void setOwner(L2PcInstance owner) {
        if (owner == null || this.owner != null) {
            return;
        }
        this.owner = owner;
    }

    public Builder getBuilder() {
        return builder;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {

        return "Achievement [id =" + id + ", title=" + title + ", desc=" + desc + ", state=" + states + ", time=" + time
                + ", classId=" + classId + ", condition=" + condition + ", repeating=" + repeating + ", rewardItems="
                + rewards + ",unlock=" + Arrays.toString(unlock) + ", require=" + Arrays.toString(require)
                + ", owner=" + owner + ", mindLevel=" + minLevel + ", builder=" + builder + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Achievement other = (Achievement) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }

    @Override
    public int compareTo(Achievement o) {

        int otherAchievementProgress = o.getProgress();
        int currentAchievementsProgress = this.getProgress();

        if (otherAchievementProgress < currentAchievementsProgress) {
            return 1;
        } else if (otherAchievementProgress > currentAchievementsProgress) {
            return -1;
        }

        return 0;
    }

}
