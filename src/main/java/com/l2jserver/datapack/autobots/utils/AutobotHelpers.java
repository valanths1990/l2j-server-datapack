package com.l2jserver.datapack.autobots.utils;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.datapack.autobots.AutobotsManager;
import com.l2jserver.datapack.autobots.behaviors.CombatBehavior;
import com.l2jserver.datapack.autobots.behaviors.classes.*;
import com.l2jserver.datapack.autobots.behaviors.classes.pre.*;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.config.AutobotEquipment;
import com.l2jserver.datapack.autobots.config.AutobotEquipmentTest;
import com.l2jserver.datapack.autobots.config.AutobotJewels;
import com.l2jserver.gameserver.data.xml.impl.ArmorSetsData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.enums.Sex;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.model.L2ArmorSet;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.L2WorldRegion;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.appearance.PcAppearance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.itemcontainer.Inventory;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.items.type.CrystalType;
import com.l2jserver.gameserver.model.items.type.WeaponType;
import com.l2jserver.gameserver.model.stats.Stats;
import com.l2jserver.gameserver.model.stats.functions.FuncTemplate;
import com.l2jserver.gameserver.network.serverpackets.ExServerPrimitive;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.l2jserver.datapack.autobots.utils.AutobotItems.beastSoulShotId;

public class AutobotHelpers {

    public static int getArrowId(Autobot player) {
        int level = player.getLevel();
        if (level < 20) return 17; // wooden arrow
        if (level <= 39) return 1341; // bone arrow
        if (level <= 51) return 1342; // steel arrow
        if (level <= 60) return 1343; // Silver arrow
        if (level <= 75) return 1344; // Mithril Arrow
        return 1345; // shining
    }

    public static CrystalType getGradeType(Autobot player) {
        int level = player.getLevel();
        if (level < 20) return CrystalType.NONE;
        if (level <= 39) return CrystalType.D;
        if (level <= 51) return CrystalType.C;
        if (level <= 60) return CrystalType.B;
        if (level <= 75) return CrystalType.A;
        if (level <= 79) return CrystalType.S;
        if (level <= 84) return CrystalType.S80;
        if (level <= 85) return CrystalType.S84;
        return CrystalType.NONE;
    }

    public static List<SkillHolder> getDefaultFighterBuffs() {
        return List.of(new SkillHolder(1204, 2), new SkillHolder(1040, 3),
                new SkillHolder(1035, 4), new SkillHolder(1045, 6), new SkillHolder(1068, 3),
                new SkillHolder(1062, 2), new SkillHolder(1086, 2), new SkillHolder(1077, 3),
                new SkillHolder(1388, 3), new SkillHolder(1036, 2), new SkillHolder(274, 1),
                new SkillHolder(273, 1), new SkillHolder(268, 1), new SkillHolder(271, 1),
                new SkillHolder(267, 1), new SkillHolder(349, 1), new SkillHolder(264, 1),
                new SkillHolder(269, 1), new SkillHolder(364, 1), new SkillHolder(1363, 1),
                new SkillHolder(4699, 5), new SkillHolder(310, 1), new SkillHolder(1268, 4));
    }

    public static List<SkillHolder> getDefaultMageBuffs() {
        return List.of(new SkillHolder(1204, 2), new SkillHolder(1040, 3), new SkillHolder(1035, 4), new SkillHolder(4351, 6), new SkillHolder(1036, 2), new SkillHolder(1045, 6), new SkillHolder(1303, 2), new SkillHolder(1085, 3), new SkillHolder(1062, 2), new SkillHolder(1059, 3), new SkillHolder(1389, 3), new SkillHolder(273, 1), new SkillHolder(276, 1), new SkillHolder(365, 1), new SkillHolder(264, 1), new SkillHolder(268, 1), new SkillHolder(267, 1), new SkillHolder(349, 1), new SkillHolder(1413, 1), new SkillHolder(4703, 4));
    }

