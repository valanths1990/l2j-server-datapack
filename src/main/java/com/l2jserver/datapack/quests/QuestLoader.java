/*
 * Copyright © 2004-2021 L2J DataPack
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
package com.l2jserver.datapack.quests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.datapack.quests.Dummy.Q00201_HumanFighterTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00202_HumanMageTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00203_ElfTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00204_DarkElfTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00205_OrcTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00206_DwarfTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00207_NewbieWeaponCoupon;
import com.l2jserver.datapack.quests.Dummy.Q00208_NewbieAccessoryCoupon;
import com.l2jserver.datapack.quests.Dummy.Q00209_KamaelTutorial;
import com.l2jserver.datapack.quests.Dummy.Q00728_TerritoryWar;
import com.l2jserver.datapack.quests.Q00001_LettersOfLove.Q00001_LettersOfLove;
import com.l2jserver.datapack.quests.Q00002_WhatWomenWant.Q00002_WhatWomenWant;
import com.l2jserver.datapack.quests.Q00003_WillTheSealBeBroken.Q00003_WillTheSealBeBroken;
import com.l2jserver.datapack.quests.Q00004_LongLiveThePaagrioLord.Q00004_LongLiveThePaagrioLord;
import com.l2jserver.datapack.quests.Q00005_MinersFavor.Q00005_MinersFavor;
import com.l2jserver.datapack.quests.Q00006_StepIntoTheFuture.Q00006_StepIntoTheFuture;
import com.l2jserver.datapack.quests.Q00007_ATripBegins.Q00007_ATripBegins;
import com.l2jserver.datapack.quests.Q00008_AnAdventureBegins.Q00008_AnAdventureBegins;
import com.l2jserver.datapack.quests.Q00009_IntoTheCityOfHumans.Q00009_IntoTheCityOfHumans;
import com.l2jserver.datapack.quests.Q00010_IntoTheWorld.Q00010_IntoTheWorld;
import com.l2jserver.datapack.quests.Q00011_SecretMeetingWithKetraOrcs.Q00011_SecretMeetingWithKetraOrcs;
import com.l2jserver.datapack.quests.Q00012_SecretMeetingWithVarkaSilenos.Q00012_SecretMeetingWithVarkaSilenos;
import com.l2jserver.datapack.quests.Q00013_ParcelDelivery.Q00013_ParcelDelivery;
import com.l2jserver.datapack.quests.Q00014_WhereaboutsOfTheArchaeologist.Q00014_WhereaboutsOfTheArchaeologist;
import com.l2jserver.datapack.quests.Q00015_SweetWhispers.Q00015_SweetWhispers;
import com.l2jserver.datapack.quests.Q00016_TheComingDarkness.Q00016_TheComingDarkness;
import com.l2jserver.datapack.quests.Q00017_LightAndDarkness.Q00017_LightAndDarkness;
import com.l2jserver.datapack.quests.Q00018_MeetingWithTheGoldenRam.Q00018_MeetingWithTheGoldenRam;
import com.l2jserver.datapack.quests.Q00019_GoToThePastureland.Q00019_GoToThePastureland;
import com.l2jserver.datapack.quests.Q00020_BringUpWithLove.Q00020_BringUpWithLove;
import com.l2jserver.datapack.quests.Q00021_HiddenTruth.Q00021_HiddenTruth;
import com.l2jserver.datapack.quests.Q00022_TragedyInVonHellmannForest.Q00022_TragedyInVonHellmannForest;
import com.l2jserver.datapack.quests.Q00023_LidiasHeart.Q00023_LidiasHeart;
import com.l2jserver.datapack.quests.Q00024_InhabitantsOfTheForestOfTheDead.Q00024_InhabitantsOfTheForestOfTheDead;
import com.l2jserver.datapack.quests.Q00025_HidingBehindTheTruth.Q00025_HidingBehindTheTruth;
import com.l2jserver.datapack.quests.Q00026_TiredOfWaiting.Q00026_TiredOfWaiting;
import com.l2jserver.datapack.quests.Q00027_ChestCaughtWithABaitOfWind.Q00027_ChestCaughtWithABaitOfWind;
import com.l2jserver.datapack.quests.Q00028_ChestCaughtWithABaitOfIcyAir.Q00028_ChestCaughtWithABaitOfIcyAir;
import com.l2jserver.datapack.quests.Q00029_ChestCaughtWithABaitOfEarth.Q00029_ChestCaughtWithABaitOfEarth;
import com.l2jserver.datapack.quests.Q00030_ChestCaughtWithABaitOfFire.Q00030_ChestCaughtWithABaitOfFire;
import com.l2jserver.datapack.quests.Q00031_SecretBuriedInTheSwamp.Q00031_SecretBuriedInTheSwamp;
import com.l2jserver.datapack.quests.Q00032_AnObviousLie.Q00032_AnObviousLie;
import com.l2jserver.datapack.quests.Q00033_MakeAPairOfDressShoes.Q00033_MakeAPairOfDressShoes;
import com.l2jserver.datapack.quests.Q00034_InSearchOfCloth.Q00034_InSearchOfCloth;
import com.l2jserver.datapack.quests.Q00035_FindGlitteringJewelry.Q00035_FindGlitteringJewelry;
import com.l2jserver.datapack.quests.Q00036_MakeASewingKit.Q00036_MakeASewingKit;
import com.l2jserver.datapack.quests.Q00037_MakeFormalWear.Q00037_MakeFormalWear;
import com.l2jserver.datapack.quests.Q00038_DragonFangs.Q00038_DragonFangs;
import com.l2jserver.datapack.quests.Q00039_RedEyedInvaders.Q00039_RedEyedInvaders;
import com.l2jserver.datapack.quests.Q00040_ASpecialOrder.Q00040_ASpecialOrder;
import com.l2jserver.datapack.quests.Q00042_HelpTheUncle.Q00042_HelpTheUncle;
import com.l2jserver.datapack.quests.Q00043_HelpTheSister.Q00043_HelpTheSister;
import com.l2jserver.datapack.quests.Q00044_HelpTheSon.Q00044_HelpTheSon;
import com.l2jserver.datapack.quests.Q00045_ToTalkingIsland.Q00045_ToTalkingIsland;
import com.l2jserver.datapack.quests.Q00046_OnceMoreInTheArmsOfTheMotherTree.Q00046_OnceMoreInTheArmsOfTheMotherTree;
import com.l2jserver.datapack.quests.Q00047_IntoTheDarkElvenForest.Q00047_IntoTheDarkElvenForest;
import com.l2jserver.datapack.quests.Q00048_ToTheImmortalPlateau.Q00048_ToTheImmortalPlateau;
import com.l2jserver.datapack.quests.Q00049_TheRoadHome.Q00049_TheRoadHome;
import com.l2jserver.datapack.quests.Q00050_LanoscosSpecialBait.Q00050_LanoscosSpecialBait;
import com.l2jserver.datapack.quests.Q00051_OFullesSpecialBait.Q00051_OFullesSpecialBait;
import com.l2jserver.datapack.quests.Q00052_WilliesSpecialBait.Q00052_WilliesSpecialBait;
import com.l2jserver.datapack.quests.Q00053_LinnaeusSpecialBait.Q00053_LinnaeusSpecialBait;
import com.l2jserver.datapack.quests.Q00060_GoodWorksReward.Q00060_GoodWorksReward;
import com.l2jserver.datapack.quests.Q00061_LawEnforcement.Q00061_LawEnforcement;
import com.l2jserver.datapack.quests.Q00062_PathOfTheTrooper.Q00062_PathOfTheTrooper;
import com.l2jserver.datapack.quests.Q00063_PathOfTheWarder.Q00063_PathOfTheWarder;
import com.l2jserver.datapack.quests.Q00064_CertifiedBerserker.Q00064_CertifiedBerserker;
import com.l2jserver.datapack.quests.Q00065_CertifiedSoulBreaker.Q00065_CertifiedSoulBreaker;
import com.l2jserver.datapack.quests.Q00066_CertifiedArbalester.Q00066_CertifiedArbalester;
import com.l2jserver.datapack.quests.Q00067_SagaOfTheDoombringer.Q00067_SagaOfTheDoombringer;
import com.l2jserver.datapack.quests.Q00068_SagaOfTheSoulHound.Q00068_SagaOfTheSoulHound;
import com.l2jserver.datapack.quests.Q00069_SagaOfTheTrickster.Q00069_SagaOfTheTrickster;
import com.l2jserver.datapack.quests.Q00070_SagaOfThePhoenixKnight.Q00070_SagaOfThePhoenixKnight;
import com.l2jserver.datapack.quests.Q00071_SagaOfEvasTemplar.Q00071_SagaOfEvasTemplar;
import com.l2jserver.datapack.quests.Q00072_SagaOfTheSwordMuse.Q00072_SagaOfTheSwordMuse;
import com.l2jserver.datapack.quests.Q00073_SagaOfTheDuelist.Q00073_SagaOfTheDuelist;
import com.l2jserver.datapack.quests.Q00074_SagaOfTheDreadnought.Q00074_SagaOfTheDreadnought;
import com.l2jserver.datapack.quests.Q00075_SagaOfTheTitan.Q00075_SagaOfTheTitan;
import com.l2jserver.datapack.quests.Q00076_SagaOfTheGrandKhavatari.Q00076_SagaOfTheGrandKhavatari;
import com.l2jserver.datapack.quests.Q00077_SagaOfTheDominator.Q00077_SagaOfTheDominator;
import com.l2jserver.datapack.quests.Q00078_SagaOfTheDoomcryer.Q00078_SagaOfTheDoomcryer;
import com.l2jserver.datapack.quests.Q00079_SagaOfTheAdventurer.Q00079_SagaOfTheAdventurer;
import com.l2jserver.datapack.quests.Q00080_SagaOfTheWindRider.Q00080_SagaOfTheWindRider;
import com.l2jserver.datapack.quests.Q00081_SagaOfTheGhostHunter.Q00081_SagaOfTheGhostHunter;
import com.l2jserver.datapack.quests.Q00082_SagaOfTheSagittarius.Q00082_SagaOfTheSagittarius;
import com.l2jserver.datapack.quests.Q00083_SagaOfTheMoonlightSentinel.Q00083_SagaOfTheMoonlightSentinel;
import com.l2jserver.datapack.quests.Q00084_SagaOfTheGhostSentinel.Q00084_SagaOfTheGhostSentinel;
import com.l2jserver.datapack.quests.Q00085_SagaOfTheCardinal.Q00085_SagaOfTheCardinal;
import com.l2jserver.datapack.quests.Q00086_SagaOfTheHierophant.Q00086_SagaOfTheHierophant;
import com.l2jserver.datapack.quests.Q00087_SagaOfEvasSaint.Q00087_SagaOfEvasSaint;
import com.l2jserver.datapack.quests.Q00088_SagaOfTheArchmage.Q00088_SagaOfTheArchmage;
import com.l2jserver.datapack.quests.Q00089_SagaOfTheMysticMuse.Q00089_SagaOfTheMysticMuse;
import com.l2jserver.datapack.quests.Q00090_SagaOfTheStormScreamer.Q00090_SagaOfTheStormScreamer;
import com.l2jserver.datapack.quests.Q00091_SagaOfTheArcanaLord.Q00091_SagaOfTheArcanaLord;
import com.l2jserver.datapack.quests.Q00092_SagaOfTheElementalMaster.Q00092_SagaOfTheElementalMaster;
import com.l2jserver.datapack.quests.Q00093_SagaOfTheSpectralMaster.Q00093_SagaOfTheSpectralMaster;
import com.l2jserver.datapack.quests.Q00094_SagaOfTheSoultaker.Q00094_SagaOfTheSoultaker;
import com.l2jserver.datapack.quests.Q00095_SagaOfTheHellKnight.Q00095_SagaOfTheHellKnight;
import com.l2jserver.datapack.quests.Q00096_SagaOfTheSpectralDancer.Q00096_SagaOfTheSpectralDancer;
import com.l2jserver.datapack.quests.Q00097_SagaOfTheShillienTemplar.Q00097_SagaOfTheShillienTemplar;
import com.l2jserver.datapack.quests.Q00098_SagaOfTheShillienSaint.Q00098_SagaOfTheShillienSaint;
import com.l2jserver.datapack.quests.Q00099_SagaOfTheFortuneSeeker.Q00099_SagaOfTheFortuneSeeker;
import com.l2jserver.datapack.quests.Q00100_SagaOfTheMaestro.Q00100_SagaOfTheMaestro;
import com.l2jserver.datapack.quests.Q00101_SwordOfSolidarity.Q00101_SwordOfSolidarity;
import com.l2jserver.datapack.quests.Q00102_SeaOfSporesFever.Q00102_SeaOfSporesFever;
import com.l2jserver.datapack.quests.Q00103_SpiritOfCraftsman.Q00103_SpiritOfCraftsman;
import com.l2jserver.datapack.quests.Q00104_SpiritOfMirrors.Q00104_SpiritOfMirrors;
import com.l2jserver.datapack.quests.Q00105_SkirmishWithOrcs.Q00105_SkirmishWithOrcs;
import com.l2jserver.datapack.quests.Q00106_ForgottenTruth.Q00106_ForgottenTruth;
import com.l2jserver.datapack.quests.Q00107_MercilessPunishment.Q00107_MercilessPunishment;
import com.l2jserver.datapack.quests.Q00108_JumbleTumbleDiamondFuss.Q00108_JumbleTumbleDiamondFuss;
import com.l2jserver.datapack.quests.Q00109_InSearchOfTheNest.Q00109_InSearchOfTheNest;
import com.l2jserver.datapack.quests.Q00110_ToThePrimevalIsle.Q00110_ToThePrimevalIsle;
import com.l2jserver.datapack.quests.Q00111_ElrokianHuntersProof.Q00111_ElrokianHuntersProof;
import com.l2jserver.datapack.quests.Q00112_WalkOfFate.Q00112_WalkOfFate;
import com.l2jserver.datapack.quests.Q00113_StatusOfTheBeaconTower.Q00113_StatusOfTheBeaconTower;
import com.l2jserver.datapack.quests.Q00114_ResurrectionOfAnOldManager.Q00114_ResurrectionOfAnOldManager;
import com.l2jserver.datapack.quests.Q00115_TheOtherSideOfTruth.Q00115_TheOtherSideOfTruth;
import com.l2jserver.datapack.quests.Q00116_BeyondTheHillsOfWinter.Q00116_BeyondTheHillsOfWinter;
import com.l2jserver.datapack.quests.Q00117_TheOceanOfDistantStars.Q00117_TheOceanOfDistantStars;
import com.l2jserver.datapack.quests.Q00118_ToLeadAndBeLed.Q00118_ToLeadAndBeLed;
import com.l2jserver.datapack.quests.Q00119_LastImperialPrince.Q00119_LastImperialPrince;
import com.l2jserver.datapack.quests.Q00120_PavelsLastResearch.Q00120_PavelsLastResearch;
import com.l2jserver.datapack.quests.Q00121_PavelTheGiant.Q00121_PavelTheGiant;
import com.l2jserver.datapack.quests.Q00122_OminousNews.Q00122_OminousNews;
import com.l2jserver.datapack.quests.Q00123_TheLeaderAndTheFollower.Q00123_TheLeaderAndTheFollower;
import com.l2jserver.datapack.quests.Q00124_MeetingTheElroki.Q00124_MeetingTheElroki;
import com.l2jserver.datapack.quests.Q00125_TheNameOfEvil1.Q00125_TheNameOfEvil1;
import com.l2jserver.datapack.quests.Q00126_TheNameOfEvil2.Q00126_TheNameOfEvil2;
import com.l2jserver.datapack.quests.Q00128_PailakaSongOfIceAndFire.Q00128_PailakaSongOfIceAndFire;
import com.l2jserver.datapack.quests.Q00129_PailakaDevilsLegacy.Q00129_PailakaDevilsLegacy;
import com.l2jserver.datapack.quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import com.l2jserver.datapack.quests.Q00131_BirdInACage.Q00131_BirdInACage;
import com.l2jserver.datapack.quests.Q00132_MatrasCuriosity.Q00132_MatrasCuriosity;
import com.l2jserver.datapack.quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;
import com.l2jserver.datapack.quests.Q00134_TempleMissionary.Q00134_TempleMissionary;
import com.l2jserver.datapack.quests.Q00135_TempleExecutor.Q00135_TempleExecutor;
import com.l2jserver.datapack.quests.Q00136_MoreThanMeetsTheEye.Q00136_MoreThanMeetsTheEye;
import com.l2jserver.datapack.quests.Q00137_TempleChampionPart1.Q00137_TempleChampionPart1;
import com.l2jserver.datapack.quests.Q00138_TempleChampionPart2.Q00138_TempleChampionPart2;
import com.l2jserver.datapack.quests.Q00139_ShadowFoxPart1.Q00139_ShadowFoxPart1;
import com.l2jserver.datapack.quests.Q00140_ShadowFoxPart2.Q00140_ShadowFoxPart2;
import com.l2jserver.datapack.quests.Q00141_ShadowFoxPart3.Q00141_ShadowFoxPart3;
import com.l2jserver.datapack.quests.Q00142_FallenAngelRequestOfDawn.Q00142_FallenAngelRequestOfDawn;
import com.l2jserver.datapack.quests.Q00143_FallenAngelRequestOfDusk.Q00143_FallenAngelRequestOfDusk;
import com.l2jserver.datapack.quests.Q00146_TheZeroHour.Q00146_TheZeroHour;
import com.l2jserver.datapack.quests.Q00147_PathtoBecominganEliteMercenary.Q00147_PathtoBecominganEliteMercenary;
import com.l2jserver.datapack.quests.Q00148_PathtoBecominganExaltedMercenary.Q00148_PathtoBecominganExaltedMercenary;
import com.l2jserver.datapack.quests.Q00151_CureForFever.Q00151_CureForFever;
import com.l2jserver.datapack.quests.Q00152_ShardsOfGolem.Q00152_ShardsOfGolem;
import com.l2jserver.datapack.quests.Q00153_DeliverGoods.Q00153_DeliverGoods;
import com.l2jserver.datapack.quests.Q00154_SacrificeToTheSea.Q00154_SacrificeToTheSea;
import com.l2jserver.datapack.quests.Q00155_FindSirWindawood.Q00155_FindSirWindawood;
import com.l2jserver.datapack.quests.Q00156_MillenniumLove.Q00156_MillenniumLove;
import com.l2jserver.datapack.quests.Q00157_RecoverSmuggledGoods.Q00157_RecoverSmuggledGoods;
import com.l2jserver.datapack.quests.Q00158_SeedOfEvil.Q00158_SeedOfEvil;
import com.l2jserver.datapack.quests.Q00159_ProtectTheWaterSource.Q00159_ProtectTheWaterSource;
import com.l2jserver.datapack.quests.Q00160_NerupasRequest.Q00160_NerupasRequest;
import com.l2jserver.datapack.quests.Q00161_FruitOfTheMotherTree.Q00161_FruitOfTheMotherTree;
import com.l2jserver.datapack.quests.Q00162_CurseOfTheUndergroundFortress.Q00162_CurseOfTheUndergroundFortress;
import com.l2jserver.datapack.quests.Q00163_LegacyOfThePoet.Q00163_LegacyOfThePoet;
import com.l2jserver.datapack.quests.Q00164_BloodFiend.Q00164_BloodFiend;
import com.l2jserver.datapack.quests.Q00165_ShilensHunt.Q00165_ShilensHunt;
import com.l2jserver.datapack.quests.Q00166_MassOfDarkness.Q00166_MassOfDarkness;
import com.l2jserver.datapack.quests.Q00167_DwarvenKinship.Q00167_DwarvenKinship;
import com.l2jserver.datapack.quests.Q00168_DeliverSupplies.Q00168_DeliverSupplies;
import com.l2jserver.datapack.quests.Q00169_OffspringOfNightmares.Q00169_OffspringOfNightmares;
import com.l2jserver.datapack.quests.Q00170_DangerousSeduction.Q00170_DangerousSeduction;
import com.l2jserver.datapack.quests.Q00171_ActsOfEvil.Q00171_ActsOfEvil;
import com.l2jserver.datapack.quests.Q00172_NewHorizons.Q00172_NewHorizons;
import com.l2jserver.datapack.quests.Q00173_ToTheIsleOfSouls.Q00173_ToTheIsleOfSouls;
import com.l2jserver.datapack.quests.Q00174_SupplyCheck.Q00174_SupplyCheck;
import com.l2jserver.datapack.quests.Q00175_TheWayOfTheWarrior.Q00175_TheWayOfTheWarrior;
import com.l2jserver.datapack.quests.Q00176_StepsForHonor.Q00176_StepsForHonor;
import com.l2jserver.datapack.quests.Q00178_IconicTrinity.Q00178_IconicTrinity;
import com.l2jserver.datapack.quests.Q00179_IntoTheLargeCavern.Q00179_IntoTheLargeCavern;
import com.l2jserver.datapack.quests.Q00182_NewRecruits.Q00182_NewRecruits;
import com.l2jserver.datapack.quests.Q00183_RelicExploration.Q00183_RelicExploration;
import com.l2jserver.datapack.quests.Q00184_ArtOfPersuasion.Q00184_ArtOfPersuasion;
import com.l2jserver.datapack.quests.Q00185_NikolasCooperation.Q00185_NikolasCooperation;
import com.l2jserver.datapack.quests.Q00186_ContractExecution.Q00186_ContractExecution;
import com.l2jserver.datapack.quests.Q00187_NikolasHeart.Q00187_NikolasHeart;
import com.l2jserver.datapack.quests.Q00188_SealRemoval.Q00188_SealRemoval;
import com.l2jserver.datapack.quests.Q00189_ContractCompletion.Q00189_ContractCompletion;
import com.l2jserver.datapack.quests.Q00190_LostDream.Q00190_LostDream;
import com.l2jserver.datapack.quests.Q00191_VainConclusion.Q00191_VainConclusion;
import com.l2jserver.datapack.quests.Q00192_SevenSignsSeriesOfDoubt.Q00192_SevenSignsSeriesOfDoubt;
import com.l2jserver.datapack.quests.Q00193_SevenSignsDyingMessage.Q00193_SevenSignsDyingMessage;
import com.l2jserver.datapack.quests.Q00194_SevenSignsMammonsContract.Q00194_SevenSignsMammonsContract;
import com.l2jserver.datapack.quests.Q00195_SevenSignsSecretRitualOfThePriests.Q00195_SevenSignsSecretRitualOfThePriests;
import com.l2jserver.datapack.quests.Q00196_SevenSignsSealOfTheEmperor.Q00196_SevenSignsSealOfTheEmperor;
import com.l2jserver.datapack.quests.Q00197_SevenSignsTheSacredBookOfSeal.Q00197_SevenSignsTheSacredBookOfSeal;
import com.l2jserver.datapack.quests.Q00198_SevenSignsEmbryo.Q00198_SevenSignsEmbryo;
import com.l2jserver.datapack.quests.Q00211_TrialOfTheChallenger.Q00211_TrialOfTheChallenger;
import com.l2jserver.datapack.quests.Q00212_TrialOfDuty.Q00212_TrialOfDuty;
import com.l2jserver.datapack.quests.Q00213_TrialOfTheSeeker.Q00213_TrialOfTheSeeker;
import com.l2jserver.datapack.quests.Q00214_TrialOfTheScholar.Q00214_TrialOfTheScholar;
import com.l2jserver.datapack.quests.Q00215_TrialOfThePilgrim.Q00215_TrialOfThePilgrim;
import com.l2jserver.datapack.quests.Q00216_TrialOfTheGuildsman.Q00216_TrialOfTheGuildsman;
import com.l2jserver.datapack.quests.Q00217_TestimonyOfTrust.Q00217_TestimonyOfTrust;
import com.l2jserver.datapack.quests.Q00218_TestimonyOfLife.Q00218_TestimonyOfLife;
import com.l2jserver.datapack.quests.Q00219_TestimonyOfFate.Q00219_TestimonyOfFate;
import com.l2jserver.datapack.quests.Q00220_TestimonyOfGlory.Q00220_TestimonyOfGlory;
import com.l2jserver.datapack.quests.Q00221_TestimonyOfProsperity.Q00221_TestimonyOfProsperity;
import com.l2jserver.datapack.quests.Q00222_TestOfTheDuelist.Q00222_TestOfTheDuelist;
import com.l2jserver.datapack.quests.Q00223_TestOfTheChampion.Q00223_TestOfTheChampion;
import com.l2jserver.datapack.quests.Q00224_TestOfSagittarius.Q00224_TestOfSagittarius;
import com.l2jserver.datapack.quests.Q00225_TestOfTheSearcher.Q00225_TestOfTheSearcher;
import com.l2jserver.datapack.quests.Q00226_TestOfTheHealer.Q00226_TestOfTheHealer;
import com.l2jserver.datapack.quests.Q00227_TestOfTheReformer.Q00227_TestOfTheReformer;
import com.l2jserver.datapack.quests.Q00228_TestOfMagus.Q00228_TestOfMagus;
import com.l2jserver.datapack.quests.Q00229_TestOfWitchcraft.Q00229_TestOfWitchcraft;
import com.l2jserver.datapack.quests.Q00230_TestOfTheSummoner.Q00230_TestOfTheSummoner;
import com.l2jserver.datapack.quests.Q00231_TestOfTheMaestro.Q00231_TestOfTheMaestro;
import com.l2jserver.datapack.quests.Q00232_TestOfTheLord.Q00232_TestOfTheLord;
import com.l2jserver.datapack.quests.Q00233_TestOfTheWarSpirit.Q00233_TestOfTheWarSpirit;
import com.l2jserver.datapack.quests.Q00234_FatesWhisper.Q00234_FatesWhisper;
import com.l2jserver.datapack.quests.Q00235_MimirsElixir.Q00235_MimirsElixir;
import com.l2jserver.datapack.quests.Q00236_SeedsOfChaos.Q00236_SeedsOfChaos;
import com.l2jserver.datapack.quests.Q00237_WindsOfChange.Q00237_WindsOfChange;
import com.l2jserver.datapack.quests.Q00238_SuccessFailureOfBusiness.Q00238_SuccessFailureOfBusiness;
import com.l2jserver.datapack.quests.Q00239_WontYouJoinUs.Q00239_WontYouJoinUs;
import com.l2jserver.datapack.quests.Q00240_ImTheOnlyOneYouCanTrust.Q00240_ImTheOnlyOneYouCanTrust;
import com.l2jserver.datapack.quests.Q00241_PossessorOfAPreciousSoul1.Q00241_PossessorOfAPreciousSoul1;
import com.l2jserver.datapack.quests.Q00242_PossessorOfAPreciousSoul2.Q00242_PossessorOfAPreciousSoul2;
import com.l2jserver.datapack.quests.Q00246_PossessorOfAPreciousSoul3.Q00246_PossessorOfAPreciousSoul3;
import com.l2jserver.datapack.quests.Q00247_PossessorOfAPreciousSoul4.Q00247_PossessorOfAPreciousSoul4;
import com.l2jserver.datapack.quests.Q00249_PoisonedPlainsOfTheLizardmen.Q00249_PoisonedPlainsOfTheLizardmen;
import com.l2jserver.datapack.quests.Q00250_WatchWhatYouEat.Q00250_WatchWhatYouEat;
import com.l2jserver.datapack.quests.Q00251_NoSecrets.Q00251_NoSecrets;
import com.l2jserver.datapack.quests.Q00252_ItSmellsDelicious.Q00252_ItSmellsDelicious;
import com.l2jserver.datapack.quests.Q00254_LegendaryTales.Q00254_LegendaryTales;
import com.l2jserver.datapack.quests.Q00255_Tutorial.Q00255_Tutorial;
import com.l2jserver.datapack.quests.Q00257_TheGuardIsBusy.Q00257_TheGuardIsBusy;
import com.l2jserver.datapack.quests.Q00258_BringWolfPelts.Q00258_BringWolfPelts;
import com.l2jserver.datapack.quests.Q00259_RequestFromTheFarmOwner.Q00259_RequestFromTheFarmOwner;
import com.l2jserver.datapack.quests.Q00260_OrcHunting.Q00260_OrcHunting;
import com.l2jserver.datapack.quests.Q00261_CollectorsDream.Q00261_CollectorsDream;
import com.l2jserver.datapack.quests.Q00262_TradeWithTheIvoryTower.Q00262_TradeWithTheIvoryTower;
import com.l2jserver.datapack.quests.Q00263_OrcSubjugation.Q00263_OrcSubjugation;
import com.l2jserver.datapack.quests.Q00264_KeenClaws.Q00264_KeenClaws;
import com.l2jserver.datapack.quests.Q00265_BondsOfSlavery.Q00265_BondsOfSlavery;
import com.l2jserver.datapack.quests.Q00266_PleasOfPixies.Q00266_PleasOfPixies;
import com.l2jserver.datapack.quests.Q00267_WrathOfVerdure.Q00267_WrathOfVerdure;
import com.l2jserver.datapack.quests.Q00268_TracesOfEvil.Q00268_TracesOfEvil;
import com.l2jserver.datapack.quests.Q00269_InventionAmbition.Q00269_InventionAmbition;
import com.l2jserver.datapack.quests.Q00270_TheOneWhoEndsSilence.Q00270_TheOneWhoEndsSilence;
import com.l2jserver.datapack.quests.Q00271_ProofOfValor.Q00271_ProofOfValor;
import com.l2jserver.datapack.quests.Q00272_WrathOfAncestors.Q00272_WrathOfAncestors;
import com.l2jserver.datapack.quests.Q00273_InvadersOfTheHolyLand.Q00273_InvadersOfTheHolyLand;
import com.l2jserver.datapack.quests.Q00274_SkirmishWithTheWerewolves.Q00274_SkirmishWithTheWerewolves;
import com.l2jserver.datapack.quests.Q00275_DarkWingedSpies.Q00275_DarkWingedSpies;
import com.l2jserver.datapack.quests.Q00276_TotemOfTheHestui.Q00276_TotemOfTheHestui;
import com.l2jserver.datapack.quests.Q00277_GatekeepersOffering.Q00277_GatekeepersOffering;
import com.l2jserver.datapack.quests.Q00278_HomeSecurity.Q00278_HomeSecurity;
import com.l2jserver.datapack.quests.Q00279_TargetOfOpportunity.Q00279_TargetOfOpportunity;
import com.l2jserver.datapack.quests.Q00280_TheFoodChain.Q00280_TheFoodChain;
import com.l2jserver.datapack.quests.Q00281_HeadForTheHills.Q00281_HeadForTheHills;
import com.l2jserver.datapack.quests.Q00283_TheFewTheProudTheBrave.Q00283_TheFewTheProudTheBrave;
import com.l2jserver.datapack.quests.Q00284_MuertosFeather.Q00284_MuertosFeather;
import com.l2jserver.datapack.quests.Q00286_FabulousFeathers.Q00286_FabulousFeathers;
import com.l2jserver.datapack.quests.Q00287_FiguringItOut.Q00287_FiguringItOut;
import com.l2jserver.datapack.quests.Q00288_HandleWithCare.Q00288_HandleWithCare;
import com.l2jserver.datapack.quests.Q00289_NoMoreSoupForYou.Q00289_NoMoreSoupForYou;
import com.l2jserver.datapack.quests.Q00290_ThreatRemoval.Q00290_ThreatRemoval;
import com.l2jserver.datapack.quests.Q00291_RevengeOfTheRedbonnet.Q00291_RevengeOfTheRedbonnet;
import com.l2jserver.datapack.quests.Q00292_BrigandsSweep.Q00292_BrigandsSweep;
import com.l2jserver.datapack.quests.Q00293_TheHiddenVeins.Q00293_TheHiddenVeins;
import com.l2jserver.datapack.quests.Q00294_CovertBusiness.Q00294_CovertBusiness;
import com.l2jserver.datapack.quests.Q00295_DreamingOfTheSkies.Q00295_DreamingOfTheSkies;
import com.l2jserver.datapack.quests.Q00296_TarantulasSpiderSilk.Q00296_TarantulasSpiderSilk;
import com.l2jserver.datapack.quests.Q00297_GatekeepersFavor.Q00297_GatekeepersFavor;
import com.l2jserver.datapack.quests.Q00298_LizardmensConspiracy.Q00298_LizardmensConspiracy;
import com.l2jserver.datapack.quests.Q00299_GatherIngredientsForPie.Q00299_GatherIngredientsForPie;
import com.l2jserver.datapack.quests.Q00300_HuntingLetoLizardman.Q00300_HuntingLetoLizardman;
import com.l2jserver.datapack.quests.Q00303_CollectArrowheads.Q00303_CollectArrowheads;
import com.l2jserver.datapack.quests.Q00306_CrystalOfFireAndIce.Q00306_CrystalOfFireAndIce;
import com.l2jserver.datapack.quests.Q00307_ControlDeviceOfTheGiants.Q00307_ControlDeviceOfTheGiants;
import com.l2jserver.datapack.quests.Q00308_ReedFieldMaintenance.Q00308_ReedFieldMaintenance;
import com.l2jserver.datapack.quests.Q00309_ForAGoodCause.Q00309_ForAGoodCause;
import com.l2jserver.datapack.quests.Q00310_OnlyWhatRemains.Q00310_OnlyWhatRemains;
import com.l2jserver.datapack.quests.Q00311_ExpulsionOfEvilSpirits.Q00311_ExpulsionOfEvilSpirits;
import com.l2jserver.datapack.quests.Q00312_TakeAdvantageOfTheCrisis.Q00312_TakeAdvantageOfTheCrisis;
import com.l2jserver.datapack.quests.Q00313_CollectSpores.Q00313_CollectSpores;
import com.l2jserver.datapack.quests.Q00316_DestroyPlagueCarriers.Q00316_DestroyPlagueCarriers;
import com.l2jserver.datapack.quests.Q00317_CatchTheWind.Q00317_CatchTheWind;
import com.l2jserver.datapack.quests.Q00319_ScentOfDeath.Q00319_ScentOfDeath;
import com.l2jserver.datapack.quests.Q00320_BonesTellTheFuture.Q00320_BonesTellTheFuture;
import com.l2jserver.datapack.quests.Q00324_SweetestVenom.Q00324_SweetestVenom;
import com.l2jserver.datapack.quests.Q00325_GrimCollector.Q00325_GrimCollector;
import com.l2jserver.datapack.quests.Q00326_VanquishRemnants.Q00326_VanquishRemnants;
import com.l2jserver.datapack.quests.Q00327_RecoverTheFarmland.Q00327_RecoverTheFarmland;
import com.l2jserver.datapack.quests.Q00328_SenseForBusiness.Q00328_SenseForBusiness;
import com.l2jserver.datapack.quests.Q00329_CuriosityOfADwarf.Q00329_CuriosityOfADwarf;
import com.l2jserver.datapack.quests.Q00330_AdeptOfTaste.Q00330_AdeptOfTaste;
import com.l2jserver.datapack.quests.Q00331_ArrowOfVengeance.Q00331_ArrowOfVengeance;
import com.l2jserver.datapack.quests.Q00333_HuntOfTheBlackLion.Q00333_HuntOfTheBlackLion;
import com.l2jserver.datapack.quests.Q00334_TheWishingPotion.Q00334_TheWishingPotion;
import com.l2jserver.datapack.quests.Q00335_TheSongOfTheHunter.Q00335_TheSongOfTheHunter;
import com.l2jserver.datapack.quests.Q00336_CoinsOfMagic.Q00336_CoinsOfMagic;
import com.l2jserver.datapack.quests.Q00337_AudienceWithTheLandDragon.Q00337_AudienceWithTheLandDragon;
import com.l2jserver.datapack.quests.Q00338_AlligatorHunter.Q00338_AlligatorHunter;
import com.l2jserver.datapack.quests.Q00340_SubjugationOfLizardmen.Q00340_SubjugationOfLizardmen;
import com.l2jserver.datapack.quests.Q00341_HuntingForWildBeasts.Q00341_HuntingForWildBeasts;
import com.l2jserver.datapack.quests.Q00343_UnderTheShadowOfTheIvoryTower.Q00343_UnderTheShadowOfTheIvoryTower;
import com.l2jserver.datapack.quests.Q00344_1000YearsTheEndOfLamentation.Q00344_1000YearsTheEndOfLamentation;
import com.l2jserver.datapack.quests.Q00345_MethodToRaiseTheDead.Q00345_MethodToRaiseTheDead;
import com.l2jserver.datapack.quests.Q00347_GoGetTheCalculator.Q00347_GoGetTheCalculator;
import com.l2jserver.datapack.quests.Q00348_AnArrogantSearch.Q00348_AnArrogantSearch;
import com.l2jserver.datapack.quests.Q00350_EnhanceYourWeapon.Q00350_EnhanceYourWeapon;
import com.l2jserver.datapack.quests.Q00351_BlackSwan.Q00351_BlackSwan;
import com.l2jserver.datapack.quests.Q00352_HelpRoodRaiseANewPet.Q00352_HelpRoodRaiseANewPet;
import com.l2jserver.datapack.quests.Q00354_ConquestOfAlligatorIsland.Q00354_ConquestOfAlligatorIsland;
import com.l2jserver.datapack.quests.Q00355_FamilyHonor.Q00355_FamilyHonor;
import com.l2jserver.datapack.quests.Q00356_DigUpTheSeaOfSpores.Q00356_DigUpTheSeaOfSpores;
import com.l2jserver.datapack.quests.Q00357_WarehouseKeepersAmbition.Q00357_WarehouseKeepersAmbition;
import com.l2jserver.datapack.quests.Q00358_IllegitimateChildOfTheGoddess.Q00358_IllegitimateChildOfTheGoddess;
import com.l2jserver.datapack.quests.Q00359_ForASleeplessDeadman.Q00359_ForASleeplessDeadman;
import com.l2jserver.datapack.quests.Q00360_PlunderTheirSupplies.Q00360_PlunderTheirSupplies;
import com.l2jserver.datapack.quests.Q00362_BardsMandolin.Q00362_BardsMandolin;
import com.l2jserver.datapack.quests.Q00363_SorrowfulSoundOfFlute.Q00363_SorrowfulSoundOfFlute;
import com.l2jserver.datapack.quests.Q00364_JovialAccordion.Q00364_JovialAccordion;
import com.l2jserver.datapack.quests.Q00365_DevilsLegacy.Q00365_DevilsLegacy;
import com.l2jserver.datapack.quests.Q00366_SilverHairedShaman.Q00366_SilverHairedShaman;
import com.l2jserver.datapack.quests.Q00367_ElectrifyingRecharge.Q00367_ElectrifyingRecharge;
import com.l2jserver.datapack.quests.Q00368_TrespassingIntoTheHolyGround.Q00368_TrespassingIntoTheHolyGround;
import com.l2jserver.datapack.quests.Q00369_CollectorOfJewels.Q00369_CollectorOfJewels;
import com.l2jserver.datapack.quests.Q00370_AnElderSowsSeeds.Q00370_AnElderSowsSeeds;
import com.l2jserver.datapack.quests.Q00371_ShrieksOfGhosts.Q00371_ShrieksOfGhosts;
import com.l2jserver.datapack.quests.Q00372_LegacyOfInsolence.Q00372_LegacyOfInsolence;
import com.l2jserver.datapack.quests.Q00373_SupplierOfReagents.Q00373_SupplierOfReagents;
import com.l2jserver.datapack.quests.Q00376_ExplorationOfTheGiantsCavePart1.Q00376_ExplorationOfTheGiantsCavePart1;
import com.l2jserver.datapack.quests.Q00377_ExplorationOfTheGiantsCavePart2.Q00377_ExplorationOfTheGiantsCavePart2;
import com.l2jserver.datapack.quests.Q00378_GrandFeast.Q00378_GrandFeast;
import com.l2jserver.datapack.quests.Q00379_FantasyWine.Q00379_FantasyWine;
import com.l2jserver.datapack.quests.Q00380_BringOutTheFlavorOfIngredients.Q00380_BringOutTheFlavorOfIngredients;
import com.l2jserver.datapack.quests.Q00381_LetsBecomeARoyalMember.Q00381_LetsBecomeARoyalMember;
import com.l2jserver.datapack.quests.Q00382_KailsMagicCoin.Q00382_KailsMagicCoin;
import com.l2jserver.datapack.quests.Q00383_TreasureHunt.Q00383_TreasureHunt;
import com.l2jserver.datapack.quests.Q00384_WarehouseKeepersPastime.Q00384_WarehouseKeepersPastime;
import com.l2jserver.datapack.quests.Q00385_YokeOfThePast.Q00385_YokeOfThePast;
import com.l2jserver.datapack.quests.Q00386_StolenDignity.Q00386_StolenDignity;
import com.l2jserver.datapack.quests.Q00401_PathOfTheWarrior.Q00401_PathOfTheWarrior;
import com.l2jserver.datapack.quests.Q00402_PathOfTheHumanKnight.Q00402_PathOfTheHumanKnight;
import com.l2jserver.datapack.quests.Q00403_PathOfTheRogue.Q00403_PathOfTheRogue;
import com.l2jserver.datapack.quests.Q00404_PathOfTheHumanWizard.Q00404_PathOfTheHumanWizard;
import com.l2jserver.datapack.quests.Q00405_PathOfTheCleric.Q00405_PathOfTheCleric;
import com.l2jserver.datapack.quests.Q00406_PathOfTheElvenKnight.Q00406_PathOfTheElvenKnight;
import com.l2jserver.datapack.quests.Q00407_PathOfTheElvenScout.Q00407_PathOfTheElvenScout;
import com.l2jserver.datapack.quests.Q00408_PathOfTheElvenWizard.Q00408_PathOfTheElvenWizard;
import com.l2jserver.datapack.quests.Q00409_PathOfTheElvenOracle.Q00409_PathOfTheElvenOracle;
import com.l2jserver.datapack.quests.Q00410_PathOfThePalusKnight.Q00410_PathOfThePalusKnight;
import com.l2jserver.datapack.quests.Q00411_PathOfTheAssassin.Q00411_PathOfTheAssassin;
import com.l2jserver.datapack.quests.Q00412_PathOfTheDarkWizard.Q00412_PathOfTheDarkWizard;
import com.l2jserver.datapack.quests.Q00413_PathOfTheShillienOracle.Q00413_PathOfTheShillienOracle;
import com.l2jserver.datapack.quests.Q00414_PathOfTheOrcRaider.Q00414_PathOfTheOrcRaider;
import com.l2jserver.datapack.quests.Q00415_PathOfTheOrcMonk.Q00415_PathOfTheOrcMonk;
import com.l2jserver.datapack.quests.Q00416_PathOfTheOrcShaman.Q00416_PathOfTheOrcShaman;
import com.l2jserver.datapack.quests.Q00417_PathOfTheScavenger.Q00417_PathOfTheScavenger;
import com.l2jserver.datapack.quests.Q00418_PathOfTheArtisan.Q00418_PathOfTheArtisan;
import com.l2jserver.datapack.quests.Q00419_GetAPet.Q00419_GetAPet;
import com.l2jserver.datapack.quests.Q00420_LittleWing.Q00420_LittleWing;
import com.l2jserver.datapack.quests.Q00421_LittleWingsBigAdventure.Q00421_LittleWingsBigAdventure;
import com.l2jserver.datapack.quests.Q00422_RepentYourSins.Q00422_RepentYourSins;
import com.l2jserver.datapack.quests.Q00423_TakeYourBestShot.Q00423_TakeYourBestShot;
import com.l2jserver.datapack.quests.Q00426_QuestForFishingShot.Q00426_QuestForFishingShot;
import com.l2jserver.datapack.quests.Q00431_WeddingMarch.Q00431_WeddingMarch;
import com.l2jserver.datapack.quests.Q00432_BirthdayPartySong.Q00432_BirthdayPartySong;
import com.l2jserver.datapack.quests.Q00450_GraveRobberRescue.Q00450_GraveRobberRescue;
import com.l2jserver.datapack.quests.Q00451_LuciensAltar.Q00451_LuciensAltar;
import com.l2jserver.datapack.quests.Q00452_FindingtheLostSoldiers.Q00452_FindingtheLostSoldiers;
import com.l2jserver.datapack.quests.Q00453_NotStrongEnoughAlone.Q00453_NotStrongEnoughAlone;
import com.l2jserver.datapack.quests.Q00454_CompletelyLost.Q00454_CompletelyLost;
import com.l2jserver.datapack.quests.Q00455_WingsOfSand.Q00455_WingsOfSand;
import com.l2jserver.datapack.quests.Q00456_DontKnowDontCare.Q00456_DontKnowDontCare;
import com.l2jserver.datapack.quests.Q00457_LostAndFound.Q00457_LostAndFound;
import com.l2jserver.datapack.quests.Q00458_PerfectForm.Q00458_PerfectForm;
import com.l2jserver.datapack.quests.Q00461_RumbleInTheBase.Q00461_RumbleInTheBase;
import com.l2jserver.datapack.quests.Q00463_IMustBeaGenius.Q00463_IMustBeaGenius;
import com.l2jserver.datapack.quests.Q00464_Oath.Q00464_Oath;
import com.l2jserver.datapack.quests.Q00501_ProofOfClanAlliance.Q00501_ProofOfClanAlliance;
import com.l2jserver.datapack.quests.Q00503_PursuitOfClanAmbition.Q00503_PursuitOfClanAmbition;
import com.l2jserver.datapack.quests.Q00504_CompetitionForTheBanditStronghold.Q00504_CompetitionForTheBanditStronghold;
import com.l2jserver.datapack.quests.Q00508_AClansReputation.Q00508_AClansReputation;
import com.l2jserver.datapack.quests.Q00509_AClansFame.Q00509_AClansFame;
import com.l2jserver.datapack.quests.Q00510_AClansPrestige.Q00510_AClansPrestige;
import com.l2jserver.datapack.quests.Q00511_AwlUnderFoot.Q00511_AwlUnderFoot;
import com.l2jserver.datapack.quests.Q00512_BladeUnderFoot.Q00512_BladeUnderFoot;
import com.l2jserver.datapack.quests.Q00551_OlympiadStarter.Q00551_OlympiadStarter;
import com.l2jserver.datapack.quests.Q00552_OlympiadVeteran.Q00552_OlympiadVeteran;
import com.l2jserver.datapack.quests.Q00553_OlympiadUndefeated.Q00553_OlympiadUndefeated;
import com.l2jserver.datapack.quests.Q00601_WatchingEyes.Q00601_WatchingEyes;
import com.l2jserver.datapack.quests.Q00602_ShadowOfLight.Q00602_ShadowOfLight;
import com.l2jserver.datapack.quests.Q00603_DaimonTheWhiteEyedPart1.Q00603_DaimonTheWhiteEyedPart1;
import com.l2jserver.datapack.quests.Q00604_DaimonTheWhiteEyedPart2.Q00604_DaimonTheWhiteEyedPart2;
import com.l2jserver.datapack.quests.Q00605_AllianceWithKetraOrcs.Q00605_AllianceWithKetraOrcs;
import com.l2jserver.datapack.quests.Q00606_BattleAgainstVarkaSilenos.Q00606_BattleAgainstVarkaSilenos;
import com.l2jserver.datapack.quests.Q00607_ProveYourCourageKetra.Q00607_ProveYourCourageKetra;
import com.l2jserver.datapack.quests.Q00608_SlayTheEnemyCommanderKetra.Q00608_SlayTheEnemyCommanderKetra;
import com.l2jserver.datapack.quests.Q00609_MagicalPowerOfWaterPart1.Q00609_MagicalPowerOfWaterPart1;
import com.l2jserver.datapack.quests.Q00610_MagicalPowerOfWaterPart2.Q00610_MagicalPowerOfWaterPart2;
import com.l2jserver.datapack.quests.Q00611_AllianceWithVarkaSilenos.Q00611_AllianceWithVarkaSilenos;
import com.l2jserver.datapack.quests.Q00612_BattleAgainstKetraOrcs.Q00612_BattleAgainstKetraOrcs;
import com.l2jserver.datapack.quests.Q00613_ProveYourCourageVarka.Q00613_ProveYourCourageVarka;
import com.l2jserver.datapack.quests.Q00614_SlayTheEnemyCommanderVarka.Q00614_SlayTheEnemyCommanderVarka;
import com.l2jserver.datapack.quests.Q00615_MagicalPowerOfFirePart1.Q00615_MagicalPowerOfFirePart1;
import com.l2jserver.datapack.quests.Q00616_MagicalPowerOfFirePart2.Q00616_MagicalPowerOfFirePart2;
import com.l2jserver.datapack.quests.Q00617_GatherTheFlames.Q00617_GatherTheFlames;
import com.l2jserver.datapack.quests.Q00618_IntoTheFlame.Q00618_IntoTheFlame;
import com.l2jserver.datapack.quests.Q00619_RelicsOfTheOldEmpire.Q00619_RelicsOfTheOldEmpire;
import com.l2jserver.datapack.quests.Q00620_FourGoblets.Q00620_FourGoblets;
import com.l2jserver.datapack.quests.Q00621_EggDelivery.Q00621_EggDelivery;
import com.l2jserver.datapack.quests.Q00622_SpecialtyLiquorDelivery.Q00622_SpecialtyLiquorDelivery;
import com.l2jserver.datapack.quests.Q00623_TheFinestFood.Q00623_TheFinestFood;
import com.l2jserver.datapack.quests.Q00624_TheFinestIngredientsPart1.Q00624_TheFinestIngredientsPart1;
import com.l2jserver.datapack.quests.Q00625_TheFinestIngredientsPart2.Q00625_TheFinestIngredientsPart2;
import com.l2jserver.datapack.quests.Q00626_ADarkTwilight.Q00626_ADarkTwilight;
import com.l2jserver.datapack.quests.Q00627_HeartInSearchOfPower.Q00627_HeartInSearchOfPower;
import com.l2jserver.datapack.quests.Q00628_HuntGoldenRam.Q00628_HuntGoldenRam;
import com.l2jserver.datapack.quests.Q00629_CleanUpTheSwampOfScreams.Q00629_CleanUpTheSwampOfScreams;
import com.l2jserver.datapack.quests.Q00631_DeliciousTopChoiceMeat.Q00631_DeliciousTopChoiceMeat;
import com.l2jserver.datapack.quests.Q00632_NecromancersRequest.Q00632_NecromancersRequest;
import com.l2jserver.datapack.quests.Q00633_InTheForgottenVillage.Q00633_InTheForgottenVillage;
import com.l2jserver.datapack.quests.Q00634_InSearchOfFragmentsOfDimension.Q00634_InSearchOfFragmentsOfDimension;
import com.l2jserver.datapack.quests.Q00635_IntoTheDimensionalRift.Q00635_IntoTheDimensionalRift;
import com.l2jserver.datapack.quests.Q00636_TruthBeyond.Q00636_TruthBeyond;
import com.l2jserver.datapack.quests.Q00637_ThroughOnceMore.Q00637_ThroughOnceMore;
import com.l2jserver.datapack.quests.Q00638_SeekersOfTheHolyGrail.Q00638_SeekersOfTheHolyGrail;
import com.l2jserver.datapack.quests.Q00639_GuardiansOfTheHolyGrail.Q00639_GuardiansOfTheHolyGrail;
import com.l2jserver.datapack.quests.Q00641_AttackSailren.Q00641_AttackSailren;
import com.l2jserver.datapack.quests.Q00642_APowerfulPrimevalCreature.Q00642_APowerfulPrimevalCreature;
import com.l2jserver.datapack.quests.Q00643_RiseAndFallOfTheElrokiTribe.Q00643_RiseAndFallOfTheElrokiTribe;
import com.l2jserver.datapack.quests.Q00644_GraveRobberAnnihilation.Q00644_GraveRobberAnnihilation;
import com.l2jserver.datapack.quests.Q00645_GhostsOfBatur.Q00645_GhostsOfBatur;
import com.l2jserver.datapack.quests.Q00646_SignsOfRevolt.Q00646_SignsOfRevolt;
import com.l2jserver.datapack.quests.Q00647_InfluxOfMachines.Q00647_InfluxOfMachines;
import com.l2jserver.datapack.quests.Q00648_AnIceMerchantsDream.Q00648_AnIceMerchantsDream;
import com.l2jserver.datapack.quests.Q00649_ALooterAndARailroadMan.Q00649_ALooterAndARailroadMan;
import com.l2jserver.datapack.quests.Q00650_ABrokenDream.Q00650_ABrokenDream;
import com.l2jserver.datapack.quests.Q00651_RunawayYouth.Q00651_RunawayYouth;
import com.l2jserver.datapack.quests.Q00652_AnAgedExAdventurer.Q00652_AnAgedExAdventurer;
import com.l2jserver.datapack.quests.Q00653_WildMaiden.Q00653_WildMaiden;
import com.l2jserver.datapack.quests.Q00654_JourneyToASettlement.Q00654_JourneyToASettlement;
import com.l2jserver.datapack.quests.Q00655_AGrandPlanForTamingWildBeasts.Q00655_AGrandPlanForTamingWildBeasts;
import com.l2jserver.datapack.quests.Q00659_IdRatherBeCollectingFairyBreath.Q00659_IdRatherBeCollectingFairyBreath;
import com.l2jserver.datapack.quests.Q00660_AidingTheFloranVillage.Q00660_AidingTheFloranVillage;
import com.l2jserver.datapack.quests.Q00661_MakingTheHarvestGroundsSafe.Q00661_MakingTheHarvestGroundsSafe;
import com.l2jserver.datapack.quests.Q00662_AGameOfCards.Q00662_AGameOfCards;
import com.l2jserver.datapack.quests.Q00663_SeductiveWhispers.Q00663_SeductiveWhispers;
import com.l2jserver.datapack.quests.Q00688_DefeatTheElrokianRaiders.Q00688_DefeatTheElrokianRaiders;
import com.l2jserver.datapack.quests.Q00690_JudesRequest.Q00690_JudesRequest;
import com.l2jserver.datapack.quests.Q00691_MatrasSuspiciousRequest.Q00691_MatrasSuspiciousRequest;
import com.l2jserver.datapack.quests.Q00692_HowtoOpposeEvil.Q00692_HowtoOpposeEvil;
import com.l2jserver.datapack.quests.Q00695_DefendTheHallOfSuffering.Q00695_DefendTheHallOfSuffering;
import com.l2jserver.datapack.quests.Q00699_GuardianOfTheSkies.Q00699_GuardianOfTheSkies;
import com.l2jserver.datapack.quests.Q00700_CursedLife.Q00700_CursedLife;
import com.l2jserver.datapack.quests.Q00701_ProofOfExistence.Q00701_ProofOfExistence;
import com.l2jserver.datapack.quests.Q00702_ATrapForRevenge.Q00702_ATrapForRevenge;
import com.l2jserver.datapack.quests.Q00708_PathToBecomingALordGludio.Q00708_PathToBecomingALordGludio;
import com.l2jserver.datapack.quests.Q00901_HowLavasaurusesAreMade.Q00901_HowLavasaurusesAreMade;
import com.l2jserver.datapack.quests.Q00902_ReclaimOurEra.Q00902_ReclaimOurEra;
import com.l2jserver.datapack.quests.Q00903_TheCallOfAntharas.Q00903_TheCallOfAntharas;
import com.l2jserver.datapack.quests.Q00904_DragonTrophyAntharas.Q00904_DragonTrophyAntharas;
import com.l2jserver.datapack.quests.Q00905_RefinedDragonBlood.Q00905_RefinedDragonBlood;
import com.l2jserver.datapack.quests.Q00906_TheCallOfValakas.Q00906_TheCallOfValakas;
import com.l2jserver.datapack.quests.Q00907_DragonTrophyValakas.Q00907_DragonTrophyValakas;
import com.l2jserver.datapack.quests.Q00998_FallenAngelSelect.Q00998_FallenAngelSelect;
import com.l2jserver.datapack.quests.Q10267_JourneyToGracia.Q10267_JourneyToGracia;
import com.l2jserver.datapack.quests.Q10268_ToTheSeedOfInfinity.Q10268_ToTheSeedOfInfinity;
import com.l2jserver.datapack.quests.Q10269_ToTheSeedOfDestruction.Q10269_ToTheSeedOfDestruction;
import com.l2jserver.datapack.quests.Q10270_BirthOfTheSeed.Q10270_BirthOfTheSeed;
import com.l2jserver.datapack.quests.Q10271_TheEnvelopingDarkness.Q10271_TheEnvelopingDarkness;
import com.l2jserver.datapack.quests.Q10272_LightFragment.Q10272_LightFragment;
import com.l2jserver.datapack.quests.Q10273_GoodDayToFly.Q10273_GoodDayToFly;
import com.l2jserver.datapack.quests.Q10274_CollectingInTheAir.Q10274_CollectingInTheAir;
import com.l2jserver.datapack.quests.Q10275_ContainingTheAttributePower.Q10275_ContainingTheAttributePower;
import com.l2jserver.datapack.quests.Q10276_MutatedKaneusGludio.Q10276_MutatedKaneusGludio;
import com.l2jserver.datapack.quests.Q10277_MutatedKaneusDion.Q10277_MutatedKaneusDion;
import com.l2jserver.datapack.quests.Q10278_MutatedKaneusHeine.Q10278_MutatedKaneusHeine;
import com.l2jserver.datapack.quests.Q10279_MutatedKaneusOren.Q10279_MutatedKaneusOren;
import com.l2jserver.datapack.quests.Q10280_MutatedKaneusSchuttgart.Q10280_MutatedKaneusSchuttgart;
import com.l2jserver.datapack.quests.Q10281_MutatedKaneusRune.Q10281_MutatedKaneusRune;
import com.l2jserver.datapack.quests.Q10282_ToTheSeedOfAnnihilation.Q10282_ToTheSeedOfAnnihilation;
import com.l2jserver.datapack.quests.Q10283_RequestOfIceMerchant.Q10283_RequestOfIceMerchant;
import com.l2jserver.datapack.quests.Q10284_AcquisitionOfDivineSword.Q10284_AcquisitionOfDivineSword;
import com.l2jserver.datapack.quests.Q10285_MeetingSirra.Q10285_MeetingSirra;
import com.l2jserver.datapack.quests.Q10286_ReunionWithSirra.Q10286_ReunionWithSirra;
import com.l2jserver.datapack.quests.Q10287_StoryOfThoseLeft.Q10287_StoryOfThoseLeft;
import com.l2jserver.datapack.quests.Q10288_SecretMission.Q10288_SecretMission;
import com.l2jserver.datapack.quests.Q10289_FadeToBlack.Q10289_FadeToBlack;
import com.l2jserver.datapack.quests.Q10290_LandDragonConqueror.Q10290_LandDragonConqueror;
import com.l2jserver.datapack.quests.Q10291_FireDragonDestroyer.Q10291_FireDragonDestroyer;
import com.l2jserver.datapack.quests.Q10292_SevenSignsGirlOfDoubt.Q10292_SevenSignsGirlOfDoubt;
import com.l2jserver.datapack.quests.Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom.Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom;
import com.l2jserver.datapack.quests.Q10294_SevenSignsToTheMonasteryOfSilence.Q10294_SevenSignsToTheMonasteryOfSilence;
import com.l2jserver.datapack.quests.Q10295_SevenSignsSolinasTomb.Q10295_SevenSignsSolinasTomb;
import com.l2jserver.datapack.quests.Q10296_SevenSignsOneWhoSeeksThePowerOfTheSeal.Q10296_SevenSignsOneWhoSeeksThePowerOfTheSeal;
import com.l2jserver.datapack.quests.Q10501_ZakenEmbroideredSoulCloak.Q10501_ZakenEmbroideredSoulCloak;
import com.l2jserver.datapack.quests.Q10502_FreyaEmbroideredSoulCloak.Q10502_FreyaEmbroideredSoulCloak;
import com.l2jserver.datapack.quests.Q10503_FrintezzaEmbroideredSoulCloak.Q10503_FrintezzaEmbroideredSoulCloak;
import com.l2jserver.datapack.quests.Q10504_JewelOfAntharas.Q10504_JewelOfAntharas;
import com.l2jserver.datapack.quests.Q10505_JewelOfValakas.Q10505_JewelOfValakas;

/**
 * Quest loader.
 * @author NosBit
 * @author Zoey76
 */
