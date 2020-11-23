/*
 * Copyright © 2004-2020 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeAgathionEnergy;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeChameleonRest;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeFakeDeath;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeHp;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeMp;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeMpByLevel;
import com.l2jserver.datapack.handlers.effecthandlers.consume.ConsumeRest;
import com.l2jserver.datapack.handlers.effecthandlers.custom.BlockAction;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Buff;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Debuff;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Detection;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Flag;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Grow;
import com.l2jserver.datapack.handlers.effecthandlers.custom.ImmobileBuff;
import com.l2jserver.datapack.handlers.effecthandlers.custom.ImmobilePetBuff;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Mute;
import com.l2jserver.datapack.handlers.effecthandlers.custom.OpenChest;
import com.l2jserver.datapack.handlers.effecthandlers.custom.OpenDoor;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Paralyze;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Recovery;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Root;
import com.l2jserver.datapack.handlers.effecthandlers.custom.SilentMove;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Sleep;
import com.l2jserver.datapack.handlers.effecthandlers.custom.Stun;
import com.l2jserver.datapack.handlers.effecthandlers.custom.ThrowUp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.AddHate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Backstab;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Blink;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Bluff;
import com.l2jserver.datapack.handlers.effecthandlers.instant.CallParty;
import com.l2jserver.datapack.handlers.effecthandlers.instant.CallPc;
import com.l2jserver.datapack.handlers.effecthandlers.instant.CallSkill;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ChangeFace;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ChangeHairColor;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ChangeHairStyle;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ClanGate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Confuse;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ConsumeBody;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ConvertItem;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Cp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DeathLink;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DeleteHate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DeleteHateOfMe;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DetectHiddenObjects;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DispelAll;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DispelByCategory;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DispelBySlot;
import com.l2jserver.datapack.handlers.effecthandlers.instant.DispelBySlotProbability;
import com.l2jserver.datapack.handlers.effecthandlers.instant.EnergyAttack;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Escape;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FatalBlow;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Fishing;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FlySelf;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FocusEnergy;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FocusMaxEnergy;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FocusSouls;
import com.l2jserver.datapack.handlers.effecthandlers.instant.FoodForPet;
import com.l2jserver.datapack.handlers.effecthandlers.instant.GetAgro;
import com.l2jserver.datapack.handlers.effecthandlers.instant.GiveRecommendation;
import com.l2jserver.datapack.handlers.effecthandlers.instant.GiveSp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Harvesting;
import com.l2jserver.datapack.handlers.effecthandlers.instant.HeadquarterCreate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Heal;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Hp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.HpByLevel;
import com.l2jserver.datapack.handlers.effecthandlers.instant.HpDrain;
import com.l2jserver.datapack.handlers.effecthandlers.instant.HpPerMax;
import com.l2jserver.datapack.handlers.effecthandlers.instant.InstantAgathionEnergy;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Lethal;
import com.l2jserver.datapack.handlers.effecthandlers.instant.MagicalAttack;
import com.l2jserver.datapack.handlers.effecthandlers.instant.MagicalAttackByAbnormal;
import com.l2jserver.datapack.handlers.effecthandlers.instant.MagicalAttackMp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.MagicalSoulAttack;
import com.l2jserver.datapack.handlers.effecthandlers.instant.ManaHealByLevel;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Mp;
import com.l2jserver.datapack.handlers.effecthandlers.instant.MpPerMax;
import com.l2jserver.datapack.handlers.effecthandlers.instant.OpenCommonRecipeBook;
import com.l2jserver.datapack.handlers.effecthandlers.instant.OpenDwarfRecipeBook;
import com.l2jserver.datapack.handlers.effecthandlers.instant.OutpostCreate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.OutpostDestroy;
import com.l2jserver.datapack.handlers.effecthandlers.instant.PhysicalAttack;
import com.l2jserver.datapack.handlers.effecthandlers.instant.PhysicalAttackHpLink;
import com.l2jserver.datapack.handlers.effecthandlers.instant.PhysicalSoulAttack;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Pumping;
import com.l2jserver.datapack.handlers.effecthandlers.instant.RandomizeHate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.RebalanceHP;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Reeling;
import com.l2jserver.datapack.handlers.effecthandlers.instant.RefuelAirship;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Restoration;
import com.l2jserver.datapack.handlers.effecthandlers.instant.RestorationRandom;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Resurrection;
import com.l2jserver.datapack.handlers.effecthandlers.instant.RunAway;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SetSkill;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SkillTurning;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SoulBlow;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Sow;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Spoil;
import com.l2jserver.datapack.handlers.effecthandlers.instant.StaticDamage;
import com.l2jserver.datapack.handlers.effecthandlers.instant.StealAbnormal;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Summon;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SummonAgathion;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SummonCubic;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SummonNpc;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SummonPet;
import com.l2jserver.datapack.handlers.effecthandlers.instant.SummonTrap;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Sweeper;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TakeCastle;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TakeFort;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TakeFortStart;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TakeTerritoryFlag;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TargetCancel;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TargetMeProbability;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Teleport;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TeleportToTarget;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TransferHate;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TrapDetect;
import com.l2jserver.datapack.handlers.effecthandlers.instant.TrapRemove;
import com.l2jserver.datapack.handlers.effecthandlers.instant.Unsummon;
import com.l2jserver.datapack.handlers.effecthandlers.instant.UnsummonAgathion;
import com.l2jserver.datapack.handlers.effecthandlers.instant.VitalityPointUp;
import com.l2jserver.datapack.handlers.effecthandlers.pump.AttackTrait;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Betray;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockBuff;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockBuffSlot;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockChat;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockDamage;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockDebuff;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockParty;
import com.l2jserver.datapack.handlers.effecthandlers.pump.BlockResurrection;
import com.l2jserver.datapack.handlers.effecthandlers.pump.ChangeFishingMastery;
import com.l2jserver.datapack.handlers.effecthandlers.pump.CrystalGradeModify;
import com.l2jserver.datapack.handlers.effecthandlers.pump.CubicMastery;
import com.l2jserver.datapack.handlers.effecthandlers.pump.DefenceTrait;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Disarm;
import com.l2jserver.datapack.handlers.effecthandlers.pump.EnableCloak;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Fear;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Hide;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Lucky;
import com.l2jserver.datapack.handlers.effecthandlers.pump.MaxCp;
import com.l2jserver.datapack.handlers.effecthandlers.pump.MaxHp;
import com.l2jserver.datapack.handlers.effecthandlers.pump.MaxMp;
import com.l2jserver.datapack.handlers.effecthandlers.pump.NoblesseBless;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Passive;
import com.l2jserver.datapack.handlers.effecthandlers.pump.PhysicalAttackMute;
import com.l2jserver.datapack.handlers.effecthandlers.pump.PhysicalMute;
import com.l2jserver.datapack.handlers.effecthandlers.pump.ProtectionBlessing;
import com.l2jserver.datapack.handlers.effecthandlers.pump.ResistSkill;
import com.l2jserver.datapack.handlers.effecthandlers.pump.ResurrectionSpecial;
import com.l2jserver.datapack.handlers.effecthandlers.pump.ServitorShare;
import com.l2jserver.datapack.handlers.effecthandlers.pump.SingleTarget;
import com.l2jserver.datapack.handlers.effecthandlers.pump.SoulEating;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TalismanSlot;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TargetMe;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TransferDamage;
import com.l2jserver.datapack.handlers.effecthandlers.pump.Transformation;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TriggerSkillByAttack;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TriggerSkillByAvoid;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TriggerSkillByDamage;
import com.l2jserver.datapack.handlers.effecthandlers.pump.TriggerSkillBySkill;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickHp;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickHpFatal;
import com.l2jserver.datapack.handlers.effecthandlers.ticks.TickMp;
import com.l2jserver.gameserver.handler.EffectHandler;
import com.l2jserver.gameserver.model.effects.AbstractEffect;

/**
 * Effect Master handler.
 * @author BiggBoss
 * @author Zoey76
 */
