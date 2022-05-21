package com.l2jserver.datapack.autobots.behaviors;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.attributes.*;
import com.l2jserver.datapack.autobots.behaviors.preferences.*;
import com.l2jserver.datapack.autobots.skills.BotSkill;
import com.l2jserver.datapack.autobots.utils.AutobotHelpers;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.datatables.SkillData;
import com.l2jserver.gameserver.enums.ShotType;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.TeleportWhereType;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Summon;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.skills.BuffInfo;
import com.l2jserver.gameserver.model.skills.Skill;

import java.util.*;

public abstract class CombatBehavior implements ConsumableUser {

    protected final Autobot player;
    protected CombatPreferences combatPreferences;
    protected ActivityPreferences activityPreferences = new ActivityPreferences();
    protected final List<Integer> conditionalSkills = new ArrayList<>();
    protected SkillPreferences skillPreferences;
    protected L2Character committedTarget = null;
    protected boolean isMovingTowardsTarget = false;
    protected final List<Integer> blacklistedTargets = new ArrayList<>();

    public CombatBehavior(Autobot player, CombatPreferences combatPreferences) {
        this.player = player;
        this.combatPreferences = combatPreferences;
    }

    public abstract ShotType getShotType();

    protected abstract List<BotSkill> getOffensiveSkills();

    protected List<BotSkill> getSelfSupportSkills() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    public <T extends SkillPreferences> T typedSkillPreferences() {
        return (T) skillPreferences;
    }

    public void onLevelUp(int oldLevel, int newLevel) {

    }

    public void onUpdate() {
        if (player.isDead()) {
            handleDeath();
            return;
        }
        ensureSummonPet();
        handleShots();
        targetAppropriateTarget();
        checkSelfSupportingSkills();
        beforeAttack();
        attack();
        afterAttack();

    }

    public void applyBuffs() {
        combatPreferences.getBuffs().forEach(b -> {
            Skill skill = SkillData.getInstance().getSkill(b.getSkillId(), b.getSkillLvl());
            if (skill != null && !player.getEffectList().isAffectedBySkill(b.getSkillId())
                    && player.getEffectList().getBuffCount() < Configuration.character().getMaxBuffAmount()) {
                skill.applyEffects(player, player);
                if (player.hasServitor() && this instanceof PetOwner) {
                    L2Summon servitor = player.getSummon();
                    if (combatPreferences instanceof PetOwnerPreferences && ((PetOwnerPreferences) combatPreferences).getPetHasBuffs()) {
                        PetOwner petOwner = (PetOwner) this;
                        List<SkillHolder> petBuffs = petOwner.getPetBuffs();
                        petBuffs.forEach(petB -> {
                            Skill petBuff = SkillData.getInstance().getSkill(petB.getSkillId(), petB.getSkillLvl());
                            if (petBuff != null && !servitor.getEffectList().isAffectedBySkill(petB.getSkillId())
                                    && servitor.getEffectList().getBuffCount() < Configuration.character().getMaxBuffAmount()) {
                                petBuff.applyEffects(servitor, servitor);
                            }
                        });
                    }
                    if (combatPreferences instanceof PetOwnerPreferences && !(((PetOwnerPreferences) combatPreferences).getPetHasBuffs())) {
                        servitor.getEffectList().stopAllEffects();
                    }
                }
            }
        });
    }

    private void resetTarget() {
        committedTarget = null;
        player.setTarget(null);
        isMovingTowardsTarget = false;
    }

    private void handleDeath() {
        Optional<L2Object> reserAround = player.getClosestEntityInRadius(2000, it ->
                it instanceof Autobot && ((Autobot) it).getCombatBehavior() instanceof Reser
                        && (this.getPlayer().isInParty() && it.isInParty()
                        && this.getPlayer().getParty() == it.getParty()
                        || this.getPlayer().getClan() != null && it.getClan() != null
                        && this.getPlayer().getClanId() == it.getClanId()));
        if (reserAround.isPresent()) {
            Util.sleep(3000);
            return;
        }

        Util.sleep(Rnd.get(3000L, 8000L));

        Location location = MapRegionManager.getInstance().getTeleToLocation(player, TeleportWhereType.TOWN);
        if (player.isDead()) {
            player.setLastLocation();
            resetTarget();
            player.doRevive();
            player.teleToLocation(location, true);
            player.getSocialBehavior().onRespawn();
        }
    }