public class QuestLoader {
	private static final Logger LOG = LoggerFactory.getLogger(QuestLoader.class);
	
	private static final Class<?>[] QUESTS = {
		Q00001_LettersOfLove.class,
		Q00002_WhatWomenWant.class,
		Q00003_WillTheSealBeBroken.class,
		Q00004_LongLiveThePaagrioLord.class,
		Q00005_MinersFavor.class,
		Q00006_StepIntoTheFuture.class,
		Q00007_ATripBegins.class,
		Q00008_AnAdventureBegins.class,
		Q00009_IntoTheCityOfHumans.class,
		Q00010_IntoTheWorld.class,
		Q00011_SecretMeetingWithKetraOrcs.class,
		Q00012_SecretMeetingWithVarkaSilenos.class,
		Q00013_ParcelDelivery.class,
		Q00014_WhereaboutsOfTheArchaeologist.class,
		Q00015_SweetWhispers.class,
		Q00016_TheComingDarkness.class,
		Q00017_LightAndDarkness.class,
		Q00018_MeetingWithTheGoldenRam.class,
		Q00019_GoToThePastureland.class,
		Q00020_BringUpWithLove.class,
		Q00021_HiddenTruth.class,
		Q00022_TragedyInVonHellmannForest.class,
		Q00023_LidiasHeart.class,
		Q00024_InhabitantsOfTheForestOfTheDead.class,
		Q00025_HidingBehindTheTruth.class,
		Q00026_TiredOfWaiting.class,
		Q00027_ChestCaughtWithABaitOfWind.class,
		Q00028_ChestCaughtWithABaitOfIcyAir.class,
		Q00029_ChestCaughtWithABaitOfEarth.class,
		Q00030_ChestCaughtWithABaitOfFire.class,
		Q00031_SecretBuriedInTheSwamp.class,
		Q00032_AnObviousLie.class,
		Q00033_MakeAPairOfDressShoes.class,
		Q00034_InSearchOfCloth.class,
		Q00035_FindGlitteringJewelry.class,
		Q00036_MakeASewingKit.class,
		Q00037_MakeFormalWear.class,
		Q00038_DragonFangs.class,
		Q00039_RedEyedInvaders.class,
		Q00040_ASpecialOrder.class,
		Q00042_HelpTheUncle.class,
		Q00043_HelpTheSister.class,
		Q00044_HelpTheSon.class,
		Q00045_ToTalkingIsland.class,
		Q00046_OnceMoreInTheArmsOfTheMotherTree.class,
		Q00047_IntoTheDarkElvenForest.class,
		Q00048_ToTheImmortalPlateau.class,
		Q00049_TheRoadHome.class,
		Q00050_LanoscosSpecialBait.class,
		Q00051_OFullesSpecialBait.class,
		Q00052_WilliesSpecialBait.class,
		Q00053_LinnaeusSpecialBait.class,
		Q00060_GoodWorksReward.class,
		Q00061_LawEnforcement.class,
		Q00062_PathOfTheTrooper.class,
		Q00063_PathOfTheWarder.class,
		Q00064_CertifiedBerserker.class,
		Q00065_CertifiedSoulBreaker.class,
		Q00066_CertifiedArbalester.class,
		Q00067_SagaOfTheDoombringer.class,
		Q00068_SagaOfTheSoulHound.class,
		Q00069_SagaOfTheTrickster.class,
		Q00070_SagaOfThePhoenixKnight.class,
		Q00071_SagaOfEvasTemplar.class,
		Q00072_SagaOfTheSwordMuse.class,
		Q00073_SagaOfTheDuelist.class,
		Q00074_SagaOfTheDreadnought.class,
		Q00075_SagaOfTheTitan.class,
		Q00076_SagaOfTheGrandKhavatari.class,
		Q00077_SagaOfTheDominator.class,
		Q00078_SagaOfTheDoomcryer.class,
		Q00079_SagaOfTheAdventurer.class,
		Q00080_SagaOfTheWindRider.class,
		Q00081_SagaOfTheGhostHunter.class,
		Q00082_SagaOfTheSagittarius.class,
		Q00083_SagaOfTheMoonlightSentinel.class,
		Q00084_SagaOfTheGhostSentinel.class,
		Q00085_SagaOfTheCardinal.class,
		Q00086_SagaOfTheHierophant.class,
		Q00087_SagaOfEvasSaint.class,
		Q00088_SagaOfTheArchmage.class,
		Q00089_SagaOfTheMysticMuse.class,
		Q00090_SagaOfTheStormScreamer.class,
		Q00091_SagaOfTheArcanaLord.class,
		Q00092_SagaOfTheElementalMaster.class,
		Q00093_SagaOfTheSpectralMaster.class,
		Q00094_SagaOfTheSoultaker.class,
		Q00095_SagaOfTheHellKnight.class,
		Q00096_SagaOfTheSpectralDancer.class,
		Q00097_SagaOfTheShillienTemplar.class,
		Q00098_SagaOfTheShillienSaint.class,
		Q00099_SagaOfTheFortuneSeeker.class,
		Q00100_SagaOfTheMaestro.class,
		Q00101_SwordOfSolidarity.class,
		Q00102_SeaOfSporesFever.class,
		Q00103_SpiritOfCraftsman.class,
		Q00104_SpiritOfMirrors.class,
		Q00105_SkirmishWithOrcs.class,
		Q00106_ForgottenTruth.class,
		Q00107_MercilessPunishment.class,
		Q00108_JumbleTumbleDiamondFuss.class,
		Q00109_InSearchOfTheNest.class,
		Q00110_ToThePrimevalIsle.class,
		Q00111_ElrokianHuntersProof.class,
		Q00112_WalkOfFate.class,
		Q00113_StatusOfTheBeaconTower.class,
		Q00114_ResurrectionOfAnOldManager.class,
		Q00115_TheOtherSideOfTruth.class,
		Q00116_BeyondTheHillsOfWinter.class,
		Q00117_TheOceanOfDistantStars.class,
		Q00118_ToLeadAndBeLed.class,
		Q00119_LastImperialPrince.class,
		Q00120_PavelsLastResearch.class,
		Q00121_PavelTheGiant.class,
		Q00122_OminousNews.class,
		Q00123_TheLeaderAndTheFollower.class,
		Q00124_MeetingTheElroki.class,
		Q00125_TheNameOfEvil1.class,
		Q00126_TheNameOfEvil2.class,
		Q00128_PailakaSongOfIceAndFire.class,
		Q00129_PailakaDevilsLegacy.class,
		Q00130_PathToHellbound.class,
		Q00131_BirdInACage.class,
		Q00132_MatrasCuriosity.class,
		Q00133_ThatsBloodyHot.class,
		Q00134_TempleMissionary.class,
		Q00135_TempleExecutor.class,
		Q00136_MoreThanMeetsTheEye.class,
		Q00137_TempleChampionPart1.class,
		Q00138_TempleChampionPart2.class,
		Q00139_ShadowFoxPart1.class,
		Q00140_ShadowFoxPart2.class,
		Q00141_ShadowFoxPart3.class,
		Q00142_FallenAngelRequestOfDawn.class,
		Q00143_FallenAngelRequestOfDusk.class,
		Q00146_TheZeroHour.class,
		Q00147_PathtoBecominganEliteMercenary.class,
		Q00148_PathtoBecominganExaltedMercenary.class,
		Q00151_CureForFever.class,
		Q00152_ShardsOfGolem.class,
		Q00153_DeliverGoods.class,
		Q00154_SacrificeToTheSea.class,
		Q00155_FindSirWindawood.class,
		Q00156_MillenniumLove.class,
		Q00157_RecoverSmuggledGoods.class,
		Q00158_SeedOfEvil.class,
		Q00159_ProtectTheWaterSource.class,
		Q00160_NerupasRequest.class,
		Q00161_FruitOfTheMotherTree.class,
		Q00162_CurseOfTheUndergroundFortress.class,
		Q00163_LegacyOfThePoet.class,
		Q00164_BloodFiend.class,
		Q00165_ShilensHunt.class,
		Q00166_MassOfDarkness.class,
		Q00167_DwarvenKinship.class,
		Q00168_DeliverSupplies.class,
		Q00169_OffspringOfNightmares.class,
		Q00170_DangerousSeduction.class,
		Q00171_ActsOfEvil.class,
		Q00172_NewHorizons.class,
		Q00173_ToTheIsleOfSouls.class,
		Q00174_SupplyCheck.class,
		Q00175_TheWayOfTheWarrior.class,
		Q00176_StepsForHonor.class,
		Q00178_IconicTrinity.class,
		Q00179_IntoTheLargeCavern.class,
		Q00182_NewRecruits.class,
		Q00183_RelicExploration.class,
		Q00184_ArtOfPersuasion.class,
		Q00185_NikolasCooperation.class,
		Q00186_ContractExecution.class,
		Q00187_NikolasHeart.class,
		Q00188_SealRemoval.class,
		Q00189_ContractCompletion.class,
		Q00190_LostDream.class,
		Q00191_VainConclusion.class,
		Q00192_SevenSignsSeriesOfDoubt.class,
		Q00193_SevenSignsDyingMessage.class,
		Q00194_SevenSignsMammonsContract.class,
		Q00195_SevenSignsSecretRitualOfThePriests.class,
		Q00196_SevenSignsSealOfTheEmperor.class,
		Q00197_SevenSignsTheSacredBookOfSeal.class,
		Q00198_SevenSignsEmbryo.class,
		Q00201_HumanFighterTutorial.class,
		Q00202_HumanMageTutorial.class,
		Q00203_ElfTutorial.class,
		Q00204_DarkElfTutorial.class,
		Q00205_OrcTutorial.class,
		Q00206_DwarfTutorial.class,
		Q00207_NewbieWeaponCoupon.class,
		Q00208_NewbieAccessoryCoupon.class,
		Q00209_KamaelTutorial.class,
		Q00211_TrialOfTheChallenger.class,
		Q00212_TrialOfDuty.class,
		Q00213_TrialOfTheSeeker.class,
		Q00214_TrialOfTheScholar.class,
		Q00215_TrialOfThePilgrim.class,
		Q00216_TrialOfTheGuildsman.class,
		Q00217_TestimonyOfTrust.class,
		Q00218_TestimonyOfLife.class,
		Q00219_TestimonyOfFate.class,
		Q00220_TestimonyOfGlory.class,
		Q00221_TestimonyOfProsperity.class,
		Q00222_TestOfTheDuelist.class,
		Q00223_TestOfTheChampion.class,
		Q00224_TestOfSagittarius.class,
		Q00225_TestOfTheSearcher.class,
		Q00226_TestOfTheHealer.class,
		Q00227_TestOfTheReformer.class,
		Q00228_TestOfMagus.class,
		Q00229_TestOfWitchcraft.class,
		Q00230_TestOfTheSummoner.class,
		Q00231_TestOfTheMaestro.class,
		Q00232_TestOfTheLord.class,
		Q00233_TestOfTheWarSpirit.class,
		Q00234_FatesWhisper.class,
		Q00235_MimirsElixir.class,
		Q00236_SeedsOfChaos.class,
		Q00237_WindsOfChange.class,
		Q00238_SuccessFailureOfBusiness.class,
		Q00239_WontYouJoinUs.class,
		Q00240_ImTheOnlyOneYouCanTrust.class,
		Q00241_PossessorOfAPreciousSoul1.class,
		Q00242_PossessorOfAPreciousSoul2.class,
		Q00246_PossessorOfAPreciousSoul3.class,
		Q00247_PossessorOfAPreciousSoul4.class,
		Q00249_PoisonedPlainsOfTheLizardmen.class,
		Q00250_WatchWhatYouEat.class,
		Q00251_NoSecrets.class,
		Q00252_ItSmellsDelicious.class,
		Q00254_LegendaryTales.class,
		Q00255_Tutorial.class,
		Q00257_TheGuardIsBusy.class,
		Q00258_BringWolfPelts.class,
		Q00259_RequestFromTheFarmOwner.class,
		Q00260_OrcHunting.class,
		Q00261_CollectorsDream.class,
		Q00262_TradeWithTheIvoryTower.class,
		Q00263_OrcSubjugation.class,
		Q00264_KeenClaws.class,
		Q00265_BondsOfSlavery.class,
		Q00266_PleasOfPixies.class,
		Q00267_WrathOfVerdure.class,
		Q00268_TracesOfEvil.class,
		Q00269_InventionAmbition.class,
		Q00270_TheOneWhoEndsSilence.class,
		Q00271_ProofOfValor.class,
		Q00272_WrathOfAncestors.class,
		Q00273_InvadersOfTheHolyLand.class,
		Q00274_SkirmishWithTheWerewolves.class,
		Q00275_DarkWingedSpies.class,
		Q00276_TotemOfTheHestui.class,
		Q00277_GatekeepersOffering.class,
		Q00278_HomeSecurity.class,
		Q00279_TargetOfOpportunity.class,
		Q00280_TheFoodChain.class,
		Q00281_HeadForTheHills.class,
		Q00283_TheFewTheProudTheBrave.class,
		Q00284_MuertosFeather.class,
		Q00286_FabulousFeathers.class,
		Q00287_FiguringItOut.class,
		Q00288_HandleWithCare.class,
		Q00289_NoMoreSoupForYou.class,
		Q00290_ThreatRemoval.class,
		Q00291_RevengeOfTheRedbonnet.class,
		Q00292_BrigandsSweep.class,
		Q00293_TheHiddenVeins.class,
		Q00294_CovertBusiness.class,
		Q00295_DreamingOfTheSkies.class,
		Q00296_TarantulasSpiderSilk.class,
		Q00297_GatekeepersFavor.class,
		Q00298_LizardmensConspiracy.class,
		Q00299_GatherIngredientsForPie.class,
		Q00300_HuntingLetoLizardman.class,
		Q00303_CollectArrowheads.class,
		Q00306_CrystalOfFireAndIce.class,
		Q00307_ControlDeviceOfTheGiants.class,
		Q00308_ReedFieldMaintenance.class,
		Q00309_ForAGoodCause.class,
		Q00310_OnlyWhatRemains.class,
		Q00311_ExpulsionOfEvilSpirits.class,
		Q00312_TakeAdvantageOfTheCrisis.class,
		Q00313_CollectSpores.class,
		Q00316_DestroyPlagueCarriers.class,
		Q00317_CatchTheWind.class,
		Q00319_ScentOfDeath.class,
		Q00320_BonesTellTheFuture.class,
		Q00324_SweetestVenom.class,
		Q00325_GrimCollector.class,
		Q00326_VanquishRemnants.class,
		Q00327_RecoverTheFarmland.class,
		Q00328_SenseForBusiness.class,
		Q00329_CuriosityOfADwarf.class,
		Q00330_AdeptOfTaste.class,
		Q00331_ArrowOfVengeance.class,
		Q00333_HuntOfTheBlackLion.class,
		Q00334_TheWishingPotion.class,
		Q00335_TheSongOfTheHunter.class,
		Q00336_CoinsOfMagic.class,
		Q00337_AudienceWithTheLandDragon.class,
		Q00338_AlligatorHunter.class,
		Q00340_SubjugationOfLizardmen.class,
		Q00341_HuntingForWildBeasts.class,
		Q00343_UnderTheShadowOfTheIvoryTower.class,
		Q00344_1000YearsTheEndOfLamentation.class,
		Q00345_MethodToRaiseTheDead.class,
		Q00347_GoGetTheCalculator.class,
		Q00348_AnArrogantSearch.class,
		Q00350_EnhanceYourWeapon.class,
		Q00351_BlackSwan.class,
		Q00352_HelpRoodRaiseANewPet.class,
		Q00354_ConquestOfAlligatorIsland.class,
		Q00355_FamilyHonor.class,
		Q00356_DigUpTheSeaOfSpores.class,
		Q00357_WarehouseKeepersAmbition.class,
		Q00358_IllegitimateChildOfTheGoddess.class,
		Q00359_ForASleeplessDeadman.class,
		Q00360_PlunderTheirSupplies.class,
		Q00362_BardsMandolin.class,
		Q00363_SorrowfulSoundOfFlute.class,
		Q00364_JovialAccordion.class,
		Q00365_DevilsLegacy.class,
		Q00366_SilverHairedShaman.class,
		Q00367_ElectrifyingRecharge.class,
		Q00368_TrespassingIntoTheHolyGround.class,
		Q00369_CollectorOfJewels.class,
		Q00370_AnElderSowsSeeds.class,
		Q00371_ShrieksOfGhosts.class,
		Q00372_LegacyOfInsolence.class,
		Q00373_SupplierOfReagents.class,
		Q00376_ExplorationOfTheGiantsCavePart1.class,
		Q00377_ExplorationOfTheGiantsCavePart2.class,
		Q00378_GrandFeast.class,
		Q00379_FantasyWine.class,
		Q00380_BringOutTheFlavorOfIngredients.class,
		Q00381_LetsBecomeARoyalMember.class,
		Q00382_KailsMagicCoin.class,
		Q00383_TreasureHunt.class,
		Q00384_WarehouseKeepersPastime.class,
		Q00385_YokeOfThePast.class,
		Q00386_StolenDignity.class,
		Q00401_PathOfTheWarrior.class,
		Q00402_PathOfTheHumanKnight.class,
		Q00403_PathOfTheRogue.class,
		Q00404_PathOfTheHumanWizard.class,
		Q00405_PathOfTheCleric.class,
		Q00406_PathOfTheElvenKnight.class,
		Q00407_PathOfTheElvenScout.class,
		Q00408_PathOfTheElvenWizard.class,
		Q00409_PathOfTheElvenOracle.class,
		Q00410_PathOfThePalusKnight.class,
		Q00411_PathOfTheAssassin.class,
		Q00412_PathOfTheDarkWizard.class,
		Q00413_PathOfTheShillienOracle.class,
		Q00414_PathOfTheOrcRaider.class,
		Q00415_PathOfTheOrcMonk.class,
		Q00416_PathOfTheOrcShaman.class,
		Q00417_PathOfTheScavenger.class,
		Q00418_PathOfTheArtisan.class,
		Q00419_GetAPet.class,
		Q00420_LittleWing.class,
		Q00421_LittleWingsBigAdventure.class,
		Q00422_RepentYourSins.class,
		Q00423_TakeYourBestShot.class,
		Q00426_QuestForFishingShot.class,
		Q00431_WeddingMarch.class,
		Q00432_BirthdayPartySong.class,
		Q00450_GraveRobberRescue.class,
		Q00451_LuciensAltar.class,
		Q00452_FindingtheLostSoldiers.class,
		Q00453_NotStrongEnoughAlone.class,
		Q00454_CompletelyLost.class,
		Q00455_WingsOfSand.class,
		Q00456_DontKnowDontCare.class,
		Q00457_LostAndFound.class,
		Q00458_PerfectForm.class,
		Q00461_RumbleInTheBase.class,
		Q00463_IMustBeaGenius.class,
		Q00464_Oath.class,
		Q00501_ProofOfClanAlliance.class,
		Q00503_PursuitOfClanAmbition.class,
		Q00504_CompetitionForTheBanditStronghold.class,
		Q00508_AClansReputation.class,
		Q00509_AClansFame.class,
		Q00510_AClansPrestige.class,
		Q00511_AwlUnderFoot.class,
		Q00512_BladeUnderFoot.class,
		Q00551_OlympiadStarter.class,
		Q00552_OlympiadVeteran.class,
		Q00553_OlympiadUndefeated.class,
		Q00601_WatchingEyes.class,
		Q00602_ShadowOfLight.class,
		Q00603_DaimonTheWhiteEyedPart1.class,
		Q00604_DaimonTheWhiteEyedPart2.class,
		Q00605_AllianceWithKetraOrcs.class,
		Q00606_BattleAgainstVarkaSilenos.class,
		Q00607_ProveYourCourageKetra.class,
		Q00608_SlayTheEnemyCommanderKetra.class,
		Q00609_MagicalPowerOfWaterPart1.class,
		Q00610_MagicalPowerOfWaterPart2.class,
		Q00611_AllianceWithVarkaSilenos.class,
		Q00612_BattleAgainstKetraOrcs.class,
		Q00613_ProveYourCourageVarka.class,
		Q00614_SlayTheEnemyCommanderVarka.class,
		Q00615_MagicalPowerOfFirePart1.class,
		Q00616_MagicalPowerOfFirePart2.class,
		Q00617_GatherTheFlames.class,
		Q00618_IntoTheFlame.class,
		Q00619_RelicsOfTheOldEmpire.class,
		Q00620_FourGoblets.class,
		Q00621_EggDelivery.class,
		Q00622_SpecialtyLiquorDelivery.class,
		Q00623_TheFinestFood.class,
		Q00624_TheFinestIngredientsPart1.class,
		Q00625_TheFinestIngredientsPart2.class,
		Q00626_ADarkTwilight.class,
		Q00627_HeartInSearchOfPower.class,
		Q00628_HuntGoldenRam.class,
		Q00629_CleanUpTheSwampOfScreams.class,
		Q00631_DeliciousTopChoiceMeat.class,
		Q00632_NecromancersRequest.class,
		Q00633_InTheForgottenVillage.class,
		Q00634_InSearchOfFragmentsOfDimension.class,
		Q00635_IntoTheDimensionalRift.class,
		Q00636_TruthBeyond.class,
		Q00637_ThroughOnceMore.class,
		Q00638_SeekersOfTheHolyGrail.class,
		Q00639_GuardiansOfTheHolyGrail.class,
		Q00641_AttackSailren.class,
		Q00642_APowerfulPrimevalCreature.class,
		Q00643_RiseAndFallOfTheElrokiTribe.class,
		Q00644_GraveRobberAnnihilation.class,
		Q00645_GhostsOfBatur.class,
		Q00646_SignsOfRevolt.class,
		Q00647_InfluxOfMachines.class,
		Q00648_AnIceMerchantsDream.class,
		Q00649_ALooterAndARailroadMan.class,
		Q00650_ABrokenDream.class,
		Q00651_RunawayYouth.class,
		Q00652_AnAgedExAdventurer.class,
		Q00653_WildMaiden.class,
		Q00654_JourneyToASettlement.class,
		Q00655_AGrandPlanForTamingWildBeasts.class,
		Q00659_IdRatherBeCollectingFairyBreath.class,
		Q00660_AidingTheFloranVillage.class,
		Q00661_MakingTheHarvestGroundsSafe.class,
		Q00662_AGameOfCards.class,
		Q00663_SeductiveWhispers.class,
		Q00688_DefeatTheElrokianRaiders.class,
		Q00690_JudesRequest.class,
		Q00691_MatrasSuspiciousRequest.class,
		Q00692_HowtoOpposeEvil.class,
		Q00695_DefendTheHallOfSuffering.class,
		Q00699_GuardianOfTheSkies.class,
		Q00700_CursedLife.class,
		Q00701_ProofOfExistence.class,
		Q00702_ATrapForRevenge.class,
		Q00708_PathToBecomingALordGludio.class,
		Q00728_TerritoryWar.class,
		Q00901_HowLavasaurusesAreMade.class,
		Q00902_ReclaimOurEra.class,
		Q00903_TheCallOfAntharas.class,
		Q00904_DragonTrophyAntharas.class,
		Q00905_RefinedDragonBlood.class,
		Q00906_TheCallOfValakas.class,
		Q00907_DragonTrophyValakas.class,
		Q00998_FallenAngelSelect.class,
		Q10267_JourneyToGracia.class,
		Q10268_ToTheSeedOfInfinity.class,
		Q10269_ToTheSeedOfDestruction.class,
		Q10270_BirthOfTheSeed.class,
		Q10271_TheEnvelopingDarkness.class,
		Q10272_LightFragment.class,
		Q10273_GoodDayToFly.class,
		Q10274_CollectingInTheAir.class,
		Q10275_ContainingTheAttributePower.class,
		Q10276_MutatedKaneusGludio.class,
		Q10277_MutatedKaneusDion.class,
		Q10278_MutatedKaneusHeine.class,
		Q10279_MutatedKaneusOren.class,
		Q10280_MutatedKaneusSchuttgart.class,
		Q10281_MutatedKaneusRune.class,
		Q10282_ToTheSeedOfAnnihilation.class,
		Q10283_RequestOfIceMerchant.class,
		Q10284_AcquisitionOfDivineSword.class,
		Q10285_MeetingSirra.class,
		Q10286_ReunionWithSirra.class,
		Q10287_StoryOfThoseLeft.class,
		Q10288_SecretMission.class,
		Q10289_FadeToBlack.class,
		Q10290_LandDragonConqueror.class,
		Q10291_FireDragonDestroyer.class,
		Q10292_SevenSignsGirlOfDoubt.class,
		Q10293_SevenSignsForbiddenBookOfTheElmoreAdenKingdom.class,
		Q10294_SevenSignsToTheMonasteryOfSilence.class,
		Q10295_SevenSignsSolinasTomb.class,
		Q10296_SevenSignsOneWhoSeeksThePowerOfTheSeal.class,
		Q10501_ZakenEmbroideredSoulCloak.class,
		Q10502_FreyaEmbroideredSoulCloak.class,
		Q10503_FrintezzaEmbroideredSoulCloak.class,
		Q10504_JewelOfAntharas.class,
		Q10505_JewelOfValakas.class
	};
	
	public static void main(String[] args) {
		int n = 0;
		for (var quest : QUESTS) {
			try {
				quest.getDeclaredConstructor().newInstance();
				n++;
			} catch (Exception ex) {
				LOG.error("Failed loading quest {}!", quest.getSimpleName(), ex);
			}
		}
		LOG.info("Loaded {} quests.", n);
	}
}
