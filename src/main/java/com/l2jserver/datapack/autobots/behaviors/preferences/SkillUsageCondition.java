package com.l2jserver.datapack.autobots.behaviors.preferences;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.skills.Skill;
import org.jetbrains.annotations.NotNull;

public class SkillUsageCondition {
    private final int skillId;
    private final StatusCondition statusCondition;
    private final ComparisonCondition comparisonCondition;
    private final ConditionValueType conditionValueType;
    private final TargetCondition targetCondition;
    private final int value;

    public SkillUsageCondition(@JsonProperty("skillId") int skillId, @JsonProperty("statusCondition") @NotNull StatusCondition statusCondition,
                               @JsonProperty("comparisonCondition") @NotNull ComparisonCondition comparisonCondition,
                               @JsonProperty("conditionValueType") @NotNull ConditionValueType conditionValueType,
                               @JsonProperty("targetCondition") @NotNull TargetCondition targetCondition,
                               @JsonProperty("value") int value) {
        this.skillId = skillId;
        this.statusCondition = statusCondition;
        this.comparisonCondition = comparisonCondition;
        this.conditionValueType = conditionValueType;
        this.targetCondition = targetCondition;
        this.value = value;
    }

    public boolean isValid(Autobot player) {
        if(player==null) return false;
        L2Object target = switch (targetCondition) {
            case My -> player;
            case Target -> player.getTarget();
            case PlayerTarget -> {
                if (player.getTarget()!=null &&player.getTarget().isPlayer()) {
                    yield player.getTarget();
                }
                yield null;
            }
            case MobTarget -> {
                if (player.getTarget().isMonster()) {
                    yield player.getTarget();
                }
                yield null;
            }
        };
        if (!(target instanceof L2Character)) {
            return false;
        }

        int valueToCompare = switch (conditionValueType) {
            case Percentage -> switch (statusCondition) {
                case Hp -> ((L2Character) target).getHpPercentage();
                case Cp -> {
                    L2Character c = (L2Character) target;
                    yield (int) ((c.getCurrentCp() / c.getMaxCp()) * 100.0);
                }
                case Mp -> ((L2Character) target).getMpPercentage();
                case Level, Distance -> 0;
            };
            case Amount -> switch (statusCondition) {
                case Hp -> (int) ((L2Character) target).getCurrentHp();
                case Cp -> (int) ((L2Character) target).getCurrentCp();
                case Mp -> (int) ((L2Character) target).getCurrentMp();
                case Level, Distance -> 0;
            };
            case MissingAmount -> switch (statusCondition) {
                case Hp -> (int) (((L2Character) target).getMaxHp() - ((L2Character) target).getCurrentHp());
                case Cp -> {
                    L2Character c = (L2Character) target;
                    yield (int) (c.getMaxCp() - c.getCurrentCp());
                }
                case Mp -> (int) (((L2Character) target).getMaxMp() - ((L2Character) target).getCurrentMp());
                case Level, Distance -> 0;
            };
        };

        return switch (comparisonCondition) {
            case Equals -> valueToCompare == value;
            case LessThan -> valueToCompare < value;
            case LessOrEqualThan -> valueToCompare <= value;
            case MoreThan -> valueToCompare > value;
            case MoreOrEqualThan -> valueToCompare >= value;
        };

    }

    public String getConditionText() {

        StringBuilder sb = new StringBuilder();

        switch (targetCondition) {
            case My -> sb.append("My ");
            case Target -> sb.append("My Target's ");
            case MobTarget -> sb.append("My monster target's ");
            case PlayerTarget -> sb.append("My player target's ");
        }
        switch (statusCondition) {
            case Hp -> sb.append("HP ");
            case Cp -> sb.append("CP ");
            case Mp -> sb.append("MP ");
            case Distance -> sb.append("distance ");
            case Level -> sb.append("level ");
        }

        switch (comparisonCondition) {

            case Equals -> sb.append("is equal to ");
            case LessThan -> sb.append("is less than ");
            case LessOrEqualThan -> sb.append("is less or equal than ");
            case MoreThan -> sb.append("is more than ");
            case MoreOrEqualThan -> sb.append("is more or equal than ");
        }

        switch (conditionValueType) {

            case Percentage -> sb.append(value + "%");
            case Amount -> sb.append(value);
            case MissingAmount -> sb.append(value + " of the missing max amount");
        }
        return sb.toString();
    }

    public int getSkillId() {
        return skillId;
    }

    public StatusCondition getStatusCondition() {
        return statusCondition;
    }

    public ComparisonCondition getComparisonCondition() {
        return comparisonCondition;
    }

    public ConditionValueType getConditionValueType() {
        return conditionValueType;
    }

    public TargetCondition getTargetCondition() {
        return targetCondition;
    }

    public int getValue() {
        return value;
    }
}
