package com.l2jserver.datapack.instances;

import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Npc;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.*;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventDispatcher;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.OnGrandBossKill;
import com.l2jserver.gameserver.model.instancezone.InstanceWorld;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.type.L2NoRestartZone;

import java.util.List;
import java.util.Optional;

public abstract class GrandBossInstance extends AbstractInstance {

    protected final int grandBossId;
    protected L2GrandBossInstance boss;
    protected Integer instanceId;
    protected InstanceWorld world;
    protected final L2NoRestartZone zone;
    private final CaptureTower entryTower;
    protected Status status = Status.WAITING;
    protected int templadeId = 0;
    protected boolean firstEntrance = true;
    public GrandBossInstance(String name, int grandBossId, int zoneId, String instanceFile, InstanceWorld instanceWorld) {
        super(name, "GrandBossInstance");
        zone = ZoneManager.getInstance().getZoneById(zoneId, L2NoRestartZone.class);
        this.grandBossId = grandBossId;
        instanceId = InstanceManager.getInstance().createDynamicInstance(instanceFile);
        this.world = instanceWorld;
        world.setInstanceId(instanceId);
        world.setTemplateId(templadeId);
        world.setStatus(0);
        InstanceManager.getInstance().addWorld(world);

        entryTower = new CaptureTower(NpcData.getInstance().getTemplate(Configuration.customs().getCaptureTowerId()),
                List.of(new CheckForSinglePlayer(), new CheckForParty(), new CheckForPartyCommandChannel(), new CheckForClan(), new CheckForAlliance()),
                new OnTowerProgressUpdate(), new OnCaptureTowerAction());
        entryTower.setLocation(zone.getTowerLocation());
        entryTower.setInstanceId(0);
        entryTower.spawnMe();
        System.out.println(instanceId);

    }

    @Override
    public String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        switch (event) {
            case "enter" -> {
                String htmltext = "";
                if (status == Status.DEAD) {
                    return "13001-01.html";
                }
                onEnterInstance(player, world, true);
                return htmltext;
            }
        }
        return super.onAdvEvent(event, npc, player);
    }

    @Override
    public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon) {
        if (npc.getId() == grandBossId) {
            EventDispatcher.getInstance().notifyEventAsync(new OnGrandBossKill(killer, (L2GrandBossInstance) npc),killer);
            finishInstance(world);
        }
        return super.onKill(npc, killer, isSummon);
    }

    @Override
    protected void onEnterInstance(L2PcInstance player, InstanceWorld world, boolean firstEntrance) {
        teleportPlayer(player, zone.getEntranceLocation(), instanceId, true);
    }

    @Override
    public boolean unload(boolean removeFromList) {
        entryTower.deleteMe();
        InstanceManager.getInstance().destroyInstance(instanceId);
        return super.unload(removeFromList);
    }

    @Override
    public String onExitZone(L2Character character, L2ZoneType zone) {
//        if (character instanceof L2Playable) {
////            character.setInstanceId(0);
//        }
        return super.onExitZone(character, zone);
    }

    public CaptureTower getTower() {
        return entryTower;
    }

    public Optional<L2NoRestartZone> getZone() {
        return Optional.ofNullable(this.zone);
    }

    public Optional<L2GrandBossInstance> getBoss() {
        return Optional.ofNullable(this.boss);
    }

    public int getGrandBossId() {
        return grandBossId;
    }

}
