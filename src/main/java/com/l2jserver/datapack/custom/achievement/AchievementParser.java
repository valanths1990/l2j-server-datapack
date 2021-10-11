package com.l2jserver.datapack.custom.achievement;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.l2jserver.datapack.custom.achievement.exception.AchievementParsingException;
import com.l2jserver.datapack.custom.achievement.pojo.AchievementPojo;
import com.l2jserver.datapack.custom.achievement.pojo.AchievementsListPojo;
import com.l2jserver.datapack.custom.achievement.pojo.IRewardOperation;
import com.l2jserver.datapack.custom.achievement.stateImpl.Achievement;
import com.l2jserver.datapack.custom.achievement.stateImpl.IState;
import com.l2jserver.datapack.custom.achievement.stateImpl.StateFactory;
import com.l2jserver.gameserver.model.base.ClassId;

public class AchievementParser extends ConditionParser {

    public static List<Achievement> parse(AchievementsListPojo achievements) {
        List<Achievement> allAchievements = new ArrayList<>();
        for (AchievementPojo a : achievements.getAchievementsList()) {
            try {
                validateAchievement(a);
                Achievement achievement = Achievement.Builder.builder().setId(a.getId()).setStates(getStates(a))
                        .setTitle(a.getTitle()).setDesc(a.getDescription()).setRewardItems(getRewardItems(a))
                        .setClassId(getClassId(a)).setRepeating(a.isRepeating()).setTime(getTime(a))
                        .setUnlock(a.getUnlock()).setRequire(a.getRequire()).setCondition(getCondition(a))
                        .setMinlevel(a.getMinLevel()).build();
                allAchievements.add(achievement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allAchievements;
    }

    public static Achievement parse(AchievementPojo a) throws AchievementParsingException {
        Achievement achievement;
        validateAchievement(a);
        achievement = Achievement.Builder.builder().setId(a.getId()).setTitle(a.getTitle()).setStates(getStates(a))
                .setDesc(a.getDescription()).setRewardItems(getRewardItems(a)).setClassId(getClassId(a))
                .setRepeating(a.isRepeating()).setTime(getTime(a)).setUnlock(a.getUnlock()).setRequire(a.getRequire())
                .setCondition(getCondition(a)).setMinlevel(a.getMinLevel()).build();

        return achievement;

    }

    public static LocalDateTime getTime(AchievementPojo achievement) {

        if (achievement.getTime() == null) {
            return null;
        }
        List<String> splittedTime = List.of(achievement.getTime().split(":"));

        LocalDateTime time = LocalDateTime.now();
        for (String s : splittedTime) {

            if (Character.isUpperCase(s.charAt(s.length() - 1))) {
                time = time.plus(Period.parse("P" + s));
            } else {
                time = time.plus(Duration.parse("PT" + s));
            }
        }
        return time;
    }

    public static Set<ClassId> getClassId(AchievementPojo achievement) {

        if (achievement.getClassId() == null) {
            return null;
        }
        Set<ClassId> tempList = new HashSet<>();
        achievement.getClassId().forEach(classId -> {

            Optional<ClassId> found = Arrays.stream(ClassId.values()).filter(c -> c.name().equalsIgnoreCase(classId))
                    .findAny();

            if (found.isEmpty()) {
                return;
            }
            ClassId classToCompareWith = found.get();
            if (!(classToCompareWith.level() >= 4)) {
                searchAllClassesRecursively(classToCompareWith, tempList);
            }
        });
        return tempList;
    }

    private static void searchAllClassesRecursively(ClassId classId, Set<ClassId> result) {

        if (classId == null) {
            return;
        }

        result.add(classId);
        Set<ClassId> tmp = Arrays.stream(ClassId.values())
                .filter(c -> c.getParent() != null && c.getParent().name().equalsIgnoreCase(classId.name()))
                .collect(Collectors.toSet());
        if (tmp.size() > 0) {
            for (ClassId id : tmp) {
                searchAllClassesRecursively(id, result);
            }
        }
    }

    private static List<IRewardOperation> getRewardItems(AchievementPojo achievement) {
        return achievement.getReward().getRewardOperations();
    }

    private static List<IState<? extends Number>> getStates(AchievementPojo achievement) {
        return achievement.getState().stream()
                .map(StateFactory::getState)
                .collect(Collectors.toList());
    }

    public static void validateAchievement(AchievementPojo achievement) throws AchievementParsingException {
        if (achievement == null) {
            throw new NullPointerException();
        }
        if (achievement.getState() == null || achievement.getState().isEmpty()) {
            throw new AchievementParsingException("state", "States must be provided");
        }
        if (achievement.getDescription() == null) {
            throw new AchievementParsingException("description", "Description for Achievement must be provided");
        }
        if (achievement.getReward() == null || achievement.getReward().getRewardOperations() == null
                || achievement.getReward().getRewardOperations().isEmpty()) {
            throw new AchievementParsingException("reward", "Achievement need a reward");
        }
        if (achievement.getCondition() != null) {
            try {
                getCondition(achievement);
            } catch (Exception e) {
                throw new AchievementParsingException("conditions", "Something went wrong with the Conditions");
            }
        }
        if (achievement.getTime() != null) {
            try {
                getTime(achievement);
            } catch (DateTimeParseException e) {
                throw new AchievementParsingException("time", "Something went wrong while parsing the Time");
            }
        }
        if (achievement.getClassId() != null && Objects.requireNonNull(getClassId(achievement)).size() == 0) {
            throw new AchievementParsingException("classId", "Something went wrong while parsing the ClassId");
        }

    }

}