    public static void giveAndEquipItem(L2PcInstance player, int itemId, int enchant, boolean broadcast) {
        player.getInventory().addItem("AutobotItem", itemId, 1, player, null);
        L2ItemInstance item = player.getInventory().getItemByItemId(itemId);
        item.setEnchantLevel(enchant);
        player.getInventory().equipItemAndRecord(item);
        player.getInventory().reloadEquippedItems();
        if (broadcast) {
            player.broadcastUserInfo();
        }
    }

    public static void giveItemsByGrade(Autobot player, boolean addRealistically) {
        giveItemsByGrade(player, 0, 0, 0, addRealistically);
    }

    public static void giveItemsByGrade(Autobot player, int weaponEnchant, int armorEnchant, int jewelEnchant, boolean addRealistically) {
        CrystalType playersGrade = getGradeType(player);
        AutobotEquipmentTest equipmentTest = AutobotData.getInstance().getTestEquipmentMap().entrySet()
                .stream()
                .filter(e -> e.getKey() == player.getClassId() && e.getValue().containsKey(playersGrade))
                .map(e -> e.getValue().get(playersGrade))
                .findFirst().orElse(null);
        if (equipmentTest == null) {
            System.out.println("No equipment found for class " + player.getClassId() + " and grade " + playersGrade);
            return;
        }

        L2ArmorSet suitedArmor = ArmorSetsData.getInstance().getSet(equipmentTest.setId);

        if (!suitedArmor.getHead().isEmpty()) {
            suitedArmor.getHead().stream().mapToInt(Integer::intValue).max().ifPresent(i -> giveAndEquipItem(player, i, armorEnchant, addRealistically));
        }
        if (!suitedArmor.getGloves().isEmpty()) {
            suitedArmor.getGloves().stream().mapToInt(Integer::intValue).max().ifPresent(i -> giveAndEquipItem(player, i, armorEnchant, addRealistically));
        }
        if (!suitedArmor.getLegs().isEmpty()) {
            suitedArmor.getLegs().stream().mapToInt(Integer::intValue).max().ifPresent(i -> giveAndEquipItem(player, i, armorEnchant, addRealistically));
        }
        if (!suitedArmor.getFeet().isEmpty()) {
            suitedArmor.getFeet().stream().mapToInt(Integer::intValue).max().ifPresent(i -> giveAndEquipItem(player, i, armorEnchant, addRealistically));
        }
        if (equipmentTest.useShield) {
            if (!suitedArmor.getShield().isEmpty()) {
                suitedArmor.getShield().stream().mapToInt(Integer::intValue).max().ifPresent(i -> giveAndEquipItem(player, i, armorEnchant, addRealistically));
            }
        }
        giveAndEquipItem(player, suitedArmor.getChestId(), armorEnchant, addRealistically);


        if (equipmentTest.giveAccessories) {
            List<AutobotJewels> jewels = AutobotData.getInstance().getJewels().get(playersGrade);
            if (jewels != null && !jewels.isEmpty()) {
                AutobotJewels jewelSet = jewels.stream()
                        .filter(j -> (!j.isRaidboss || equipmentTest.raidBossAccessories) || (!j.isMage || player.isMageClass()))
                        .findFirst().orElse(null);
                if (jewelSet != null) {
                    giveAndEquipItem(player, jewelSet.neck, jewelEnchant, addRealistically);
                    giveAndEquipItem(player, jewelSet.leftEar, jewelEnchant, addRealistically);
                    giveAndEquipItem(player, jewelSet.rightEar, jewelEnchant, addRealistically);
                    giveAndEquipItem(player, jewelSet.leftRing, jewelEnchant, addRealistically);
                    giveAndEquipItem(player, jewelSet.rightRing, jewelEnchant, addRealistically);
                }
            }
        }
        Set<Integer> allWeapons = ItemTable.getInstance().getAllWeaponsId();
        allWeapons.stream().
                map(w -> ItemTable.getInstance().getTemplate(w))
                .filter(w -> w.getItemGrade() == playersGrade
                        && (w.getItemType() == equipmentTest.weaponType
                        && checkForTwoHanded(w, equipmentTest)
                        && checkIfWeaponIsMagic(w, equipmentTest)
                        && (equipmentTest.sa.isEmpty() || w.getName().contains(equipmentTest.sa))
                )).findFirst().ifPresent(w -> giveAndEquipItem(player, w.getId(), weaponEnchant, true));

    }


