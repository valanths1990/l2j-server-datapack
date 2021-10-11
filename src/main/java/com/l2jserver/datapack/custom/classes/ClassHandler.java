package com.l2jserver.datapack.custom.classes;

import java.util.*;
import java.util.Map.Entry;

import static com.l2jserver.gameserver.config.Configuration.character;

import com.l2jserver.datapack.ai.npc.ClassMaster.ClassMaster;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.dao.factory.impl.DAOFactory;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.data.xml.impl.SkillTreesData;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.CategoryType;
import com.l2jserver.gameserver.enums.Race;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.instancemanager.QuestManager;
import com.l2jserver.gameserver.model.L2SkillLearn;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2NpcInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.base.AcquireSkillType;
import com.l2jserver.gameserver.model.base.ClassId;
import com.l2jserver.gameserver.model.base.ClassLevel;
import com.l2jserver.gameserver.model.base.PlayerClass;
import com.l2jserver.gameserver.model.base.SubClass;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLevelChanged;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerProfessionCancel;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.quest.QuestState;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.SystemMessageId;
import com.l2jserver.gameserver.network.serverpackets.*;
import com.l2jserver.gameserver.util.Util;

public class ClassHandler implements IBypassHandler {
    private final String[] COMMANDS = {"class;homepage", "class;certifications", "class;removesublass", "class;addsubclass"};
    private final String addClassTableTitle = "<tr><td align=center><font name=\"hs12\" color=\"%raceColor%\">%race%</font> </td></tr>";

    private static final String addClassButton = "<tr><td align=center><button value = \"%className%\"action=\"bypass class;homepage %action% %id%\"width=120 height=25 back=\"L2UI_ct1.button_df_down\"fore=\"L2UI_ct1.button_df\"></td></tr>";
    private static final String transferAvailableButton = "  <button value=\"Transfer\" action=\"bypass class;homepage showtransferclass\" width=140 height=40 back=\"L2UI_ct1.button_df_down\"fore=\"L2UI_ct1.button_df\">";
    private static final String transferButton = " <button value=\"%className%\" action=\"bypass class;homepage transfer %id%\" width=160 height=45 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
    private static final String changeClassButton = "<button value=\"%className%\" action=\"bypass class;homepage changeclass %id%\" width=130 height=30 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
    private static final String learnCertificationSkillsRow = "<tr><td align=center width=32 height=32> <button value=\"\" action=\"bypass class;certifications learnskill %type% %id%\" width=32 height=32 back=\"%icon%\" fore=\"%icon%\"></td> <td align=center width=120><font name=\"hs9\" color=\"fca503\">%skillName%</font></td> <td align=center width=180 height=32> %text% </td></tr>";
    private static final String learnPomanderSkillButton = "<button value=\"Learn Skills\" action=\"bypass class;homepage learnpomanderskill\" width=110 height=30 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
    private static final String resetPomanderSkillButton = "<button value=\"Reset Skill\" action=\"bypass class;homepage resetpomanderskill\" width=110 height=30 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\">";
    private final List<Skill> certificationSkillsForAllClasses = new ArrayList<>();
    private final List<Skill> certificationSkillsForKamaels = new ArrayList<>();
    private final List<Skill> transformSkillsForAllClasses = new ArrayList<>();
    private final List<Skill> transformSkillsForKamael = new ArrayList<>();
    private final List<Skill> emergentSkills = new ArrayList<>();
    private final L2NpcInstance aventGarde = new L2NpcInstance(NpcData.getInstance().getTemplate(32323));

    private final int[] kamaelTransSkillIds = {
            656, 658, 662
    };
    private static final int[] allCertSkillIds = {
            637, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 648, 650, 651, 652, 653, 654, 655, 799, 800, 801, 802, 803, 804, 1489, 1490, 1491
    };

    private final int[] emergentSkillIds = {631, 632, 633, 634}; // order is very important
    private static final int MIN_LEVEL = 76;
    private static final int MIN_CLASS_LEVEL = 3;
    private static final ItemHolder[] PORMANDERS = {
            // Cardinal (97)
            new ItemHolder(15307, 1),
            // Eva's Saint (105)
            new ItemHolder(15308, 1),
            // Shillen Saint (112)
            new ItemHolder(15309, 4)
    };

    private enum RACECOLOR {
        HumanFighter("HUMAN Fighter", "731111", null), HumanMage("HUMAN Mage", "b51b1b", null), ElfFighter("ELF Fighter", "159599", null), ElfMage("ELF Mage", "83d7ee", null), DarkElfFighter("DARK_ELF Fighter", "22359c", null), DarkElfMage("DARK_ELF Mage", "344ee0", null), OrcFighter("ORC Fighter", "104a27", null), OrcMage("ORC Mage", "109144", null), Dwarf("DWARF Fighter", "d1b545", null), Kamael("KAMAEL Fighter", "630707", null);

