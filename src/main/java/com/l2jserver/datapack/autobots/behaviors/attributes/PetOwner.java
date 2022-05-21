package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.preferences.PetOwnerPreferences;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.items.instance.L2ItemInstance;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;

import java.util.List;

public interface PetOwner {
    SummonInfo getSummonInfo();
    List<SkillHolder> getPetBuffs();

    default void summonPet(Autobot player) {
        if (player.hasServitor()) return;
        Skill skill = player.getSkills().get(getSummonInfo().skill.getSkillId());
        if (skill == null) return;
        if (getSummonInfo().miscItem != null) {
            MiscItem miscItem = getSummonInfo().miscItem;
            if (player.getInventory().getItemByItemId(miscItem.getMiscItemId().apply(player)) != null) {
                if (player.getInventory().getItemByItemId(miscItem.getMiscItemId().apply(player)).getCount() <= miscItem.getMinimumAmountBeforeGive()) {
                    player.addItem("bot item", miscItem.getMiscItemId().apply(player), miscItem.getMinimumAmountBeforeGive(), null, false);
                }
            } else {
                player.addItem("bot item", miscItem.getMiscItemId().apply(player), miscItem.getMinimumAmountBeforeGive(), null, false);
                L2ItemInstance item = player.getInventory().getItemByItemId(miscItem.getMiscItemId().apply(player));
                if (item.isEquipable()) player.getInventory().equipItem(item);
            }
        }
        if (getSummonInfo().skill.getCondition().apply(player, skill, (L2Character) player.getTarget())) {
            player.useMagic(skill, false, false);
        }
    }

    default void petAssist(Autobot player) {
        if (player.getCombatBehavior().getCombatPreferences() instanceof PetOwnerPreferences) {
            if (player.hasServitor()) {
                PetOwnerPreferences prefs = (PetOwnerPreferences) player.getCombatBehavior();
                if (prefs.getPetAssists()) {
                    if (!player.hasServitor()) return;
                    if (player.getTarget() == null) return;
                    L2Summon servitor = player.getSummon();
                    if (servitor.getTarget() == null) return;
                    if (servitor.getTarget().isAutoAttackable(servitor)) {
                        if (servitor.getTarget() instanceof L2PcInstance
                                && ((L2PcInstance) servitor.getTarget()).isCursedWeaponEquipped()
                                && servitor.getLevel() < 21 || player.isCursedWeaponEquipped()
                                && player.getLevel() < 21) {
                            player.sendPacket(ActionFailed.STATIC_PACKET);
                            return;
                        }
                        if (servitor.getTarget() != null && GeoData.getInstance().canSeeTarget(servitor, servitor.getTarget())) {
                            if (prefs.getPetUsesShots()) {
                                if (player.getInventory().getItemByItemId(prefs.getPetShotId()) != null) {
                                    if (player.getInventory().getItemByItemId(prefs.getPetShotId()).getCount() <= 5) {
                                        player.getInventory().addItem("Bot item", prefs.getPetShotId(), 100, player, null);
                                    }
                                } else {
                                    player.getInventory().addItem("bot item", prefs.getPetShotId(), 100, player, null);
                                }
                                if (!player.getAutoSoulShot().contains(prefs.getPetShotId())) {
                                    player.addAutoSoulShot(prefs.getPetShotId());
                                    player.rechargeShots(true, true);
                                }
                            } else {
                                if (player.getAutoSoulShot().contains(prefs.getPetShotId())) {
                                    player.removeAutoSoulShot(prefs.getPetShotId());
                                }
                            }
                            servitor.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, servitor.getTarget());
                        }
                    }

                } else {
                    L2Summon servitor = player.getSummon();
                    if (!servitor.getFollowStatus()) {
                        servitor.followOwner();
                    }
                }
            }
        }
    }

    final class SummonInfo {
        BotSkill skill;
        MiscItem miscItem;

        public SummonInfo(BotSkill skill, MiscItem misItem) {
            this.skill = skill;
            this.miscItem = misItem;
        }

        public SummonInfo() {

        }
    }
}