    private static boolean isAccessory(L2Item item) {
        return item.getBodyPart() == L2Item.SLOT_LR_FINGER || item.getBodyPart() == L2Item.SLOT_NECK || item.getBodyPart() == L2Item.SLOT_LR_EAR;
    }

    private static boolean isRaidbossAccessory(L2Item item) {
        return item.getName().matches(".*Baium.*|.*Queen.*|.*Antharas.*|.*Beleth.*|.*Core.*|.*Zaken.*|.*Orfen.*|.*Valakas.*|.*Frintezza.*|.*Freya.*");
    }


    private static boolean checkForTwoHanded(L2Item item, AutobotEquipmentTest equipmentTest) {
        if (item.getItemType() == WeaponType.BLUNT || item.getItemType() == WeaponType.SWORD) {
            if (equipmentTest.isTwoHanded) {
                return item.getBodyPart() == L2Item.SLOT_LR_HAND;
            }
        }
        return true;
    }

    private static boolean checkIfWeaponIsMagic(L2Item item, AutobotEquipmentTest equipmentTest) {
        if (equipmentTest.isMagicWeapon) {
            return item.isMagicWeapon();
        }
        return true;
    }

    public static void giveItemsByClassAndLevel(Autobot player, boolean addRealistically) {
//        giveItemsByClassAndLevel(player, 0, 0, 0, addRealistically);
    }