        private final String color;
        private final String race;
        private final String icon;

        RACECOLOR(String race, String color, String icon) {
            this.color = color;
            this.race = race;
            this.icon = icon;
        }

        public static String getIcon(String classId) {
            for (RACECOLOR r : values()) {
                if (r.race.equals(classId)) {
                    return r.icon;
                }
            }
            return null;
        }

        public static String getColor(String race) {
            for (RACECOLOR r : values()) {
                if (r.race.equals(race)) {
                    return r.color;
                }
            }
            return null;
        }
    }

    public ClassHandler() {
        ClassMaster classMaster = (ClassMaster) QuestManager.getInstance().getScripts().get(ClassMaster.class.getSimpleName());
        for (int i : allCertSkillIds) {
            Skill tmp = SkillData.getInstance().getSkill(i, 1);
            if (tmp != null) {
                certificationSkillsForAllClasses.add(tmp);
            }
            int[] kamaelcertSkillIds = {
                    637, 638, 639, 640, 642, 644, 645, 647, 650, 651, 653, 800, 801, 655, 799
            };
            if (Arrays.stream(kamaelcertSkillIds).anyMatch(id -> id == i)) {
                certificationSkillsForKamaels.add(tmp);
            }
        }
        int[] allTransformSkillIds = {
                656, 657, 658, 659, 660, 661, 662
        };
        for (int i : allTransformSkillIds) {
            Skill tmpSkill = SkillData.getInstance().getSkill(i, 1);
            if (tmpSkill != null) {
                transformSkillsForAllClasses.add(tmpSkill);
            }
            if (Arrays.stream(kamaelTransSkillIds).anyMatch(id -> id == i)) {
                transformSkillsForKamael.add(tmpSkill);
            }
        }
        for (int i : emergentSkillIds) {
            Skill s = SkillData.getInstance().getSkill(i, 1);
            emergentSkills.add(s);
        }
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LEVEL_CHANGED, this::showTranserOnLevelChange, this));
    }

    private void showTranserOnLevelChange(IBaseEvent event) {
        OnPlayerLevelChanged e = (OnPlayerLevelChanged) event;
        if (canTransferClass(e.getActiveChar())) {
            String html = showTransferClasses(e.getActiveChar());
            CommunityBoardHandler.separateAndSend(html, e.getActiveChar());
        }
    }

    @Override
    public boolean useBypass(String command, L2PcInstance player, L2Character bypassOrigin) {
        player.sendPacket(new TimerPacket());
        String resultHtml = "";
        int count = Arrays.stream(emergentSkillIds).filter(i -> player.getKnownSkill(i) != null).map(s -> player.getKnownSkill(s).getLevel()).sum();
        String[] splittedCommand = command.split(" ");
        String currentPage = splittedCommand[0].split(";")[1];
        if (splittedCommand.length == 1) {
            resultHtml = homepage(player);
        } else {
            switch (splittedCommand[1]) {
                case "showclasses":
                    resultHtml = showClasses(player);
                    break;
                case "showremoveclasses":
                    resultHtml = showRemoveClasses(player);
                    break;
                case "addclass":
                    resultHtml = addClass(player, Integer.parseInt(splittedCommand[2]));
                    break;
                case "removeclass":
                    removeClass(player, Integer.parseInt(splittedCommand[2]));
                    resultHtml = showRemoveClasses(player);
                    break;
                case "changeclass":
                    resultHtml = changeClass(player, Integer.parseInt(splittedCommand[2]));
                    break;
                case "showtransferclass":
                    resultHtml = showTransferClasses(player);
                    break;
                case "transfer":
                    resultHtml = transferClass(player, splittedCommand[2]);
                    break;
                case "certifications":
                    if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Fourth) {
                        resultHtml = showCertifications(player);
                    }
                    break;
                case "attack":
                    resultHtml = changeAttack(player, splittedCommand[2], count);
                    player.sendSkillList();
                    break;
                case "defence":
                    resultHtml = changeDefence(player, splittedCommand[2], count);
                    player.sendSkillList();
                    break;
                case "empower":
                    resultHtml = changeEmpower(player, splittedCommand[2], count);
                    player.sendSkillList();
                    break;
                case "magicdefence":
                    resultHtml = changeMagicDefence(player, splittedCommand[2], count);
                    player.sendSkillList();
                    break;
                case "showcertifications":
                    resultHtml = showAvailableCertifications(player);
                    break;
                case "showtransformations":
                    resultHtml = showAvailableTransformations(player);
                    break;
                case "resetcertifications":
                    resultHtml = resetCertifications(player);
                    player.sendSkillList();
                    break;
                case "resettransformations":
                    resultHtml = resetTransformations(player);
                    player.sendSkillList();
                    break;
                case "learnskill":
                    resultHtml = learnAbilities(player, splittedCommand[2], splittedCommand[3]);
                    break;
                case "learnpomanderskill":
                    resultHtml = learnPomanderSkill(player);
                    break;
                case "resetpomanderskill":
                    resultHtml = resetPomanderSkill(player);
                    break;
            }
        }
        if (resultHtml == null) {
            resultHtml = homepage(player);
        }