    public boolean validateConditionalSkill(Skill skill) {
        return skillPreferences.skillUsageConditions.stream().filter(s -> s.getSkillId() == skill.getId()).anyMatch(s -> skillPreferences.skillUsageConditions.stream().anyMatch(sc -> sc.isValid(player)));
    }

    protected void afterAttack() {
    }

    protected boolean shouldAttackPlayer() {
        if (!(player.getTarget() instanceof L2PcInstance))
            return false;

        return switch (combatPreferences.getAttackPlayerType()) {
            case Flagged -> ((L2PcInstance) player.getTarget()).getPvpFlag() > 0 || ((L2PcInstance) player.getTarget()).getKarma() > 0;
            case Innocent -> true;
            default -> false;
        };
    }

    protected Skill getNextAvailableSkill() {
        if (getOffensiveSkills().isEmpty()) return null;

        Skill skillToUse = null;
        for (BotSkill botSkill : getOffensiveSkills()) {
            Skill skill = player.getSkills().get(botSkill.getSkillId());
            if (skill == null) continue;

            if (!player.checkDoCastConditions(skill))
                continue;

            if (botSkill.getCondition().apply(player, skill, (L2Character) player.getTarget())
                    && skill.checkCondition(player, player.getTarget(), false) && skill.getMpConsume1() < player.getCurrentMp()) {
                skillToUse = skill;
                break;
            }
        }
        return skillToUse;
    }

    protected void attack() {
        if (player.getTarget() == null)
            return;

        Skill skill = getNextAvailableSkill();

        if (skill == null) {
            if (!skillPreferences.isSkillsOnly) {
                player.attack(shouldAttackPlayer());
            }
        } else {
            player.useMagicSkill(skill, shouldAttackPlayer());
        }

        if (this instanceof PetOwner) {
            ((PetOwner) this).petAssist(player);
        }
    }

    protected void beforeAttack() {
        if (this instanceof RequiresMiscItem) {
            ((RequiresMiscItem) this).handleMiscItems(player);
        }

        handleConsumables(player);

        if (this instanceof Kiter) {
            ((Kiter) this).kite(this.player);
        }

        if (this instanceof SecretManaRegen) {
            ((SecretManaRegen) this).regenMana(this.player);
        }
    }

    protected void checkSelfSupportingSkills() {
        if (getSelfSupportSkills().isEmpty()) return;

        for (BotSkill supportSkill : getSelfSupportSkills()) {
            Skill skill = player.getSkills().get(supportSkill.getSkillId());

            if (skill == null) continue;

            if (skill.isToggle() && supportSkill.isTogglableSkill() &&
                    player.getCombatBehavior().skillPreferences.togglableSkills.get(supportSkill.getSkillId()) != null &&
                    !player.getCombatBehavior().skillPreferences.togglableSkills.get(supportSkill.getSkillId()) && player.getEffectList().isAffectedBySkill(supportSkill.getSkillId())) {
                player.getEffectList().remove(true, new BuffInfo(player, player, skill));
                continue;
            }

            if (supportSkill.getCondition().apply(player, skill, supportSkill.forceTargetSelf ? player : (L2Character) player.getTarget())) {
                if (supportSkill.forceTargetSelf) {
                    L2Object previousTarget = player.getTarget();
                    player.setTarget(player);
                    player.useMagicSkill(skill, false);
                    player.setTarget(previousTarget);
                    return;
                }
                player.useMagicSkill(skill, false);
            }
        }
    }

    protected void targetAppropriateTarget() {
        tryTargetCreatureByTypeInRadius(combatPreferences.getTargetingRadius(), combatPreferences.getTargetingPreference());
    }

