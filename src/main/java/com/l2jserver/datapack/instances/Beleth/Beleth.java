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
package com.l2jserver.datapack.instances.Beleth;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.l2jserver.datapack.instances.GrandBossInstance;
import com.l2jserver.datapack.instances.Status;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.data.xml.impl.DoorData;
import com.l2jserver.gameserver.enums.audio.Music;
import com.l2jserver.gameserver.instancemanager.MapRegionManager;
import com.l2jserver.gameserver.model.L2Object;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.TeleportWhereType;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.effects.L2EffectType;
import com.l2jserver.gameserver.model.holders.ItemHolder;
import com.l2jserver.gameserver.model.holders.SkillHolder;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.skills.Skill;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.network.serverpackets.DoorStatusUpdate;
import com.l2jserver.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jserver.gameserver.network.serverpackets.SocialAction;
import com.l2jserver.gameserver.network.serverpackets.SpecialCamera;
import com.l2jserver.gameserver.network.serverpackets.StaticObject;
import com.l2jserver.gameserver.util.Util;

/**
 * Beleth's AI.
 *
 * @author Treat, Sahar
 */
public final class Beleth extends GrandBossInstance {
    // NPCs
    private static final int REAL_BELETH = 29118;
    private static final int FAKE_BELETH = 29119;
    private static final int STONE_COFFIN = 32470;
    private static final int ELF = 29128;
    private static final int WHIRPOOL = 29125;

    private static final Location BELETH_SPAWN = new Location(16323, 213059, -9357, 49152);
    // Skills
    private static final SkillHolder BLEED = new SkillHolder(5495);
    private static final SkillHolder FIREBALL = new SkillHolder(5496);
    private static final SkillHolder HORN_OF_RISING = new SkillHolder(5497);
    private static final SkillHolder LIGHTENING = new SkillHolder(5499);
    // Doors
    private static final int DOOR1 = 20240001;
    private static final int DOOR2 = 20240002;
    private static final int DOOR3 = 20240003;
    private static final int HELLBOUND_DOOR = 20250001;
    // Items
    private static final ItemHolder RING = new ItemHolder(10314, 1);
    // Variables
    private L2Npc _camera1;
    private L2Npc _camera2;
    private L2Npc _camera3;
    private L2Npc _camera4;
    private L2Npc _whirpool;
    private L2Npc boss;
    private L2Npc _priest;
    private L2Npc _stone;
    private L2PcInstance _killer;
    private int _allowedObjId;
    private int _killedCount;
    private long _lastAttack;
    private final List<L2Npc> _minions = new CopyOnWriteArrayList<>();

