package com.l2jserver.datapack.custom.achievement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.l2jserver.datapack.custom.achievement.pojo.*;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.enums.InstanceType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.model.base.PlayerState;
import com.l2jserver.gameserver.model.conditions.Condition;
import com.l2jserver.gameserver.model.conditions.ConditionCategoryType;
import com.l2jserver.gameserver.model.conditions.ConditionChangeWeapon;
import com.l2jserver.gameserver.model.conditions.ConditionCheckAbnormal;
import com.l2jserver.gameserver.model.conditions.ConditionGameChance;
import com.l2jserver.gameserver.model.conditions.ConditionGameTime;
import com.l2jserver.gameserver.model.conditions.ConditionLogicAnd;
import com.l2jserver.gameserver.model.conditions.ConditionLogicNot;
import com.l2jserver.gameserver.model.conditions.ConditionLogicOr;
import com.l2jserver.gameserver.model.conditions.ConditionMinDistance;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerActiveEffectId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerActiveSkillId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerAgathionEnergy;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerAgathionId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCallPc;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanCreateBase;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanCreateOutpost;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanEscape;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanRefuelAirship;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanResurrect;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanSummon;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanSummonSiegeGolem;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanSweep;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanTakeCastle;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanTakeFort;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanTransform;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCanUntransform;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCharges;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerClassIdRestriction;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCloakStatus;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerCp;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerFlyMounted;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerGrade;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasAgathion;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasCastle;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasClanHall;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasFort;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasPet;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHasServitor;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerHp;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerInsideZoneId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerInstanceId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerInvSize;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerIsClanLeader;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerIsHero;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerLandingZone;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerLevel;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerLevelRange;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerMp;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerPkCount;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerPledgeClass;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerRace;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerRangeFromNpc;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerSex;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerSiegeSide;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerSouls;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerState;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerSubclass;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerTransformationId;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerTvTEvent;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerVehicleMounted;
import com.l2jserver.gameserver.model.conditions.ConditionPlayerWeight;
import com.l2jserver.gameserver.model.conditions.ConditionSiegeZone;
import com.l2jserver.gameserver.model.conditions.ConditionSlotItemId;
import com.l2jserver.gameserver.model.conditions.ConditionTargetAbnormal;
import com.l2jserver.gameserver.model.conditions.ConditionTargetActiveEffectId;
import com.l2jserver.gameserver.model.conditions.ConditionTargetActiveSkillId;
import com.l2jserver.gameserver.model.conditions.ConditionTargetAggro;
import com.l2jserver.gameserver.model.conditions.ConditionTargetClassIdRestriction;
import com.l2jserver.gameserver.model.conditions.ConditionTargetInvSize;
import com.l2jserver.gameserver.model.conditions.ConditionTargetLevel;
import com.l2jserver.gameserver.model.conditions.ConditionTargetLevelRange;
import com.l2jserver.gameserver.model.conditions.ConditionTargetNpcId;
import com.l2jserver.gameserver.model.conditions.ConditionTargetNpcType;
import com.l2jserver.gameserver.model.conditions.ConditionTargetPlayable;
import com.l2jserver.gameserver.model.conditions.ConditionTargetRace;
import com.l2jserver.gameserver.model.conditions.ConditionTargetUsesWeaponKind;
import com.l2jserver.gameserver.model.conditions.ConditionTargetWeight;
import com.l2jserver.gameserver.model.conditions.ConditionUsingItemType;
import com.l2jserver.gameserver.model.conditions.ConditionUsingSkill;
import com.l2jserver.gameserver.model.conditions.ConditionUsingSlotType;
import com.l2jserver.gameserver.model.conditions.ConditionWithSkill;
import com.l2jserver.gameserver.model.conditions.ConditionGameTime.CheckGameTime;
import com.l2jserver.gameserver.model.items.type.ArmorType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.skills.AbnormalType;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ConditionParser {
    private static final Logger _log = Logger.getLogger(ConditionParser.class.getName());

    public static Condition parseCondition(ConditionPojo cond) {
        Condition condition = null;
        switch (cond.getConditionType().toLowerCase()) {
            case "player" -> condition = parsePlayerLogic((PlayerPojo) cond);
            case "target" -> condition = parseTargetLogic((TargetPojo) cond);
            case "not" -> condition = parseNotLogic((NotPojo) cond);
            case "and" -> condition = parseAndLogic((AndPojo) cond);
            case "using" -> condition = parseUsingLogic((UsingPojo) cond);
            case "game" -> condition = parseGameLogic((GamePojo) cond);
            case "or" -> condition = parseOrLogic((OrPojo) cond);
        }
        return condition;
    }

    public static Condition parseOrLogic(OrPojo or) {
        ConditionLogicOr cond = new ConditionLogicOr();
        or.getChildren().forEach(c -> cond.add(parseCondition(c)));
        return cond;
    }

    public static List<Condition> getCondition(AchievementPojo achievement) {
        if (achievement.getCondition() == null) {
            return null;
        }
        return achievement.getCondition().getConditions().stream().map(ConditionParser::parseCondition)
                .collect(Collectors.toList());
    }

    public static Condition parseAndLogic(AndPojo and) {
        ConditionLogicAnd cond = new ConditionLogicAnd();
        and.getChildren().forEach(c -> cond.add(parseCondition(c)));
        return cond;
    }

    public static Condition parseNotLogic(NotPojo not) {
        ConditionLogicAnd and = new ConditionLogicAnd();
        not.getChildren().forEach(c -> {
            Condition notCond = parseCondition(c);
            ConditionLogicNot cond = new ConditionLogicNot(notCond);
            and.add(cond);
        });
        return and;
    }

    public static Condition parseTargetLogic(TargetPojo target) {
        return switch (target.getAttribute().toLowerCase()) {
            case "aggro" -> {
                boolean val = Boolean.parseBoolean(target.getValue());
                yield new ConditionTargetAggro(val);
            }
            case "siegezone" -> {
                int value = Integer.decode(target.getValue());
                yield new ConditionSiegeZone(value, false);
            }
            case "level" -> {
                int lvl = Integer.decode(target.getValue());
                yield new ConditionTargetLevel(lvl);
            }
            case "levelrange" -> {
                String[] range = target.getValue().split(";");
                if (range.length == 2) {
                    int minimumLevel = Integer.decode(target.getValue().split(";")[0]);
                    int maximumLevel = Integer.decode(target.getValue().split(";")[1]);
                    yield new ConditionTargetLevelRange(minimumLevel, maximumLevel);
                }
                yield null;
            }
//            case "mypartyexceptme" -> {
//                 cond = joinAnd(cond, new
//                 ConditionTargetMyPartyExceptMe(Boolean.parseBoolean(target.getValue())));
//            }
            case "playable" -> new ConditionTargetPlayable();
            case "class_id_restriction" -> {
                StringTokenizer st = new StringTokenizer(target.getValue(), ",");
                List<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode(item));
                }
                yield new ConditionTargetClassIdRestriction(array);
            }
            case "active_effect_id" -> {
                int effectId = Integer.decode(target.getValue());
                yield new ConditionTargetActiveEffectId(effectId);
            }
            case "active_effect_id_lvl" -> {
                String val1 = target.getValue();
                int effect_id = Integer.decode((val1.split(",")[0]));
                int effect_lvl = Integer.decode(val1.split(",")[1]);
                yield new ConditionTargetActiveEffectId(effect_id, effect_lvl);
            }
            case "active_skill_id" -> {
                int skill_id = Integer.decode(target.getValue());
                yield new ConditionTargetActiveSkillId(skill_id);
            }
            case "active_skill_id_lvl" -> {
                String val = (target.getValue());
                int skill_id = Integer.decode(val.split(",")[0]);
                int skill_lvl = Integer.decode((val.split(",")[1]));
                yield new ConditionTargetActiveSkillId(skill_id, skill_lvl);
            }
            case "abnormal" -> {
                int abnormalId = Integer.decode((target.getValue()));
                yield new ConditionTargetAbnormal(abnormalId);
            }
            case "mindistance" -> {
                int distance = Integer.decode(target.getValue());
                yield new ConditionMinDistance(distance * distance);
            }
            case "race" -> new ConditionTargetRace(Race.valueOf(target.getValue()));
            case "using" -> {
                int mask = 0;
                StringTokenizer st = new StringTokenizer(target.getValue(), ",");
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    for (WeaponType wt : WeaponType.values()) {
                        if (wt.name().equals(item)) {
                            mask |= wt.mask();
                            break;
                        }
                    }
                    for (ArmorType at : ArmorType.values()) {
                        if (at.name().equals(item)) {
                            mask |= at.mask();
                            break;
                        }
                    }
                }
                yield new ConditionTargetUsesWeaponKind(mask);
            }
            case "npcid" -> {
                StringTokenizer st = new StringTokenizer(target.getValue(), ",");
                List<Integer> array1 = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array1.add(Integer.decode((item)));
                }
                yield new ConditionTargetNpcId(array1);
            }
            case "npctype" -> {
                String values = (target.getValue()).trim();
                String[] valuesSplit = values.split(",");
                InstanceType[] types = new InstanceType[valuesSplit.length];
                for (int j = 0; j < valuesSplit.length; j++) {
                    types[j] = Enum.valueOf(InstanceType.class, valuesSplit[j]);
                }
                yield new ConditionTargetNpcType(types);
            }
            case "weight" -> {
                int weight = Integer.decode((target.getValue()));
                yield new ConditionTargetWeight(weight);
            }
            case "invsize" -> {
                int size = Integer.decode(target.getValue());
                yield new ConditionTargetInvSize(size);
            }
            default -> throw new IllegalArgumentException(target.getAttribute() + " does not exists");
        };
    }

    public static Condition parsePlayerLogic(PlayerPojo player) {
        return switch (player.getAttribute().toLowerCase()) {
            case "races" -> {
                final String[] racesVal = player.getValue().split(",");
                final Race[] races = new Race[racesVal.length];
                for (int r = 0; r < racesVal.length; r++) {
                    if (racesVal[r] != null) {
                        races[r] = Race.valueOf(racesVal[r]);
                    }
                }
                yield new ConditionPlayerRace(races);
            }
            case "level" -> {
                int lvl = Integer.decode((player.getValue()));
                yield new ConditionPlayerLevel(lvl);
            }
            case "levelrange" -> {
                String[] range = (player.getValue()).split(";");
                if (range.length == 2) {
                    final int minimumLevel = Integer.decode((player.getValue()).split(";")[0]);
                    final int maximumLevel = Integer.decode((player.getValue()).split(";")[1]);
                    yield new ConditionPlayerLevelRange(minimumLevel, maximumLevel);
                }
                yield null;
            }
            case "resting" -> new ConditionPlayerState(PlayerState.RESTING, Boolean.parseBoolean(player.getValue()));
            case "flying" -> new ConditionPlayerState(PlayerState.FLYING, Boolean.parseBoolean(player.getValue()));
            case "moving" -> new ConditionPlayerState(PlayerState.MOVING, Boolean.parseBoolean(player.getValue()));
            case "running" -> new ConditionPlayerState(PlayerState.RUNNING, Boolean.parseBoolean(player.getValue()));
            case "standing" -> new ConditionPlayerState(PlayerState.STANDING, Boolean.parseBoolean(player.getValue()));
            case "behind" -> new ConditionPlayerState(PlayerState.BEHIND, Boolean.parseBoolean(player.getValue()));
            case "front" -> new ConditionPlayerState(PlayerState.FRONT, Boolean.parseBoolean(player.getValue()));
            case "chaotic" -> new ConditionPlayerState(PlayerState.CHAOTIC, Boolean.parseBoolean(player.getValue()));
            case "olympiad" -> new ConditionPlayerState(PlayerState.OLYMPIAD, Boolean.parseBoolean(player.getValue()));
            case "ishero" -> new ConditionPlayerIsHero(Boolean.parseBoolean(player.getValue()));
            case "transformationid" -> new ConditionPlayerTransformationId(Integer.parseInt(player.getValue()));
            case "hp" -> new ConditionPlayerHp(Integer.parseInt(player.getValue()));
            case "mp" -> new ConditionPlayerMp(Integer.decode((player.getValue())));
            case "cp" -> new ConditionPlayerCp(Integer.decode((player.getValue())));
            case "grade" -> new ConditionPlayerGrade(Integer.decode((player.getValue())));
            case "pkcount" -> new ConditionPlayerPkCount(Integer.decode((player.getValue())));
            case "siegezone" -> new ConditionSiegeZone(Integer.decode((player.getValue())), true);
            case "siegeside" -> new ConditionPlayerSiegeSide(Integer.decode((player.getValue())));
            case "charges" -> new ConditionPlayerCharges(Integer.decode((player.getValue())));
            case "souls" -> new ConditionPlayerSouls(Integer.decode((player.getValue())));
            case "weight" -> new ConditionPlayerWeight(Integer.decode((player.getValue())));
            case "invsize" -> new ConditionPlayerInvSize(Integer.decode((player.getValue())));
            case "isclanleader" -> new ConditionPlayerIsClanLeader(Boolean.parseBoolean(player.getValue()));
            case "ontvtevent" -> new ConditionPlayerTvTEvent(Boolean.parseBoolean(player.getValue()));
            case "pledgeclass" -> new ConditionPlayerPledgeClass(Integer.decode((player.getValue())));
            case "clanhall" -> {
                StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode((item)));
                }
                yield new ConditionPlayerHasClanHall(array);
            }
            case "fort" -> {
                int fort = Integer.decode((player.getValue()));
                yield new ConditionPlayerHasFort(fort);
            }
            case "castle" -> {
                int castle = Integer.decode((player.getValue()));
                yield new ConditionPlayerHasCastle(castle);
            }
            case "sex" -> {
                int sex = Integer.decode((player.getValue()));
                yield new ConditionPlayerSex(sex);
            }
            case "flymounted" -> {
                boolean val = Boolean.parseBoolean(player.getValue());
                yield new ConditionPlayerFlyMounted(val);
            }
            case "vehiclemounted" -> new ConditionPlayerVehicleMounted(Boolean.parseBoolean(player.getValue()));
            case "landingzone" -> new ConditionPlayerLandingZone(Boolean.parseBoolean(player.getValue()));
            case "active_effect_id" -> new ConditionPlayerActiveEffectId(Integer.decode((player.getValue())));
            case "active_effect_id_lvl" -> {
                String val1 = (player.getValue());
                int effect_id = Integer.decode((val1.split(",")[0]));
                int effect_lvl = Integer.decode((val1.split(",")[1]));
                yield new ConditionPlayerActiveEffectId(effect_id, effect_lvl);
            }
            case "active_skill_id" -> {
                int skill_id = Integer.decode((player.getValue()));
                yield new ConditionPlayerActiveSkillId(skill_id);
            }
            case "active_skill_id_lvl" -> {
                String val2 = (player.getValue());
                int skill_id2 = Integer.decode((val2.split(",")[0]));
                int skill_lvl = Integer.decode((val2.split(",")[1]));
                yield new ConditionPlayerActiveSkillId(skill_id2, skill_lvl);
            }
            case "class_id_restriction" -> {
                StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                List<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode((item)));
                }
                yield new ConditionPlayerClassIdRestriction(array);
            }
            case "subclass" -> new ConditionPlayerSubclass(Boolean.parseBoolean(player.getValue()));
            case "instanceid" -> {
                StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode(item));
                }
                yield new ConditionPlayerInstanceId(array);
            }
            case "agathionid" -> {
                int agathionId = Integer.decode(player.getValue());
                yield new ConditionPlayerAgathionId(agathionId);
            }
            case "cloakstatus" -> new ConditionPlayerCloakStatus(Boolean.parseBoolean(player.getValue()));
            case "haspet" -> {
                StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                ArrayList<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode(item));
                }
                yield new ConditionPlayerHasPet(array);
            }
            case "hasservitor" -> new ConditionPlayerHasServitor();
            case "npcidradius" -> {
                final StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                if (st.countTokens() == 3) {
                    final String[] ids = st.nextToken().split(";");
                    final int[] npcIds = new int[ids.length];
                    for (int index = 0; index < ids.length; index++) {
                        npcIds[index] = Integer.parseInt(ids[index]);
                    }
                    final int radius = Integer.parseInt(st.nextToken());
                    final boolean val = Boolean.parseBoolean(st.nextToken());
                    yield new ConditionPlayerRangeFromNpc(npcIds, radius, val);
                }
                yield null;
            }
            case "callpc" -> new ConditionPlayerCallPc(Boolean.parseBoolean(player.getValue()));
            case "cancreatebase" -> new ConditionPlayerCanCreateBase(Boolean.parseBoolean(player.getValue()));
            case "cancreateoutpost" -> new ConditionPlayerCanCreateOutpost(Boolean.parseBoolean(player.getValue()));
            case "canescape" -> new ConditionPlayerCanEscape(Boolean.parseBoolean(player.getValue()));
            case "canrefuelairship" -> new ConditionPlayerCanRefuelAirship(Integer.parseInt(player.getValue()));
            case "canresurrect" -> new ConditionPlayerCanResurrect(Boolean.parseBoolean(player.getValue()));
            case "cansummon" -> new ConditionPlayerCanSummon(Boolean.parseBoolean(player.getValue()));
            case "cansummonsiegegolem" -> new ConditionPlayerCanSummonSiegeGolem(Boolean.parseBoolean(player.getValue()));
            case "cansweep" -> new ConditionPlayerCanSweep(Boolean.parseBoolean(player.getValue()));
            case "cantakecastle" -> new ConditionPlayerCanTakeCastle();
            case "cantakefort" -> new ConditionPlayerCanTakeFort(Boolean.parseBoolean(player.getValue()));
            case "cantransform" -> new ConditionPlayerCanTransform(Boolean.parseBoolean(player.getValue()));
            case "canuntransform" -> new ConditionPlayerCanUntransform(Boolean.parseBoolean(player.getValue()));
            case "insidezoneid" -> {
                StringTokenizer st = new StringTokenizer(player.getValue(), ",");
                List<Integer> array = new ArrayList<>(st.countTokens());
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    array.add(Integer.decode(item));
                }
                yield new ConditionPlayerInsideZoneId(array);
            }
            case "checkabnormal" -> {
                final String value = player.getValue();
                if (value.contains(";")) {
                    final String[] values = value.split(";");
                    final var type = AbnormalType.valueOf(values[0]);
                    final var level = Integer.decode(values[1]);
                    final var mustHave = Boolean.parseBoolean(values[2]);
                    yield new ConditionCheckAbnormal(type, level, mustHave);
                } else {
                    yield new ConditionCheckAbnormal(AbnormalType.valueOf(value), -1, true);
                }
            }
            case "categorytype" -> {
                final String[] values = player.getValue().split(",");
                final Set<CategoryType> array1 = new HashSet<>(values.length);
                for (String value1 : values) {
                    array1.add(CategoryType.valueOf((value1)));
                }
                yield new ConditionCategoryType(array1);
            }
            case "hasagathion" -> new ConditionPlayerHasAgathion(Boolean.parseBoolean(player.getValue()));
            case "agathionenergy" -> new ConditionPlayerAgathionEnergy(Integer.decode((player.getValue())));
            default -> throw new IllegalArgumentException(player.getAttribute() + " does not exists");
        };
    }

    public static Condition parseUsingLogic(UsingPojo using) {
        return switch (using.getAttribute().toLowerCase()) {
            case "kind" -> {
                int mask = 0;
                StringTokenizer st = new StringTokenizer(using.getValue(), ",");
                while (st.hasMoreTokens()) {
                    int old = mask;
                    String item = st.nextToken().trim();
                    for (WeaponType wt : WeaponType.values()) {
                        if (wt.name().equals(item)) {
                            mask |= wt.mask();
                        }
                    }

                    for (ArmorType at : ArmorType.values()) {
                        if (at.name().equals(item)) {
                            mask |= at.mask();
                        }
                    }

                    if (old == mask) {
                        // _log.info("[parseUsingCondition=\"kind\"] Unknown item type name: " + item);
                    }
                }
                yield new ConditionUsingItemType(mask);
            }
            case "slot" -> {
                int mask = 0;
                StringTokenizer st = new StringTokenizer(using.getValue(), ",");
                while (st.hasMoreTokens()) {
                    int old = mask;
                    String item = st.nextToken().trim();
                    if (ItemTable.SLOTS.containsKey(item)) {
                        mask |= ItemTable.SLOTS.get(item);
                    }

                    if (old == mask) {
                        _log.info("[parseUsingCondition=\"slot\"] Unknown item slot name: " + item);
                    }
                }
                yield new ConditionUsingSlotType(mask);
            }
            case "weaponchange" -> {
                boolean val = Boolean.parseBoolean(using.getValue());
                yield new ConditionChangeWeapon(val);
            }
            case "slotitem" -> {
                StringTokenizer st = new StringTokenizer(using.getValue(), ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int slot = Integer.parseInt(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens()) {
                    enchant = Integer.parseInt(st.nextToken().trim());
                }
                yield new ConditionSlotItemId(slot, id, enchant);
            }
            case "skill" -> {
                int id = Integer.parseInt(using.getValue());
                yield new ConditionUsingSkill(id);
            }
            default -> throw new IllegalArgumentException(using.getAttribute() + " does not exists");
        };
    }

    public static Condition joinAnd(Condition cond, Condition c) {
        if (cond == null) {
            return c;
        }
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }

    public static Condition parseGameLogic(GamePojo game) {
        return switch (game.getAttribute().toLowerCase()) {
            case "skill" -> {
                boolean val = Boolean.parseBoolean(game.getValue());
                yield new ConditionWithSkill(val);
            }
            case "night" -> {
                boolean val = Boolean.parseBoolean(game.getValue());
                yield new ConditionGameTime(CheckGameTime.NIGHT, val);
            }
            case "chance" -> {
                int val = Integer.decode((game.getValue()));
                yield new ConditionGameChance(val);
            }
            default -> throw new IllegalArgumentException(game.getAttribute() + " does not exists");
        };
    }
}