    public static void giveItemsByClassAndLevel(Autobot player, int weaponEnchant, int armorEnchant, int jewelEnchant, boolean addRealistically) {
        AutobotEquipment equipment = AutobotData.getInstance().getEquipment().stream().filter(b -> player.getLevel() >= b.minLevel && player.getLevel() <= b.maxLevel && player.getClassId() == b.classId).findFirst().orElse(null);

        if (equipment == null) {
            System.out.println("No equipment found for class " + player.getClassId() + " and level " + player.getLevel());
            return;
        }

        if (equipment.head != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD) != equipment.head)
            giveAndEquipItem(player, equipment.head, armorEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.chest != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) != equipment.chest)
            giveAndEquipItem(player, equipment.chest, armorEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.legs != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS) != equipment.legs && ItemTable.getInstance().getTemplate(equipment.chest).getBodyPart() != L2Item.SLOT_FULL_ARMOR)
            giveAndEquipItem(player, equipment.legs, armorEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.hands != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) != equipment.hands)
            giveAndEquipItem(player, equipment.hands, armorEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.feet != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET) != equipment.feet)
            giveAndEquipItem(player, equipment.feet, armorEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.rightHand != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) != equipment.rightHand)
            giveAndEquipItem(player, equipment.rightHand, weaponEnchant, false);

        if (addRealistically) {
            Util.sleep(1000);
            player.broadcastUserInfo();
        }

        if (equipment.leftHand != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND) != equipment.leftHand && ItemTable.getInstance().getTemplate(equipment.rightHand).getBodyPart() != L2Item.SLOT_LR_HAND)
            giveAndEquipItem(player, equipment.leftHand, weaponEnchant, false);

        if (equipment.neck != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK) != equipment.neck)
            giveAndEquipItem(player, equipment.neck, jewelEnchant, false);
        if (equipment.leftEar != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR) != equipment.leftEar)
            giveAndEquipItem(player, equipment.leftEar, jewelEnchant, false);
        if (equipment.rightEar != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR) != equipment.rightEar)
            giveAndEquipItem(player, equipment.rightEar, jewelEnchant, false);
        if (equipment.leftRing != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER) != equipment.leftRing)
            giveAndEquipItem(player, equipment.leftRing, jewelEnchant, false);
        if (equipment.rightRing != 0 && player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER) != equipment.rightRing)
            giveAndEquipItem(player, equipment.rightRing, jewelEnchant, false);

        player.broadcastUserInfo();
    }

    public static void clearCircle(L2PcInstance player, String circleName) {
        ExServerPrimitive packet = new ExServerPrimitive(circleName, new Location(0, 0, 0));
        packet.addPoint(Color.WHITE, 0, 0, 0);
        player.sendPacket(packet);
    }

    public static ExServerPrimitive createCirclePacket(String name, int x, int y, int z, int radius, Color color, int initX, int initY) {
        ExServerPrimitive packet = new ExServerPrimitive(name, initX, initY, -65535);
        IntStream.range(0, 359).forEach(i -> {
            int newX = (int) (x + radius * Math.cos(Math.toRadians(i)));
            int newY = (int) (y + radius * Math.sin(Math.toRadians(i)));
            int newXT = (int) (x + radius * Math.cos(Math.toRadians(i + 1)));
            int newYT = (int) (y + radius * Math.sin(Math.toRadians(i + 1)));
            Location loc = new Location(newX, newY, z);
            Location locPlus = new Location(newXT, newYT, z);
            packet.addLine(color, loc, locPlus);
        });
        return packet;
    }

    public static CombatBehavior getBehaviorByClassId(ClassId classId, Autobot player, CombatPreferences combatPreferences) {
        if (!allBehaviors.containsKey(classId)) {
            throw new IllegalStateException("No behavior for class " + classId.name() + " was found!");
        }
        return allBehaviors.get(classId).apply(player, combatPreferences);
    }


    public static List<ClassId> getSupportedClassesForLevel(int level) {
        return allBehaviors.keySet().stream().filter(c -> (level >= 1 && level <= 19 && c.level() == 0) || (level >= 20 && level <= 39 && c.level() == 1) || (level >= 40 && level <= 75 && c.level() == 2) || (level >= 76 && level <= 85 && c.level() == 3)).collect(Collectors.toList());
    }

    public static List<L2Character> getKnownTargetablesInRadius(L2Character player, int radius, AttackPlayerType attackPlayerType, Function<L2Character, Boolean> condition) {
        L2WorldRegion region;
        if (player.getWorldRegion() == null) return Collections.emptyList();
        region = player.getWorldRegion();
        return region.getSurroundingRegions().stream().flatMap(r -> r.getVisibleObjects().values()
                        .stream())
                .filter(o -> o instanceof L2Character)
                .map(o -> (L2Character) o)
                .filter(c -> condition.apply(c) && shouldTarget(c, attackPlayerType) && !c.isGM() && !c.isDead() && c != player && com.l2jserver.gameserver.util.Util.checkIfInRange(radius, player, c, true))
                .collect(Collectors.toList());
    }

    private static boolean shouldTarget(L2Object it, AttackPlayerType attackPlayerType) {
        return switch (attackPlayerType) {
            case None -> it instanceof L2MonsterInstance;
            case Flagged -> (it instanceof L2PcInstance && (((L2PcInstance) it).getPvpFlag() > 0 || ((L2PcInstance) it).getKarma() > 0)) || it instanceof L2MonsterInstance;
            case Innocent -> it instanceof L2PcInstance || it instanceof L2MonsterInstance;
        };
    }

    public static PcAppearance getRandomAppearance() {

        Sex randomSex = Rnd.get(1, 2) == 1 ? Sex.MALE : Sex.FEMALE;
        int hairStyle = Rnd.get(0, randomSex == Sex.MALE ? 4 : 6);
        int hairColor = Rnd.get(0, 3);
        int faceId = Rnd.get(0, 2);

        return new PcAppearance((byte) faceId, (byte) hairColor, (byte) hairStyle, randomSex == Sex.FEMALE);
    }

    public static boolean isControllingBot(L2PcInstance player) {
        return AutobotsManager.getInstance().getActiveBots().values().stream().anyMatch(b -> b.getController() != null && b.getController().getObjectId() == player.getObjectId());
    }

    public static Autobot getControllingBot(L2PcInstance player) {
        return AutobotsManager.getInstance().getActiveBots().values().stream().filter(b -> b.getController() != null && b.getController().getObjectId() == player.getObjectId()).findFirst().orElse(null);
    }

    public static int getShotId(Autobot bot) {
        int playerLevel = bot.getLevel();
        if (playerLevel < 20)
            return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1835 : 3947;
        if (playerLevel <= 39)
            return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1463 : 3948;
        if (playerLevel <= 51)
            return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1464 : 3949;
        if (playerLevel <= 60)
            return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1465 : 3950;
        if (playerLevel <= 75)
            return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1466 : 3951;
        return bot.getCombatBehavior().getShotType() == ShotType.SOULSHOTS ? 1467 : 3952;
    }

    public static final Map<ClassId, BiFunction<Autobot, CombatPreferences, CombatBehavior>> allBehaviors = new HashMap<>();

    static {
        allBehaviors.put(ClassId.fighter, FighterBehavior::new);
        allBehaviors.put(ClassId.warrior, WarriorBehavior::new);
        allBehaviors.put(ClassId.knight, KnightBehavior::new);
        allBehaviors.put(ClassId.rogue, RogueBehavior::new);
        allBehaviors.put(ClassId.mage, MysticBehavior::new);
        allBehaviors.put(ClassId.wizard, HumanWizardBehavior::new);
        allBehaviors.put(ClassId.elvenFighter, FighterBehavior::new);
        allBehaviors.put(ClassId.elvenMage, MysticBehavior::new);
        allBehaviors.put(ClassId.elvenKnight, KnightBehavior::new);
        allBehaviors.put(ClassId.elvenScout, RogueBehavior::new);
        allBehaviors.put(ClassId.elvenWizard, ElvenWizardBehavior::new);
        allBehaviors.put(ClassId.darkFighter, FighterBehavior::new);
        allBehaviors.put(ClassId.palusKnight, KnightBehavior::new);
        allBehaviors.put(ClassId.assassin, RogueBehavior::new);
        allBehaviors.put(ClassId.darkMage, MysticBehavior::new);
        allBehaviors.put(ClassId.darkWizard, DarkElvenWizardBehavior::new);
        allBehaviors.put(ClassId.orcFighter, OrcFighterBehavior::new);
        allBehaviors.put(ClassId.orcRaider, OrcRaiderBehavior::new);
        allBehaviors.put(ClassId.orcMonk, MonkBehavior::new);
        allBehaviors.put(ClassId.orcMage, OrcMysticBehavior::new);
        allBehaviors.put(ClassId.orcShaman, OrcMysticBehavior::new);
        allBehaviors.put(ClassId.dwarvenFighter, MaestroBehavior::new);
        allBehaviors.put(ClassId.artisan, FortuneSeekerBehavior::new);
        allBehaviors.put(ClassId.scavenger, FortuneSeekerBehavior::new);
        allBehaviors.put(ClassId.spellhowler, StormScreamerBehavior::new);
        allBehaviors.put(ClassId.stormScreamer, StormScreamerBehavior::new);
        allBehaviors.put(ClassId.spellsinger, MysticMuseBehavior::new);
        allBehaviors.put(ClassId.mysticMuse, MysticMuseBehavior::new);
        allBehaviors.put(ClassId.sorceror, ArchmageBehavior::new);
        allBehaviors.put(ClassId.archmage, ArchmageBehavior::new);
        allBehaviors.put(ClassId.necromancer, SoultakerBehavior::new);
        allBehaviors.put(ClassId.soultaker, SoultakerBehavior::new);
        allBehaviors.put(ClassId.hawkeye, SagittariusBehavior::new);
        allBehaviors.put(ClassId.sagittarius, SagittariusBehavior::new);
        allBehaviors.put(ClassId.silverRanger, MoonlightSentinelBehavior::new);
        allBehaviors.put(ClassId.moonlightSentinel, MoonlightSentinelBehavior::new);
        allBehaviors.put(ClassId.phantomRanger, GhostSentinelBehavior::new);
        allBehaviors.put(ClassId.ghostSentinel, GhostSentinelBehavior::new);
        allBehaviors.put(ClassId.treasureHunter, AdventurerBehavior::new);
        allBehaviors.put(ClassId.adventurer, AdventurerBehavior::new);
        allBehaviors.put(ClassId.plainsWalker, WindRiderBehavior::new);
        allBehaviors.put(ClassId.windRider, WindRiderBehavior::new);
        allBehaviors.put(ClassId.abyssWalker, GhostHunterBehavior::new);
        allBehaviors.put(ClassId.ghostHunter, GhostHunterBehavior::new);
        allBehaviors.put(ClassId.bishop, CardinalBehavior::new);
        allBehaviors.put(ClassId.cardinal, CardinalBehavior::new);
        allBehaviors.put(ClassId.overlord, DominatorBehavior::new);
        allBehaviors.put(ClassId.dominator, DominatorBehavior::new);
        allBehaviors.put(ClassId.destroyer, TitanBehavior::new);
        allBehaviors.put(ClassId.titan, TitanBehavior::new);
        allBehaviors.put(ClassId.gladiator, DuelistBehavior::new);
        allBehaviors.put(ClassId.duelist, DuelistBehavior::new);
        allBehaviors.put(ClassId.tyrant, GrandKhavatariBehavior::new);
        allBehaviors.put(ClassId.grandKhavatari, GrandKhavatariBehavior::new);
        allBehaviors.put(ClassId.warlord, DreadnoughtBehavior::new);
        allBehaviors.put(ClassId.dreadnought, DreadnoughtBehavior::new);
        allBehaviors.put(ClassId.paladin, PhoenixKnightBehavior::new);
        allBehaviors.put(ClassId.phoenixKnight, PhoenixKnightBehavior::new);
        allBehaviors.put(ClassId.darkAvenger, HellKnightBehavior::new);
        allBehaviors.put(ClassId.hellKnight, HellKnightBehavior::new);
        allBehaviors.put(ClassId.templeKnight, EvasTemplarBehavior::new);
        allBehaviors.put(ClassId.evaTemplar, EvasTemplarBehavior::new);
        allBehaviors.put(ClassId.shillienKnight, ShillienTemplarBehavior::new);
        allBehaviors.put(ClassId.shillienTemplar, ShillienTemplarBehavior::new);
        allBehaviors.put(ClassId.bladedancer, SpectralDancerBehavior::new);
        allBehaviors.put(ClassId.spectralDancer, SpectralDancerBehavior::new);
        allBehaviors.put(ClassId.swordSinger, SwordMuseBehavior::new);
        allBehaviors.put(ClassId.swordMuse, SwordMuseBehavior::new);
        allBehaviors.put(ClassId.bountyHunter, FortuneSeekerBehavior::new);
        allBehaviors.put(ClassId.fortuneSeeker, FortuneSeekerBehavior::new);
        allBehaviors.put(ClassId.warsmith, MaestroBehavior::new);
        allBehaviors.put(ClassId.maestro, MaestroBehavior::new);
    }

    public static final Map<ClassId, Supplier<CombatPreferences>> supportedCombatPrefs = new HashMap<>();

    static {
        //Warning. Reading this map, might give you a seizure. If you get a headache please speak to a pathologist
        supportedCombatPrefs.put(ClassId.fighter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.phoenixKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.warrior, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.phoenixKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.gladiator, () -> new DuelistCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.duelist, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.warlord, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dreadnought, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.knight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.phoenixKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.paladin, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.phoenixKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.darkAvenger, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.hellKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.rogue, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.adventurer, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.treasureHunter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.adventurer, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.hawkeye, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.sagittarius, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.mage, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.soultaker, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.wizard, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.soultaker, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.sorceror, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.archmage, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.necromancer, () -> new PetOwnerCombatPreferences(false, true, false, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.soultaker, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.warlock, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.arcanaLord, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.cleric, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.cardinal, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.bishop, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.cardinal, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.prophet, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.hierophant, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.elvenFighter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.elvenKnight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.templeKnight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.swordSinger, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.swordMuse, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.elvenScout, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.moonlightSentinel, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.plainsWalker, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.windRider, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.silverRanger, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.moonlightSentinel, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.elvenMage, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.mysticMuse, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.elvenWizard, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.mysticMuse, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.spellsinger, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.mysticMuse, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.elementalSummoner, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.elementalMaster, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.oracle, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.elder, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.darkFighter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.palusKnight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.shillienKnight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.bladedancer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.spectralDancer, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.assassin, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.ghostHunter, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.abyssWalker, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.ghostHunter, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.phantomRanger, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.ghostSentinel, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.darkMage, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.stormScreamer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.darkWizard, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.stormScreamer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.spellhowler, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.stormScreamer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.phantomSummoner, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.spectralMaster, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.shillienOracle, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.shillenElder, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.orcFighter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.titan, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.orcRaider, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.grandKhavatari, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.destroyer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.titan, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.tyrant, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.grandKhavatari, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.orcMonk, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.grandKhavatari, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.orcMage, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dominator, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.orcShaman, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dominator, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.overlord, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dominator, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.warcryer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.doomcryer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.dwarvenFighter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.fortuneSeeker, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, false, false, false, false));
        supportedCombatPrefs.put(ClassId.scavenger, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.fortuneSeeker, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.bountyHunter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.fortuneSeeker, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.artisan, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.maestro, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, false, AutobotData.getInstance().getSettings().useGreaterHealingPots, false));
        supportedCombatPrefs.put(ClassId.warsmith, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.maestro, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.duelist, () -> new DuelistCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.duelist, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.dreadnought, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dreadnought, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.phoenixKnight, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.phoenixKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.hellKnight, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.hellKnight, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.sagittarius, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.sagittarius, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.stormScreamer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.stormScreamer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.mysticMuse, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.mysticMuse, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.archmage, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.archmage, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.soultaker, () -> new PetOwnerCombatPreferences(false, true, false, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.soultaker, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.moonlightSentinel, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.moonlightSentinel, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.ghostSentinel, () -> new ArcherCombatPreferences(500, true, 700, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.ghostSentinel, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.adventurer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.adventurer, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.windRider, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.windRider, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.ghostHunter, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.ghostHunter, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.cardinal, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.cardinal, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.dominator, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.dominator, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.titan, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.titan, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.grandKhavatari, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.grandKhavatari, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.evaTemplar, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.shillienTemplar, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienTemplar, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.spectralDancer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.spectralDancer, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.swordMuse, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.swordMuse, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.fortuneSeeker, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.fortuneSeeker, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.maestro, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.maestro, getDefaultFighterBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.arcanaLord, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.arcanaLord, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.hierophant, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.hierophant, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.evaSaint, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.evaSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.elementalMaster, () -> new PetOwnerCombatPreferences(true, true, true, beastSoulShotId, true, AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.elementalMaster, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.shillienSaint, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.shillienSaint, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
        supportedCombatPrefs.put(ClassId.doomcryer, () -> new DefaultCombatPreferences(AutobotData.getInstance().getSettings().targetingRange, AutobotData.getInstance().getSettings().attackPlayerType, AutobotData.getInstance().getBuffs().getOrDefault(ClassId.doomcryer, getDefaultMageBuffs()), AutobotData.getInstance().getSettings().targetingPreference, AutobotData.getInstance().getSettings().useManaPots, AutobotData.getInstance().getSettings().useQuickHealingPots, AutobotData.getInstance().getSettings().useGreaterHealingPots, AutobotData.getInstance().getSettings().useGreaterCpPots));
    }
}