    public Beleth() {
        super(Beleth.class.getSimpleName(), REAL_BELETH, 70056, "Beleth.xml", new InstanceWorld());
        addEnterZoneId(zone.getId());
        registerMobs(REAL_BELETH, FAKE_BELETH);
        addStartNpc(STONE_COFFIN);
        addTalkId(STONE_COFFIN);
        addFirstTalkId(ELF);

        DoorData.getInstance().getDoor(HELLBOUND_DOOR).openMe();
        getDoor(DOOR1, instanceId).openMe();
    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        switch (event) {
            case "CAST" -> {
                if (!npc.isDead() && !npc.isCastingNow()) {
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    npc.doCast(FIREBALL);
                }
            }
            case "SPAWN1" -> {
                zone.getCharactersInside().forEach(c -> {
                    c.disableAllSkills();
                    c.setIsInvul(true);
                    c.setIsImmobilized(true);
                });

                _camera1 = addSpawn(29120, new Location(16323, 213142, -9357), false, 0, false, instanceId);
                _camera2 = addSpawn(29121, new Location(16323, 210741, -9357), false, 0, false, instanceId);
                _camera3 = addSpawn(29122, new Location(16323, 213170, -9357), false, 0, false, instanceId);
                _camera4 = addSpawn(29123, new Location(16323, 214917, -9356), false, 0, false, instanceId);

                zone.broadcastPacket(Music.BS07_A_10000.getPacket());
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 75, -25, 0, 2500, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 75, -25, 0, 2500, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN2", 300, null, null);
            }
            case "SPAWN2" -> {
                zone.broadcastPacket(new SpecialCamera(_camera1, 1800, -45, -45, 5000, 5000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN3", 4900, null, null);
            }
            case "SPAWN3" -> {
                zone.broadcastPacket(new SpecialCamera(_camera1, 2500, -120, -45, 5000, 5000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN4", 4900, null, null);
            }
            case "SPAWN4" -> {
                zone.broadcastPacket(new SpecialCamera(_camera2, 2200, 130, 0, 0, 1500, -20, 15, 1, 0, 0));

                startQuestTimer("SPAWN5", 1400, null, null);
            }
            case "SPAWN5" -> {
                zone.broadcastPacket(new SpecialCamera(_camera2, 2300, 100, 0, 2000, 4500, 0, 10, 1, 0, 0));

                startQuestTimer("SPAWN6", 2500, null, null);
            }
            case "SPAWN6" -> {
                final L2DoorInstance door = DoorData.getInstance().getDoor(DOOR1);
                getDoor(DOOR1, instanceId).closeMe();

                zone.broadcastPacket(new StaticObject(door, false));
                zone.broadcastPacket(new DoorStatusUpdate(door));

                startQuestTimer("SPAWN7", 1700, null, null);
            }
            case "SPAWN7" -> {
                zone.broadcastPacket(new SpecialCamera(_camera4, 1500, 210, 0, 0, 1500, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera4, 900, 255, 0, 5000, 6500, 0, 10, 1, 0, 0));

                startQuestTimer("SPAWN8", 6000, null, null);
            }
            case "SPAWN8" -> {
                _whirpool = addSpawn(WHIRPOOL, new Location(16323, 214917, -9356), false, 0, false, instanceId);

                zone.broadcastPacket(new SpecialCamera(_camera4, 900, 255, 0, 0, 1500, 0, 10, 1, 0, 0));

                startQuestTimer("SPAWN9", 1000, null, null);
            }
            case "SPAWN9" -> {
                zone.broadcastPacket(new SpecialCamera(_camera4, 1000, 255, 0, 7000, 17000, 0, 25, 1, 0, 0));

                startQuestTimer("SPAWN10", 3000, null, null);
            }
            case "SPAWN10" -> {
                boss = addSpawn(REAL_BELETH, new Location(16321, 214211, -9352, 49369), false, 0, false, instanceId);
                boss.disableAllSkills();
                boss.setIsInvul(true);
                boss.setIsImmobilized(true);

                startQuestTimer("SPAWN11", 200, null, null);
            }
            case "SPAWN11" -> {
                zone.broadcastPacket(new SocialAction(boss.getObjectId(), 1));

                for (int i = 0; i < 6; i++) {
                    int x = (int) ((150 * Math.cos(i * 1.046666667)) + 16323);
                    int y = (int) ((150 * Math.sin(i * 1.046666667)) + 213059);
                    L2Npc minion = addSpawn(FAKE_BELETH, new Location(x, y, -9357, 49152), false, 0, false, instanceId);
                    minion.setShowSummonAnimation(true);
                    minion.decayMe();

                    _minions.add(minion);
                }

                startQuestTimer("SPAWN12", 6800, null, null);
            }
            case "SPAWN12" -> {
                zone.broadcastPacket(new SpecialCamera(boss, 0, 270, -5, 0, 4000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN13", 3500, null, null);
            }
            case "SPAWN13" -> {
                zone.broadcastPacket(new SpecialCamera(boss, 800, 270, 10, 3000, 6000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN14", 5000, null, null);
            }
            case "SPAWN14" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 100, 270, 15, 0, 5000, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera3, 100, 270, 15, 0, 5000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN15", 100, null, null);
            }
            case "SPAWN15" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 100, 270, 15, 3000, 6000, 0, 5, 1, 0, 0));

                startQuestTimer("SPAWN16", 1400, null, null);
            }
            case "SPAWN16" -> {
                boss.teleToLocation(BELETH_SPAWN, instanceId, 0);

                startQuestTimer("SPAWN17", 200, null, null);
            }
            case "SPAWN17" -> {
                zone.broadcastPacket(new MagicSkillUse(boss, boss, 5532, 1, 2000, 0));

                startQuestTimer("SPAWN18", 2000, null, null);
            }
            case "SPAWN18" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 700, 270, 20, 1500, 8000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN19", 6900, null, null);
            }
            case "SPAWN19" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 40, 260, 0, 0, 4000, 0, 0, 1, 0, 0));

                for (L2Npc fakeBeleth : _minions) {
                    fakeBeleth.spawnMe();
                    fakeBeleth.disableAllSkills();
                    fakeBeleth.setIsInvul(true);
                    fakeBeleth.setIsImmobilized(true);
                }

                startQuestTimer("SPAWN20", 3000, null, null);
            }
            case "SPAWN20" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 40, 280, 0, 0, 4000, 5, 0, 1, 0, 0));