//		resultHtml = resultHtml.replaceAll("%[a-zA-Z]+%", "");
        CommunityBoardHandler.separateAndSend(resultHtml, player);
        return true;
    }

    private boolean hasTransferSkillItems(L2PcInstance player) {
        int itemId;
        switch (player.getClassId()) {
            case cardinal -> {
                itemId = 15307;
            }
            case evaSaint -> {
                itemId = 15308;
            }
            case shillienSaint -> {
                itemId = 15309;
            }
            default -> {
                itemId = -1;
            }
        }
        return (player.getInventory().getInventoryItemCount(itemId, -1) > 0);
    }

    private String resetPomanderSkill(L2PcInstance player) {
        if (getTransferClassIndex(player) == -1 || character().getFeeDeleteTransferSkills() > player.getAdena()) {
            return homepage(player);
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() != ClassLevel.Fourth || hasTransferSkillItems(player)) {
            return homepage(player);
        }

        boolean hasSkills = false;
        final Collection<L2SkillLearn> skills = SkillTreesData.getInstance().getTransferSkillTree(player.getClassId()).values();
        for (L2SkillLearn skillLearn : skills) {
            final Skill skill = player.getKnownSkill(skillLearn.getSkillId());
            if (skill != null) {
                player.removeSkill(skill);
                //                for (ItemHolder item : skillLearn.getRequiredItems()) {
                //                    player.addItem("Cleanse", item.getId(), item.getCount(), aventGarde, true);
                //                }
                hasSkills = true;
            }
        }
        final int index = getTransferClassIndex(player);
        player.getInventory().addItem("ClassMaster", PORMANDERS[index].getId(), PORMANDERS[index].getCount(), player, player);
        // Adena gets reduced once.
        if (hasSkills) {
            player.reduceAdena("Cleanse", character().getFeeDeleteTransferSkills(), aventGarde, true);
        }

        player.sendSkillList();
        return homepage(player);
    }

    private static int getTransferClassIndex(L2PcInstance player) {
        switch (player.getClassId()) {
            case cardinal -> {
                return 0;
            }
            case evaSaint -> {
                return 1;
            }
            case shillienSaint -> {
                return 2;
            }
            default -> {
                return -1;
            }
        }
    }

    private String learnPomanderSkill(L2PcInstance player) {
        player.setLastFolkNPC(aventGarde);
        if (!player.isInCategory(CategoryType.HEAL_MASTER)) {
            return homepage(player);
        }
        if ((player.getLevel() < MIN_LEVEL) || (player.getClassId().level() < MIN_CLASS_LEVEL)) {
            return homepage(player);
        }
        final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.TRANSFER);
        int count = 0;
        for (L2SkillLearn skillLearn : SkillTreesData.getInstance().getAvailableTransferSkills(player)) {
            if (SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel()) != null) {
                count++;
                asl.addSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel(), skillLearn.getSkillLevel(), skillLearn.getLevelUpSp(), 0);
            }
        }
        if (count > 0) {
            player.sendPacket(asl);
        } else {
            player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
        }
        return homepage(player);
    }

    private String transferClass(L2PcInstance player, String id) {

        if (canTransferClass(player)) {
            int classId = Integer.parseInt(id);
            checkAndChangeClass(player, classId);
        }

        return showTransferClasses(player);
    }

    private void saveQuestState(L2PcInstance activeChar, Skill skill, String questVar,int classIndex) {
        QuestState st = activeChar.getQuestState("SubClassSkills");
        st.saveGlobalQuestVar(questVar+classIndex,skill.getId()+";");
    }


    private boolean checkAndChangeClass(L2PcInstance player, int val) {
        final ClassId currentClassId = player.getClassId();
        if ((getMinLevel(currentClassId.level()) > player.getLevel()) && !character().allowEntireTree()) {
            return false;
        }

        if (!validateClassId(currentClassId, val)) {
            return false;
        }

        final int newJobLevel = currentClassId.level() + 1;

        // Weight/Inventory check
        if (!character().getClassMaster().getRewardItems(newJobLevel).isEmpty() && !player.isInventoryUnder90(false)) {
            player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
            return false;
        }

        // check if player have all required items for class transfer
        for (ItemHolder holder : character().getClassMaster().getRequireItems(newJobLevel)) {
            if (player.getInventory().getInventoryItemCount(holder.getId(), -1) < holder.getCount()) {
                player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
                return false;
            }
        }

        // get all required items for class transfer
        for (ItemHolder holder : character().getClassMaster().getRequireItems(newJobLevel)) {
            if (!player.destroyItemByItemId("ClassMaster", holder.getId(), holder.getCount(), player, true)) {
                return false;
            }
        }

        // reward player with items
        for (ItemHolder holder : character().getClassMaster().getRewardItems(newJobLevel)) {
            player.addItem("ClassMaster", holder.getId(), holder.getCount(), player, true);
        }

        player.setClassId(val);

        if (player.isSubClassActive()) {
            player.getSubClasses().get(player.getClassIndex()).setClassId(player.getActiveClass());
        } else {
            player.setBaseClass(player.getActiveClass());
        }
        player.broadcastUserInfo();

        return true;
    }

    private String changeAttack(L2PcInstance player, String action, int count) {
        if (count >= 6 && action.equals("increase")) {
            return showCertifications(player);
        }
        Skill oldSkill = player.getKnownSkill(emergentSkillIds[0]);
        if (action.equals("increase")) {
            if (oldSkill == null) {
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[0], 1));
            } else if (oldSkill.getLevel() < 6) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[0], oldSkill.getLevel() + 1));
            }
        }
        if (action.equals("decrease")) {

            if (oldSkill != null && oldSkill.getLevel() > 1) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(oldSkill.getId(), oldSkill.getLevel() - 1));
            } else if (oldSkill != null && oldSkill.getLevel() == 1) {
                player.removeSkill(oldSkill);
            }

        }

        return showCertifications(player);
    }

    private String changeDefence(L2PcInstance player, String action, int count) {
        if (count >= 6 && action.equals("increase")) {
            return showCertifications(player);
        }
        Skill oldSkill = player.getKnownSkill(emergentSkillIds[1]);
        if (action.equals("increase")) {

            if (oldSkill == null) {
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[1], 1));
            } else if (oldSkill.getLevel() < 6) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[1], oldSkill.getLevel() + 1));
            }
        }
        if (action.equals("decrease")) {

            if (oldSkill != null && oldSkill.getLevel() > 1) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(oldSkill.getId(), oldSkill.getLevel() - 1));
            } else if (oldSkill != null && oldSkill.getLevel() == 1) {
                player.removeSkill(oldSkill);
            }

        }

        return showCertifications(player);
    }

    private String changeEmpower(L2PcInstance player, String action, int count) {
        if (count >= 6 && action.equals("increase")) {
            return showCertifications(player);
        }
        Skill oldSkill = player.getKnownSkill(emergentSkillIds[2]);
        if (action.equals("increase")) {

            if (oldSkill == null) {
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[2], 1));
            } else if (oldSkill.getLevel() < 6) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[2], oldSkill.getLevel() + 1));
            }
        }
        if (action.equals("decrease")) {
            if (oldSkill != null && oldSkill.getLevel() > 1) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(oldSkill.getId(), oldSkill.getLevel() - 1));
            } else if (oldSkill != null && oldSkill.getLevel() == 1) {
                player.removeSkill(oldSkill);
            }

        }

        return showCertifications(player);
    }

    private String changeMagicDefence(L2PcInstance player, String action, int count) {
        if (count >= 6 && action.equals("increase")) {
            return showCertifications(player);
        }
        Skill oldSkill = player.getKnownSkill(emergentSkillIds[3]);
        if (action.equals("increase")) {

            if (oldSkill == null) {
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[3], 1));
            } else if (oldSkill.getLevel() < 6) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(emergentSkillIds[3], oldSkill.getLevel() + 1));
            }
        }
        if (action.equals("decrease")) {

            if (oldSkill != null && oldSkill.getLevel() > 1) {
                player.removeSkill(oldSkill);
                giveSkill(player, aventGarde, SkillData.getInstance().getSkill(oldSkill.getId(), oldSkill.getLevel() - 1));
            } else if (oldSkill != null && oldSkill.getLevel() == 1) {
                player.removeSkill(oldSkill);
            }

        }

        return showCertifications(player);
    }

    private String learnAbilities(L2PcInstance player, String action, String id) {
        int skillId = Integer.parseInt(id);
        if (action.equals("master")) {
            if (certificationSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).count() >= 3) {
                return showCertifications(player);
            }
            if (player.getRace() == Race.KAMAEL) {
                Optional<Skill> optSkill = certificationSkillsForKamaels.stream().filter(s -> s.getId() == skillId).findFirst();
                if (optSkill.isPresent()) {
                    Skill skill = optSkill.get();
                    Skill oldSkill = player.getKnownSkill(skillId);
                    if (oldSkill == null) {
                        giveSkill(player, null, skill);
                    }
                }
            } else {
                Optional<Skill> optSkill = certificationSkillsForAllClasses.stream().filter(s -> s.getId() == skillId).findFirst();
                if (optSkill.isPresent()) {
                    Skill skill = optSkill.get();
                    Skill oldSkill = player.getKnownSkill(skillId);
                    if (oldSkill == null) {
                        giveSkill(player, null, skill);
                    }

                }
            }
            showAvailableCertifications(player);
        } else {
            System.out.println(transformSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).mapToInt(s -> player.getKnownSkill(s.getId()).getLevel()).sum());
            if (transformSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).mapToInt(s -> player.getKnownSkill(s.getId()).getLevel()).sum() >= 3) {
                player.sendPacket(new ShowBoard("0", "0"));
                return showCertifications(player);
            }
            if (player.getRace() == Race.KAMAEL) {
                Skill oldSkill = player.getKnownSkill(skillId);

                if (oldSkill != null && oldSkill.getLevel() < 3) {
                    Skill newSkill = SkillData.getInstance().getSkill(skillId, oldSkill.getLevel() + 1);
                    player.removeSkill(oldSkill);
                    giveSkill(player, aventGarde, newSkill);
                } else if (oldSkill == null) {
                    Optional<Skill> optSkill = transformSkillsForKamael.stream().filter(s -> s.getId() == skillId).findFirst();
                    if (optSkill.isPresent()) {
                        Skill skill = optSkill.get();
                        giveSkill(player, aventGarde, skill);
                    }
                }

            } else {
                Skill oldSkill = player.getKnownSkill(skillId);
                if (oldSkill != null && oldSkill.getLevel() < 3) {
                    Skill newSkill = SkillData.getInstance().getSkill(skillId, oldSkill.getLevel() + 1);
                    player.removeSkill(oldSkill);
                    giveSkill(player, aventGarde, newSkill);
                } else if (oldSkill == null) {
                    Optional<Skill> optSkill = transformSkillsForAllClasses.stream().filter(s -> s.getId() == skillId).findFirst();
                    if (optSkill.isPresent()) {
                        Skill skill = optSkill.get();
                        giveSkill(player, aventGarde, skill);
                    }
                }

            }
            showAvailableTransformations(player);
        }

        return showCertifications(player);
    }

    private void giveSkill(L2PcInstance player, L2Npc trainer, Skill skill) {
        // Send message.
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.LEARNED_SKILL_S1);
        sm.addSkillName(skill);
        player.sendPacket(sm);
        player.sendPacket(new AcquireSkillDone());
        player.addSkill(skill, true);
        player.sendSkillList();
        player.updateShortCuts(skill.getId(), skill.getLevel());
    }

    private String resetCertifications(L2PcInstance player) {
        certificationSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).forEach(player::removeSkill);
        return showCertifications(player);
    }

    private String resetTransformations(L2PcInstance player) {
        transformSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).forEach(player::removeSkill);
        return showCertifications(player);
    }

    private String showAvailableCertifications(L2PcInstance player) {
        if (certificationSkillsForAllClasses.stream().filter(s -> player.getKnownSkill(s.getId()) != null).count() >= 3) {
            return showCertifications(player);
        }
        NpcHtmlMessage html = new NpcHtmlMessage();
        StringBuilder str = new StringBuilder();
        String tmp = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/learnmaster.html");
        str.append("<table border=\"0\">");
        if (player.getRace() == Race.KAMAEL) {
            certificationSkillsForKamaels.stream().filter(s -> !player.getAllSkills().contains(s)).forEach(s -> {
                String temp = learnCertificationSkillsRow.replace("%id%", String.valueOf(s.getId())).replace("%skillName%", s.getName().substring(s.getName().indexOf("-") + 2)).replace("%icon%", s.getIcon()).replace("%type%", "master").replace("%text%", "some text");
                str.append(temp);
            });
        } else {
            certificationSkillsForAllClasses.stream().filter(s -> !player.getAllSkills().contains(s)).forEach(s -> {
                String temp = learnCertificationSkillsRow.replace("%id%", String.valueOf(s.getId())).replace("%skillName%", s.getName().substring(s.getName().indexOf("-") + 2)).replace("%icon%", s.getIcon()).replace("%type%", "master").replace("%text%", "some text");
                str.append(temp);
            });
        }
        tmp = tmp.replace("%title%", "Master/Class");

        str.append("</table>");

        tmp = tmp.replace("%list%", str.toString());
        html.setHtml(tmp);
        player.sendPacket(html);
        return showCertifications(player);
    }

    private String showAvailableTransformations(L2PcInstance player) {
        NpcHtmlMessage html = new NpcHtmlMessage();
        StringBuilder str = new StringBuilder();
        String tmp = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/learnmaster.html");
        str.append("<table border=\"0\">");
        if (player.getRace() == Race.KAMAEL) {
            transformSkillsForKamael.forEach(s -> {
                String temp = learnCertificationSkillsRow.replace("%id%", String.valueOf(s.getId())).replace("%skillName%", s.getName().substring(s.getName().indexOf("-") + 1)).replace("%icon%", s.getIcon()).replace("%type%", "transformation").replace("%text%", "some text");

                str.append(temp);
            });
        } else {
            transformSkillsForAllClasses.forEach(s -> {
                String temp = learnCertificationSkillsRow.replace("%id%", String.valueOf(s.getId())).replace("%skillName%", s.getName()).replace("%icon%", s.getIcon()).replace("%type%", "transformation").replace("%text%", "some text");
                str.append(temp);
            });
        }
        tmp = tmp.replace("%title%", "Transformation");

        str.append("</table>");

        tmp = tmp.replace("%list%", str.toString());
        html.setHtml(tmp);
        player.sendPacket(html);
        return showCertifications(player);
    }

    private String showCertifications(L2PcInstance player) {
        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/certifications.html");

        for (Skill s : certificationSkillsForAllClasses) {
            if (player.getKnownSkill(s.getId()) != null) {
                html = html.replaceFirst("%certificationIcon%", "<img src=\"" + s.getIcon() + "\" width=32 height=32>").replaceFirst("%certificationText%", s.getName().substring(s.getName().indexOf("-")).replaceFirst("-", ""));
            }
        }
        for (Skill s : transformSkillsForAllClasses) {
            if (player.getKnownSkill(s.getId()) != null) {
                for (int i = 0; i < player.getKnownSkill(s.getId()).getLevel(); i++) {
                    html = html.replaceFirst("%transformIcon%", "<img src=\"" + s.getIcon() + "\" width=32 height=32>").replaceFirst("%transformText%", s.getName().replaceFirst("Transform", ""));
                }
            }
        }
        for (int s : emergentSkillIds) {
            Skill skill = player.getKnownSkill(s);
            if (skill != null && skill.getId() != emergentSkillIds[2]) {
                html = html.replaceFirst("%percentage%", String.valueOf((((float) skill.getLevel()) / 2)) + "%");
            } else if (skill != null) {
                html = html.replaceFirst("%percentage%", String.valueOf(skill.getLevel()) + "%");
            } else {
                html = html.replaceFirst("%percentage%", "0%");
            }
            html = html.replaceFirst("%level%", skill == null ? "0" : String.valueOf(skill.getLevel()));
        }
        return html;

    }

    private String takeTransfer(L2PcInstance player, String[] splittedCommand) {
        if (!player.isInCategory(CategoryType.HEAL_GROUP)) {
            return homepage(player);
        }
        if ((player.getLevel() < MIN_LEVEL) || (player.getClassId().level() < MIN_CLASS_LEVEL)) {
            return homepage(player);
        }
        final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.TRANSFER);
        int count = 0;
        for (L2SkillLearn skillLearn : SkillTreesData.getInstance().getAvailableTransferSkills(player)) {
            if (SkillData.getInstance().getSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel()) != null) {
                count++;
                asl.addSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel(), skillLearn.getSkillLevel(), skillLearn.getLevelUpSp(), 0);
            }
        }
        if (count > 0) {
            player.sendPacket(asl);
        } else {
            player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
        }
        return homepage(player);
    }

    private static int getMinLevel(int level) {
        return switch (level) {
            case 0 -> 20;
            case 1 -> 40;
            case 2 -> 76;
            default -> Integer.MAX_VALUE;
        };
    }

    private static boolean validateClassId(ClassId oldCID, int val) {
        return validateClassId(oldCID, ClassId.getClassId(val));
    }

    private static boolean validateClassId(ClassId oldCID, ClassId newCID) {
        return (newCID != null) && (newCID.getRace() != null) && ((oldCID.equals(newCID.getParent()) || (character().allowEntireTree() && newCID.childOf(oldCID))));
    }

    public boolean canTransferClass(L2PcInstance player) {
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Fourth) {
            return false;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Third && player.getLevel() >= 76) {
            return true;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Second && player.getLevel() >= 40) {
            return true;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.First && player.getLevel() >= 20) {
            return true;
        }
        return false;
    }

    private int getTransferLevel(L2PcInstance player) {
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Fourth) {
            return 4;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Third && player.getLevel() >= 76) {
            return 3;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.Second && player.getLevel() >= 40) {
            return 2;
        }
        if (PlayerClass.values()[player.getActiveClass()].getLevel() == ClassLevel.First && player.getLevel() >= 20) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    private String showTransferClasses(L2PcInstance player) {
        String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/showtransferclasses.html");

        if (canTransferClass(player)) {
            final int minLevel = getMinLevel(player.getClassId().level());
            final int transferLevel = getTransferLevel(player);
            if (player.getLevel() >= minLevel) {
                for (ClassId cid : ClassId.values()) {
                    if ((cid == ClassId.inspector) && (player.getTotalSubClasses() < 2)) {
                        continue;
                    }
                    if (validateClassId(player.getClassId(), cid) && (cid.level() == transferLevel)) {
                        html = html.replaceFirst("%transferbutton%", transferButton.replace("%className%", cid.name()).replace("%id%", String.valueOf(cid.getId())));
                    }
                }
            }
        }
        html = html.replaceAll("%transferbutton%", "");
        return html;
    }

    private String homepage(L2PcInstance player) {
        String homepage = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/homepage.html");
        String className = PlayerClass.values()[player.getActiveClass()].name();
        homepage = homepage.replace("%className%", className);

        if (canTransferClass(player)) {
            homepage = homepage.replace("%transferButton%", transferAvailableButton);
        } else {
            homepage = homepage.replace("%transferButton%", "");
        }
        Map<Integer, SubClass> subClasses = player.getSubClasses();
        SubClass mainClass = new SubClass(player);
        mainClass.setClassId(player.getBaseClass());
        mainClass.setClassIndex(0);
        if (player.getActiveClass() != player.getBaseClass()) {
            homepage = homepage.replaceFirst("%class%", changeClassButton.replace("%className%", mainClass.getClassDefinition().name()).replace("%id%", String.valueOf(mainClass.getClassIndex())));
        }
        for (Entry<Integer, SubClass> e : subClasses.entrySet()) {

            if (e.getValue().getClassId() == player.getActiveClass()) {
                continue;
            }

            homepage = homepage.replaceFirst("%class%", changeClassButton.replace("%className%", e.getValue().getClassDefinition().name()).replace("%id%", String.valueOf(e.getValue().getClassIndex())));
        }
        homepage = homepage.replaceAll("%class%", "");
        if (player.isInCategory(CategoryType.HEAL_MASTER)) {
            homepage = homepage.replace("%healer%", learnPomanderSkillButton + resetPomanderSkillButton);
        } else {
            homepage = homepage.replace("%healer%", "");
        }

        return homepage;
    }

    private String showRemoveClasses(L2PcInstance player) {
        String resultHtml = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/removeclasses.html");
        Map<Integer, SubClass> subClasses = player.getSubClasses();
        StringBuilder str = new StringBuilder();
        str.append("<table border=\"0\">");
        subClasses.entrySet().stream().filter(entry -> entry.getValue().getClassId() != player.getActiveClass()).forEach(e -> {
            String key = e.getValue().getClassDefinition().getRace().name();
            if (e.getValue().getClassDefinition().getType().name().equalsIgnoreCase("Priest") || e.getValue().getClassDefinition().getType().name().equalsIgnoreCase("Mystic")) {
                key += " "; // add space
                key += "Mage";
            } else {
                key += " "; // add space
                key += "Fighter";
            }
            str.append(addClassTableTitle.replace("%race%", key).replace("%raceColor%", Objects.requireNonNull(RACECOLOR.getColor(key))));
            str.append(addClassButton.replace("%className%", e.getValue().getClassDefinition().name()).replace("%action%", "removeclass").replace("%id%", String.valueOf(e.getKey())));
        });
        str.append("</table>");

        resultHtml = resultHtml.replace("%list%", str.toString()).replace("DARK_ELF", "DARK ELF");
        return resultHtml;
    }

    private String showClasses(L2PcInstance player) {
        Set<PlayerClass> subClasses = getAvailableSubClasses(player);
        String addclassHtml = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/custom/class/showclasses.html");
        if (subClasses == null) {
            addclassHtml = addclassHtml.replaceAll("%list%", "");
            // CommunityBoardHandler.separateAndSend(addclassHtml, player);
            return addclassHtml;
        }

        Map<String, List<PlayerClass>> tempMap = new LinkedHashMap<>();
        subClasses.forEach(sub -> {
            String key = sub.getRace().name();
            if (sub.getType().name().equalsIgnoreCase("Priest") || sub.getType().name().equalsIgnoreCase("Mystic")) {
                key += " "; // add space
                key += "Mage";
            } else {
                key += " "; // add space
                key += "Fighter";
            }

            if (!tempMap.containsKey(key)) {
                tempMap.put(key, new ArrayList<>());
            }
            tempMap.get(key).add(sub);
        });
        List<String> tables = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        tempMap.forEach((key, classes) -> {
            str.append("<table border=\"0\" width=152>");
            str.append(addClassTableTitle.replace("%race%", key).replace("%raceColor%", Objects.requireNonNull(RACECOLOR.getColor(key))));
            classes.forEach(c -> {
                str.append(addClassButton.replace("%className%", c.name()).replace("%action%", "addclass").replace("%id%", String.valueOf(c.ordinal())));
            });
            str.append("</table>");
            tables.add(str.toString());
            str.setLength(0);
            ;
        });

        for (String s : tables) {
            addclassHtml = addclassHtml.replaceFirst("%list%", s);
        }
        addclassHtml = addclassHtml.replaceAll("DARK_ELF", "DARK ELF");
        addclassHtml = addclassHtml.replaceAll("%list%", "");
        //		 CommunityBoardHandler.separateAndSend(addclassHtml, player);
        return addclassHtml;
    }

    private String changeClass(L2PcInstance player, int classIndex) {
        player.changeActiveClass(classIndex);
        player.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED);
        return homepage(player);
    }

    private String addClass(L2PcInstance player, int classId) {

        if (player.getTotalSubClasses() >= character().getMaxSubclass()) {
            player.sendMessage("Delete a Subclass to add another one.");
            return null; // 6 subclasses max
        }
        if (player.getLevel() < 75) {
            player.sendMessage("Reach Level 76 first to add Another Subclass.");
            return null; // player must be over 75 lvl;
        }
        Map<Integer, SubClass> resultSubclass = player.getSubClasses();
        if (!resultSubclass.isEmpty()) {
            boolean result = resultSubclass.entrySet().stream().anyMatch(e -> e.getValue().getLevel() < 75);
            if (result) {
                player.sendMessage("Complete first your Subclass before adding a new one.");
                return null;
            }
        }
        boolean wasSuccessful = player.addSubClass(classId, resultSubclass.keySet().stream().max(Integer::compareTo).orElse(0) + 1);
        if (!wasSuccessful) {
            player.sendMessage("You cannot add a Subclass.");
            return null;
        }
        player.changeActiveClass(Collections.max(player.getSubClasses().keySet()));
        player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.

        return showClasses(player);
    }

    public boolean removeClass(L2PcInstance player, int classIndex) {

        DAOFactory.getInstance().getHennaDAO().deleteAll(player, classIndex);

        DAOFactory.getInstance().getSkillDAO().deleteAll(player, classIndex);

        DAOFactory.getInstance().getShortcutDAO().delete(player, classIndex);

        DAOFactory.getInstance().getPlayerSkillSaveDAO().delete(player, classIndex);

        DAOFactory.getInstance().getSubclassDAO().delete(player, classIndex);

        // Notify to scripts
        int classId = player.getSubClasses().get(classIndex).getClassId();
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionCancel(player, classId));
        player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));
        player.getSubClasses().remove(classIndex);

        return true;
    }

    private Set<PlayerClass> getAvailableSubClasses(L2PcInstance player) {
        // get player base class
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.getClassId(currentBaseId);

        // we need 2nd occupation ID
        int baseClassId = currentBaseId;

        if (baseCID != null) {
            if (baseCID.level() > 2) {
                baseClassId = baseCID.getParent().ordinal();
            }
        }

        // If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class.
        // If the race of your main class is Kamael, you may not subclass any other race If the race of your main class is NOT Kamael,
        // you may not subclass any Kamael class You may not select Overlord and Warsmith class as a subclass.
        // You may not select a similar class as the subclass.
        // The occupations classified as similar classes are as follows: Treasure Hunter, Plainswalker and Abyss Walker Hawkeye,
        // Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight
        // and Shillien Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer,
        // Spellsinger and Spellhowler Also, Kamael have a special, hidden 4 subclass, the inspector, which can only be taken if you have already completed the other two Kamael subclasses
        Set<PlayerClass> availSubs = PlayerClass.values()[baseClassId].getAvailableSubclasses(player);
        if ((availSubs != null) && !availSubs.isEmpty()) {
            for (Iterator<PlayerClass> availSub = availSubs.iterator(); availSub.hasNext(); ) {
                PlayerClass pClass = availSub.next();

                // scan for already used subclasses
                int availClassId = pClass.ordinal();
                ClassId cid = ClassId.getClassId(availClassId);
                SubClass prevSubClass;
                ClassId subClassId;
                for (SubClass subClass : player.getSubClasses().values()) {
                    prevSubClass = subClass;
                    subClassId = ClassId.getClassId(prevSubClass.getClassId());
                    if (subClassId != null && subClassId.equalsOrChildOf(cid)) {
                        availSub.remove();
                        break;
                    }
                }
            }
        }
        return availSubs;
    }

    @Override
    public String[] getBypassList() {
        return COMMANDS;
    }

}