public final class EffectMasterHandler {
	private static final Logger LOG = LoggerFactory.getLogger(EffectMasterHandler.class);
	
	private static final Class<?>[] EFFECTS = {
		AddHate.class,
		AttackTrait.class,
		Backstab.class,
		Betray.class,
		Blink.class,
		BlockAction.class,
		BlockBuff.class,
		BlockChat.class,
		BlockDamage.class,
		BlockDebuff.class,
		BlockParty.class,
		BlockBuffSlot.class,
		BlockResurrection.class,
		Bluff.class,
		Buff.class,
		CallParty.class,
		CallPc.class,
		CallSkill.class,
		ChangeFace.class,
		ChangeFishingMastery.class,
		ChangeHairColor.class,
		ChangeHairStyle.class,
		ClanGate.class,
		Confuse.class,
		ConsumeAgathionEnergy.class,
		ConsumeBody.class,
		ConsumeChameleonRest.class,
		ConsumeFakeDeath.class,
		ConsumeHp.class,
		ConsumeMp.class,
		ConsumeMpByLevel.class,
		ConsumeRest.class,
		ConvertItem.class,
		Cp.class,
		CrystalGradeModify.class,
		CubicMastery.class,
		DeathLink.class,
		Debuff.class,
		DefenceTrait.class,
		DeleteHate.class,
		DeleteHateOfMe.class,
		DetectHiddenObjects.class,
		Detection.class,
		Disarm.class,
		DispelAll.class,
		DispelByCategory.class,
		DispelBySlot.class,
		DispelBySlotProbability.class,
		EnableCloak.class,
		EnergyAttack.class,
		Escape.class,
		FatalBlow.class,
		Fear.class,
		Fishing.class,
		Flag.class,
		FlySelf.class,
		FocusEnergy.class,
		FocusMaxEnergy.class,
		FocusSouls.class,
		FoodForPet.class,
		GetAgro.class,
		GiveRecommendation.class,
		GiveSp.class,
		Grow.class,
		Harvesting.class,
		HeadquarterCreate.class,
		Heal.class,
		Hide.class,
		Hp.class,
		HpByLevel.class,
		HpDrain.class,
		HpPerMax.class,
		ImmobileBuff.class,
		ImmobilePetBuff.class,
		InstantAgathionEnergy.class,
		Lethal.class,
		Lucky.class,
		MagicalAttack.class,
		MagicalAttackByAbnormal.class,
		MagicalAttackMp.class,
		MagicalSoulAttack.class,
		ManaHealByLevel.class,
		MaxCp.class,
		MaxHp.class,
		MaxMp.class,
		Mp.class,
		MpPerMax.class,
		Mute.class,
		NoblesseBless.class,
		OpenChest.class,
		Unsummon.class,
		OpenCommonRecipeBook.class,
		OpenDoor.class,
		OpenDwarfRecipeBook.class,
		OutpostCreate.class,
		OutpostDestroy.class,
		Paralyze.class,
		Passive.class,
		PhysicalAttack.class,
		PhysicalAttackHpLink.class,
		PhysicalAttackMute.class,
		PhysicalMute.class,
		PhysicalSoulAttack.class,
		Pumping.class,
		ProtectionBlessing.class,
		RandomizeHate.class,
		RebalanceHP.class,
		Recovery.class,
		Reeling.class,
		RefuelAirship.class,
		ResistSkill.class,
		Restoration.class,
		RestorationRandom.class,
		Resurrection.class,
		ResurrectionSpecial.class,
		Root.class,
		RunAway.class,
		ServitorShare.class,
		SetSkill.class,
		SilentMove.class,
		SingleTarget.class,
		SkillTurning.class,
		Sleep.class,
		SoulBlow.class,
		SoulEating.class,
		Sow.class,
		Spoil.class,
		StaticDamage.class,
		StealAbnormal.class,
		Stun.class,
		Summon.class,
		SummonAgathion.class,
		SummonCubic.class,
		SummonNpc.class,
		SummonPet.class,
		SummonTrap.class,
		Sweeper.class,
		TakeCastle.class,
		TakeFort.class,
		TakeFortStart.class,
		TakeTerritoryFlag.class,
		TalismanSlot.class,
		TargetCancel.class,
		TargetMe.class,
		TargetMeProbability.class,
		Teleport.class,
		TeleportToTarget.class,
		ThrowUp.class,
		TickHp.class,
		TickHpFatal.class,
		TickMp.class,
		TransferDamage.class,
		TransferHate.class,
		Transformation.class,
		TrapDetect.class,
		TrapRemove.class,
		TriggerSkillByAttack.class,
		TriggerSkillByAvoid.class,
		TriggerSkillByDamage.class,
		TriggerSkillBySkill.class,
		UnsummonAgathion.class,
		VitalityPointUp.class,
	};
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		for (Class<?> c : EFFECTS) {
			EffectHandler.getInstance().registerHandler((Class<? extends AbstractEffect>) c);
		}
		LOG.info("Loaded {} effect handlers.", EffectHandler.getInstance().size());
	}
}