                startQuestTimer("SPAWN21", 3000, null, null);
            }
            case "SPAWN21" -> {
                zone.broadcastPacket(new SpecialCamera(_camera3, 5, 250, 5, 0, 13000, 20, 15, 1, 0, 0));

                startQuestTimer("SPAWN22", 1000, null, null);
            }
            case "SPAWN22" -> {
                zone.broadcastPacket(new SocialAction(boss.getObjectId(), 3));

                startQuestTimer("SPAWN23", 4000, null, null);
            }
            case "SPAWN23" -> {
                zone.broadcastPacket(new MagicSkillUse(boss, boss, 5533, 1, 2000, 0));

                startQuestTimer("SPAWN24", 6800, null, null);
            }
            case "SPAWN24" -> {
                boss.deleteMe();
                boss = null;

                for (L2Npc fakeBeleth : _minions) {
                    fakeBeleth.deleteMe();
                }
                _minions.clear();

                _camera1.deleteMe();
                _camera2.deleteMe();
                _camera3.deleteMe();
                _camera4.deleteMe();

                for (L2Character c : zone.getCharactersInside()) {
                    c.enableAllSkills();
                    c.setIsInvul(false);
                    c.setIsImmobilized(false);
                }

                _lastAttack = System.currentTimeMillis();

                startQuestTimer("CHECK_ATTACK", 60000, null, null);

                startQuestTimer("SPAWN25", 60000, null, null);
            }
            case "SPAWN25" -> {
                _minions.clear();

                int a = 0;
                for (int i = 0; i < 16; i++) {
                    a++;

                    int x = (int) ((650 * Math.cos(i * 0.39)) + 16323);
                    int y = (int) ((650 * Math.sin(i * 0.39)) + 213170);

                    npc = addSpawn(FAKE_BELETH, new Location(x, y, -9357, 49152), false, 0, false, instanceId);
                    _minions.add(npc);

                    if (a >= 2) {
                        npc.setIsOverloaded(true);
                        a = 0;
                    }
                }

                int[] xm = new int[16];
                int[] ym = new int[16];
                for (int i = 0; i < 4; i++) {
                    xm[i] = (int) ((1700 * Math.cos((i * 1.57) + 0.78)) + 16323);
                    ym[i] = (int) ((1700 * Math.sin((i * 1.57) + 0.78)) + 213170);

                    npc = addSpawn(FAKE_BELETH, new Location(xm[i], ym[i], -9357, 49152), false, 0, false, instanceId);
                    npc.setIsImmobilized(true);

                    _minions.add(npc);
                }

                xm[4] = (xm[0] + xm[1]) / 2;
                ym[4] = (ym[0] + ym[1]) / 2;
                npc = addSpawn(FAKE_BELETH, new Location(xm[4], ym[4], -9357, 49152), false, 0, false, instanceId);
                npc.setIsImmobilized(true);
                _minions.add(npc);
                xm[5] = (xm[1] + xm[2]) / 2;
                ym[5] = (ym[1] + ym[2]) / 2;
                npc = addSpawn(FAKE_BELETH, new Location(xm[5], ym[5], -9357, 49152), false, 0, false, instanceId);
                npc.setIsImmobilized(true);
                _minions.add(npc);
                xm[6] = (xm[2] + xm[3]) / 2;
                ym[6] = (ym[2] + ym[3]) / 2;
                npc = addSpawn(FAKE_BELETH, new Location(xm[6], ym[6], -9357, 49152), false, 0, false, instanceId);
                npc.setIsImmobilized(true);
                _minions.add(npc);
                xm[7] = (xm[3] + xm[0]) / 2;
                ym[7] = (ym[3] + ym[0]) / 2;
                npc = addSpawn(FAKE_BELETH, new Location(xm[7], ym[7], -9357, 49152), false, 0, false, instanceId);
                npc.setIsImmobilized(true);
                _minions.add(npc);

                xm[8] = (xm[0] + xm[4]) / 2;
                ym[8] = (ym[0] + ym[4]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[8], ym[8], -9357, 49152), false, 0, false, instanceId));
                xm[9] = (xm[4] + xm[1]) / 2;
                ym[9] = (ym[4] + ym[1]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[9], ym[9], -9357, 49152), false, 0, false, instanceId));
                xm[10] = (xm[1] + xm[5]) / 2;
                ym[10] = (ym[1] + ym[5]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[10], ym[10], -9357, 49152), false, 0, false, instanceId));
                xm[11] = (xm[5] + xm[2]) / 2;
                ym[11] = (ym[5] + ym[2]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[11], ym[11], -9357, 49152), false, 0, false, instanceId));
                xm[12] = (xm[2] + xm[6]) / 2;
                ym[12] = (ym[2] + ym[6]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[12], ym[12], -9357, 49152), false, 0, false, instanceId));
                xm[13] = (xm[6] + xm[3]) / 2;
                ym[13] = (ym[6] + ym[3]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[13], ym[13], -9357, 49152), false, 0, false, instanceId));
                xm[14] = (xm[3] + xm[7]) / 2;
                ym[14] = (ym[3] + ym[7]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[14], ym[14], -9357, 49152), false, 0, false, instanceId));
                xm[15] = (xm[7] + xm[0]) / 2;
                ym[15] = (ym[7] + ym[0]) / 2;
                _minions.add(addSpawn(FAKE_BELETH, new Location(xm[15], ym[15], -9357, 49152), false, 0, false, instanceId));

                _allowedObjId = _minions.get(getRandom(_minions.size())).getObjectId();
                System.out.println(_allowedObjId);
            }
            case "SPAWN_REAL" -> boss = addSpawn(REAL_BELETH, new Location(16323, 213170, -9357, 49152), false, 0, false, instanceId);
            case "SPAWN26" -> {
                boss.doDie(null);

                _camera1 = addSpawn(29122, new Location(16323, 213170, -9357), false, 0, false, instanceId);
                _camera1.broadcastPacket(Music.BS07_D_10000.getPacket());

                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 290, 25, 0, 10000, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 290, 25, 0, 10000, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 110, 25, 4000, 10000, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SocialAction(boss.getObjectId(), 5));

                startQuestTimer("SPAWN27", 4000, null, null);
            }
            case "SPAWN27" -> {
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 295, 25, 4000, 5000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN28", 4500, null, null);
            }
            case "SPAWN28" -> {
                zone.broadcastPacket(new SpecialCamera(_camera1, 400, 295, 10, 4000, 11000, 0, 25, 1, 0, 0));

                startQuestTimer("SPAWN29", 9000, null, null);
            }
            case "SPAWN29" -> {
                zone.broadcastPacket(new SpecialCamera(_camera1, 250, 90, 25, 0, 1000, 0, 0, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera1, 250, 90, 25, 0, 10000, 0, 0, 1, 0, 0));

                startQuestTimer("SPAWN30", 2000, null, null);
            }
            case "SPAWN30" -> {
                _priest.spawnMe();
                boss.deleteMe();

                _camera2 = addSpawn(29121, new Location(14056, 213170, -9357), false, 0, false, instanceId);

                startQuestTimer("SPAWN31", 3500, null, null);
            }
            case "SPAWN31" -> {
                zone.broadcastPacket(new SpecialCamera(_camera2, 800, 180, 0, 0, 4000, 0, 10, 1, 0, 0));
                zone.broadcastPacket(new SpecialCamera(_camera2, 800, 180, 0, 0, 4000, 0, 10, 1, 0, 0));

                L2DoorInstance door2 = getDoor(DOOR2, instanceId);
                door2.openMe();

                zone.broadcastPacket(new StaticObject(door2, false));
                zone.broadcastPacket(new DoorStatusUpdate(door2));

                getDoor(DOOR3, instanceId).openMe();

                _camera1.deleteMe();
                _camera2.deleteMe();
                _whirpool.deleteMe();

                for (L2Character c : zone.getCharactersInside()) {
                    c.enableAllSkills();
                    c.setIsInvul(false);
                    c.setIsImmobilized(false);
                }
            }
            case "CHECK_ATTACK" -> {
                if ((_lastAttack + 900000) < System.currentTimeMillis()) {
//					GrandBossManager.getInstance().setBossStatus(REAL_BELETH, ALIVE);
                    status = Status.ALIVE;
                    for (L2Character charInside : zone.getCharactersInside()) {
                        if (charInside != null) {
                            if (charInside.isNpc()) {
                                charInside.deleteMe();
                            } else if (charInside.isPlayer()) {
                                charInside.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(charInside, TeleportWhereType.TOWN));
                            }
                        }
                    }
                    cancelQuestTimer("CHECK_ATTACK", null, null);
                } else {
                    startQuestTimer("CHECK_ATTACK", 60000, null, null);
                }
            }
        }
        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onEnterZone(L2Character character, L2ZoneType zone) {
        if (character.isPlayer() && status == Status.WAITING) {
            if (_priest != null) {
                _priest.deleteMe();
            }
            if (_stone != null) {
                _stone.deleteMe();
            }

            status = Status.FIGHT;
            startQuestTimer("SPAWN1", 300000, null, null); //300000
        }

        return super.onEnterZone(character, zone);
    }

    @Override
    public String onSkillSee(L2Npc npc, L2PcInstance player, Skill skill, L2Object[] targets, boolean isSummon) {
        if (!npc.isDead() && (npc.getId() == REAL_BELETH) && !npc.isCastingNow() && skill.hasEffectType(L2EffectType.HP) && (getRandom(100) < 80)) {
            npc.setTarget(player);
            npc.doCast(HORN_OF_RISING);
        }
        return null;
    }

    @Override
    public String onAggroRangeEnter(L2Npc npc, L2PcInstance player, boolean isSummon) {
        if (!npc.isDead() && !npc.isCastingNow()) {
            if (getRandom(100) < 40) {
                if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty()) {
                    npc.doCast(BLEED);
                    return null;
                }
            }
            npc.setTarget(player);
            npc.doCast(FIREBALL);
        }

        return null;
    }

    @Override
    public String onSpellFinished(L2Npc npc, L2PcInstance player, Skill skill) {
        if (!npc.isDead() && !npc.isCastingNow()) {
            if (!player.isDead()) {
                final double distance2 = npc.calculateDistance(player, false, false);
                if ((distance2 > 890) && !npc.isMovementDisabled()) {
                    npc.setTarget(player);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player);
                    double speed = npc.isRunning() ? npc.getRunSpeed() : npc.getWalkSpeed();
                    int time = (int) (((distance2 - 890) / speed) * 1000);
                    startQuestTimer("CAST", time, npc, null);
                } else if (distance2 < 890) {
                    npc.setTarget(player);
                    npc.doCast(FIREBALL);
                }
                return null;
            }
            if (getRandom(100) < 40) {
                if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty()) {
                    npc.doCast(LIGHTENING);
                    return null;
                }
            }
            for (L2PcInstance plr : npc.getKnownList().getKnownPlayersInRadius(950)) {
                npc.setTarget(plr);
                npc.doCast(FIREBALL);
                return null;
            }
            ((L2Attackable) npc).clearAggroList();
        }
        return null;
    }

    @Override
    public String onSpawn(L2Npc npc) {
        npc.setRunning();
        if (!npc.getKnownList().getKnownPlayersInRadius(300).isEmpty() && (getRandom(100) < 60)) {
            npc.doCast(BLEED);
        }
        if (npc.getId() == REAL_BELETH) {
            npc.getSpawn().setRespawnDelay(0);
        }

        return null;
    }

    @Override
    public String onTalk(L2Npc npc, L2PcInstance player) {
        String html;
        if ((_killer != null) && (player.getObjectId() == _killer.getObjectId())) {
            _killer = null;

            giveItems(player, RING);

            html = "32470a.htm";
        } else {
            html = "32470b.htm";
        }

        return HtmCache.getInstance().getHtm(player.getHtmlPrefix(), "data/html/default/" + html);
    }

    @Override
    public String onFirstTalk(L2Npc npc, L2PcInstance player) {
        return onTalk(npc, player);
    }

    @Override
    public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
        if (getRandom(100) < 40) {
            return null;
        }

        final double distance = npc.calculateDistance(attacker, false, false);
        if ((distance > 500) || (getRandom(100) < 80)) {
            for (L2Npc beleth : _minions) {
                if ((beleth != null) && !beleth.isDead() && Util.checkIfInRange(900, beleth, attacker, false) && !beleth.isCastingNow()) {
                    beleth.setTarget(attacker);
                    beleth.doCast(FIREBALL);
                }
            }
            if ((boss != null) && !boss.isDead() && Util.checkIfInRange(900, boss, attacker, false) && !boss.isCastingNow()) {
                boss.setTarget(attacker);
                boss.doCast(FIREBALL);
            }
        } else if (!npc.isDead() && !npc.isCastingNow()) {
            if (!npc.getKnownList().getKnownPlayersInRadius(200).isEmpty()) {
                npc.doCast(LIGHTENING);
                return null;
            }
            ((L2Attackable) npc).clearAggroList();
        }

        return null;
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
        if (npc.getId() == REAL_BELETH) {
            cancelQuestTimer("CHECK_ATTACK", null, null);
            setBelethKiller(killer);
            status = Status.DEAD;

            for (L2Character c : zone.getCharactersInside()) {
                c.disableAllSkills();
                c.setIsInvul(true);
                c.setIsImmobilized(true);
            }

            boss = addSpawn(REAL_BELETH, new Location(16323, 213170, -9357, 49152), false, 0, false, instanceId);
            boss.disableAllSkills();
            boss.setIsInvul(true);
            boss.setIsImmobilized(true);

            _priest = addSpawn(ELF, new Location(boss), false, 0, false, instanceId);
            _priest.setShowSummonAnimation(true);
            _priest.decayMe();

            _stone = addSpawn(STONE_COFFIN, new Location(12470, 215607, -9381, 49152), false, 0, false, instanceId);

            startQuestTimer("SPAWN26", 1000, null, null);
        } else if (npc.getObjectId() == _allowedObjId) {
            deleteAll();

            _killedCount++;
            if (_killedCount >= 5) {
                startQuestTimer("SPAWN_REAL", 60000, null, null);
            } else {
                startQuestTimer("SPAWN25", 60000, null, null);
            }
        }

        return super.onKill(npc, killer, isSummon);
    }


    private void setBelethKiller(L2PcInstance killer) {
        if (killer.getParty() != null) {
            if (killer.getParty().getCommandChannel() != null) {
                _killer = killer.getParty().getCommandChannel().getLeader();
            } else {
                _killer = killer.getParty().getLeader();
            }
        } else {
            _killer = killer;
        }
    }

    private void deleteAll() {
        _minions.stream().filter(n -> !n.isDead()).forEach(n -> {
            n.abortCast();
            n.setTarget(null);
            n.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
            n.deleteMe();
        });
        _allowedObjId = 0;
    }
}