    protected void tryTargetCreatureByTypeInRadius(int radius, TargetingPreference targetingPreference) {
        if (player.getTarget() == null && committedTarget != null && !isMovingTowardsTarget && !committedTarget.isDead()) {
            if (GeoData.getInstance().canSeeTarget(player, committedTarget)) {
                player.setTarget(committedTarget);
            } else {
                resetTarget();
            }
        }

        if (player.getTarget() != null && ((L2Character) player.getTarget()).getTarget() == player
                && !((L2Character) player.getTarget()).isDead()
                && ((L2Character) player.getTarget()).isInCombat()
                && player.isInCombat())
            return;

        if (player.getTarget() != null
                && combatPreferences.getAttackPlayerType() == AttackPlayerType.Flagged
                && (player.getTarget() instanceof L2PcInstance
                && ((L2PcInstance) player.getTarget()).getPvpFlag() == 0 && ((L2PcInstance) player.getTarget()).getKarma() == 0)) {
            resetTarget();
        }

        if (player.getTarget() != null && player.getTarget() instanceof L2PcInstance &&
                combatPreferences.getAttackPlayerType() == AttackPlayerType.None) {
            resetTarget();
        }

        if (committedTarget != null
                && player.getTarget() != null
                && player.getTarget() == committedTarget
                && !((L2Character) player.getTarget()).isDead()) {
            if (isMovingTowardsTarget && GeoData.getInstance().canSeeTarget(player, player.getTarget())) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                isMovingTowardsTarget = false;
                blacklistedTargets.clear();
                return;
            }
        }

        L2Character closestTargeter = (L2Character) player.getClosestEntityInRadius(radius, p -> !areInTheSameClanOrParty(p)
                && (combatPreferences.getAttackPlayerType() != AttackPlayerType.None || !(p instanceof L2PcInstance))
                && !p.isDead()
                && p.getTarget() != null && p.getTarget().getObjectId() == p.getObjectId()).orElse(null);

        if ((player.getTarget() != null
                && closestTargeter != null
                && !blacklistedTargets.contains(closestTargeter.getObjectId())
                && player.getTarget() != closestTargeter && !((L2Character) player.getTarget()).isInCombat())
                && GeoData.getInstance().canSeeTarget(player, closestTargeter)) {
            player.setTarget(closestTargeter);
            committedTarget = closestTargeter;
            blacklistedTargets.clear();
            Util.sleep(Rnd.get(250L, 1000L));
            return;
        }

        if (committedTarget != null && !committedTarget.isDead()) {
            return;
        } else {
            committedTarget = null;
        }

        if (player.getTarget() == null || ((L2Character) player.getTarget()).isDead()) {
            targetAppropriateCreatureByTypeInRadius(radius, targetingPreference);
        } else {
            if (!player.isMoving()) {
                blacklistedTargets.add(player.getTargetId());
                resetTarget();
            }
        }

        if (player.getTarget() != null && ((L2Character) player.getTarget()).isDead()) {
            resetTarget();
        }
    }

    private void targetAppropriateCreatureByTypeInRadius(int radius, TargetingPreference targetingPreference) { // TODO alive only?

        List<L2Character> targets = switch (targetingPreference) {
            case Random -> AutobotHelpers.getKnownTargetablesInRadius(player, radius, combatPreferences.getAttackPlayerType(), (p) -> !areInTheSameClanOrParty(p) && !blacklistedTargets.contains(p.getObjectId()));
            case Closest -> {
                L2Character target = AutobotHelpers.getKnownTargetablesInRadius(player, radius, combatPreferences.getAttackPlayerType(), (p) -> !areInTheSameClanOrParty(p) && !blacklistedTargets.contains(p.getObjectId()))
                        .stream()
                        .min(Comparator.comparingInt(c -> (int) c.calculateDistance(player.getLocation(), false, false)))
                        .orElse(null);
                if (target != null)
                    yield List.of(target);
                yield Collections.emptyList();
            }
        };

        if (!targets.isEmpty()) {

            L2Character closestAttacker;
            if (player.getTarget() != null && ((L2Character) player.getTarget()).getHpPercentage() < 100)
                closestAttacker = null;
            else closestAttacker = switch (targetingPreference) {
                case Closest -> targets.stream().filter(t -> t.getTarget() != null && t.getTarget().getObjectId() == player.getObjectId() && t.isInCombat() && !blacklistedTargets.contains(t.getObjectId())).findFirst().orElse(null);
                case Random -> targets.stream()
                        .min(Comparator.comparingInt(t -> (int) t.calculateDistance(player.getLocation(), false, false)))
                        .filter(t -> t.getTarget() != null && t.getTarget().getObjectId() == player.getObjectId() && t.isInCombat() && !blacklistedTargets.contains(t.getObjectId()))
                        .orElse(null);
            };

            if (closestAttacker != null) {
                player.setTarget(closestAttacker);
                committedTarget = closestAttacker;
                blacklistedTargets.clear();
                return;
            }

            L2Character target = switch (targetingPreference) {
                case Closest -> switch (combatPreferences.getAttackPlayerType()) {
                    case None -> targets.stream().findFirst().orElse(null);//{ it.z <= player.z + 50 && it.z >= player.z - 50}
                    case Innocent, Flagged -> targets.stream().filter(t -> t instanceof L2PcInstance).findFirst().orElse(targets.stream().findFirst().orElse(null));
                };
                case Random -> switch (combatPreferences.getAttackPlayerType()) {
                    case None -> targets.get(Rnd.get(targets.size()));//.filter { it.z <= player.z + 50 && it.z >= player.z - 50}
                    case Innocent, Flagged -> targets.stream().filter(t -> t instanceof L2PcInstance).findFirst().orElse(targets.stream().findFirst().orElse(null));
                };
            };

            if (target == null && player.getTarget() == null)
                return;

            if (player.isMoving()) {
                return;
            }

            player.setTarget(target);
            committedTarget = target;
        }
    }

    private boolean areInTheSameClanOrParty(L2Character it) {
        return (it instanceof L2PcInstance && it.isInParty() && player.isInParty() && player.getParty() == it.getParty()) ||
                (it instanceof L2PcInstance && it.getClan() != null && player.getClan() != null && player.getClanId() == it.getClanId());
    }

    protected void handleShots() {

        if (player.getInventory().getItemByItemId(AutobotHelpers.getShotId(player)) != null) {
            if (player.getInventory().getItemByItemId(AutobotHelpers.getShotId(player)).getCount() <= 20) {
                player.getInventory().addItem("", AutobotHelpers.getShotId(player), 500, player, null);
            }
        } else {
            player.getInventory().addItem("", AutobotHelpers.getShotId(player), 500, player, null);
        }

        if (!player.getAutoSoulShot().contains(AutobotHelpers.getShotId(player))) {
            player.addAutoSoulShot(AutobotHelpers.getShotId(player));
            player.rechargeShots(true, true);
        }
    }

    protected void ensureSummonPet() {
        if (this instanceof PetOwner && combatPreferences instanceof PetOwnerPreferences) {
            if (((PetOwnerPreferences) combatPreferences).getSummonPet()) {
                ((com.l2jserver.datapack.autobots.behaviors.attributes.PetOwner) this).summonPet(player);
            } else {
                if (player.hasServitor()) player.getSummon().unSummon(player);
            }
        }
    }

    public boolean isMoving() {
        return player.isInMotion();
    }

    public Autobot getPlayer() {
        return player;
    }

    public CombatPreferences getCombatPreferences() {
        return combatPreferences;
    }

    public ActivityPreferences getActivityPreferences() {
        return activityPreferences;
    }

    public List<Integer> getConditionalSkills() {
        return conditionalSkills;
    }

    public SkillPreferences getSkillPreferences() {
        return skillPreferences;
    }


    public L2Character getCreature() {
        return committedTarget;
    }

    public boolean isMovingTowardsTarget() {
        return isMovingTowardsTarget;
    }

    public void setIsMovingTowardsTarget(boolean isMovingTowardsTarget) {
        this.isMovingTowardsTarget = isMovingTowardsTarget;
    }

    public List<Integer> getBlacklistedTargets() {
        return blacklistedTargets;
    }

    public void setCombatPreferences(CombatPreferences combatPreferences) {
        this.combatPreferences = combatPreferences;
    }

    public void setActivityPreferences(ActivityPreferences activityPreferences) {
        this.activityPreferences = activityPreferences;
    }

    public void setSkillPreferences(SkillPreferences skillPreferences) {
        this.skillPreferences = skillPreferences;
    }
}
