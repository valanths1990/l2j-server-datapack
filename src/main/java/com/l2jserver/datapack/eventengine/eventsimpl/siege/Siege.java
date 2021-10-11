package com.l2jserver.datapack.eventengine.eventsimpl.siege;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.eventengine.builders.TeamsBuilder;
import com.l2jserver.datapack.eventengine.dispatcher.events.*;
import com.l2jserver.datapack.eventengine.enums.DistributionType;
import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.enums.TeamType;
import com.l2jserver.datapack.eventengine.eventsimpl.siege.config.SiegeConfig;
import com.l2jserver.datapack.eventengine.managers.CacheManager;
import com.l2jserver.datapack.eventengine.model.base.BaseEvent;
import com.l2jserver.datapack.eventengine.model.entity.Player;
import com.l2jserver.datapack.eventengine.model.entity.Team;
import com.l2jserver.datapack.eventengine.model.holder.LocationHolder;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.instancemanager.InstanceManager;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2DoorInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.capturetower.CaptureTower;
import com.l2jserver.gameserver.network.clientpackets.Say2;
import com.l2jserver.gameserver.network.serverpackets.ConfirmDlg;
import com.l2jserver.gameserver.network.serverpackets.CreatureSay;
import com.l2jserver.gameserver.network.serverpackets.FlyToLocation;
import com.l2jserver.gameserver.network.serverpackets.ValidateLocation;
import com.l2jserver.gameserver.util.Broadcast;
import com.l2jserver.gameserver.util.Util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Siege extends BaseEvent<SiegeConfig> {

    private static final int TIME_RES_PLAYER = 10;
    private Team currentConqueror = null;
    private final Map<Player, L2DoorInstance> doorTeleport = new ConcurrentHashMap<>();

    @Override
    protected String getInstanceFile() {
        return "adencastle.xml";
    }

    @Override
    protected TeamsBuilder onCreateTeams() {
        List<LocationHolder> redLocs = this.zone.getRedStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
        List<LocationHolder> blueLocs = this.zone.getBlueStartPoints().stream().map(LocationHolder::new).collect(Collectors.toList());
        TeamsBuilder builder = new TeamsBuilder().setPlayers(getPlayerEventManager().getAllEventPlayers()).setDistribution(DistributionType.TEAM);
        getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.RED).findFirst().map(t -> builder.addTeam(t, redLocs));
        getConfig().getTeams().stream().filter(t -> t.getColor() == TeamType.BLUE).findFirst().map(t -> builder.addTeam(t, blueLocs));
        return builder;
    }

    @Override
    protected void onEventStart() {
        addSuscription(ListenerType.ON_LOG_IN);
        addSuscription(ListenerType.ON_DEATH);
        addSuscription(ListenerType.ON_USE_TELEPORT);
        addSuscription(ListenerType.ON_INTERACT);
        addSuscription(ListenerType.ON_TOWER_CAPTURED);
        addSuscription(ListenerType.ON_DOOR_ACTION);
        addSuscription(ListenerType.ON_USE_SKILL);
        addSuscription(ListenerType.ON_DLG_ANSWER);
        for (L2DoorInstance door : InstanceManager.getInstance().getInstance(world.getInstanceId()).getDoors()) {
            door.setIsAttackableDoor(true);
            door.setInstanceId(world.getInstanceId());
        }
        spawnTowers();
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ScoreTask(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onEventFight() {
    }

    @Override
    protected void onEventEnd() {
        //		giveRewardsTeams();
        getSpawnManager().removeAllNpcs();
    }

    @Override
    public void onDeath(OnDeathEvent event) {
        Player ph = getPlayerEventManager().getEventPlayer(event.getTarget());
        scheduleRevivePlayer(ph, TIME_RES_PLAYER);
    }

    @Override
    protected void onTowerCaptured(OnTowerCapturedEvent event) {
        System.out.println(event.getPlayer() + " capture tower");
        currentConqueror = event.getPlayer().getTeam();
    }

    @Override
    protected void onInteract(OnInteractEvent event) {
        System.out.println("interacted!!");
    }

    @Override
    protected void onDoorAction(OnDoorActionEvent event) {
//        if (currentConqueror == null) {
//            return;
//        }
        if (event.getPlayer().getPcInstance().calculateDistance(event.getDoor().getLocation(), false, false) > 150d) {
            return;
        }
//        if (event.getPlayer().getTeamType() != currentConqueror.getTeamType()) {
//            return;
//        }

        L2PcInstance pc = event.getPlayer().getPcInstance();

        Location doorLocation = event.getDoor().getLocation();
        Location pcLocation = pc.getLocation();




        final double angle = Util.calculateHeadingFrom(event.getDoor().getLocation(),pc.getLocation());
        final double radian =  Math.toRadians(angle);
        final double course = Math.toRadians(180);
        final int x1 = (int) (Math.cos(Math.PI + radian + course) * 180);
        final int y1 = (int) (Math.sin(Math.PI + radian + course) * 180);

        int x = pc.getX() + x1;
        int y = pc.getY() + y1;
        int z = pc.getZ();

//        Location l = GeoData.getInstance().moveCheck(pc.getX(), pc.getY(), pc.getZ(), x, y, z, pc.getInstanceId());
        Location l = new Location(x,y,z,0, world.getInstanceId());
        pc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        pc.broadcastPacket(new FlyToLocation(pc, pc, FlyToLocation.FlyType.DUMMY));
        pc.abortAttack();
        pc.abortCast();
        pc.setXYZ(l);
        pc.broadcastPacket(new ValidateLocation(pc));


    }

    @Override
    protected void onDlgAnswer(OnDlgAnswer event) {
        if (!doorTeleport.containsKey(event.getPlayer()) || event.getAnswer() == 0) {
            return;
        }
        L2DoorInstance door = doorTeleport.remove(event.getPlayer());
        Player p = event.getPlayer();

        if (p.getPcInstance().calculateDistance(door.getLocation(), false, false) > 300d) {
            return;
        }

        Location l = door.getLocation();
        if (p.getPcInstance().isBehind(door)) {
            l.setY(l.getY() + 100);
            l.setX(l.getX() - 100);
        } else {
            l.setY(l.getY() + 100);
            l.setX(l.getX() + 100);
        }
        p.getPcInstance().teleToLocation(l, false);
        p.getPcInstance().abortCast();
        p.getPcInstance().abortAttack();
    }

    @Override
    protected void onUseSkill(OnUseSkillEvent event) {
        if (currentConqueror == null) {
            return;
        }
        if (event.getCaster().isPlayer()) {
            Player p = (Player) event.getCaster();
            if (currentConqueror.getTeamType() == p.getTeamType()) {
                event.setCancel(true);
            }
        }
    }


    private void spawnTowers() {
        zone.getTowerSpawns().forEach(spawns -> {
            CaptureTower tower = new CaptureTower(NpcData.getInstance().getTemplate(Configuration.customs().getCaptureTowerId())
                    , List.of(new TowerBehavior()), new OnprogressUpdate()
                    , (a, b) -> {
            });
            tower.setLocation(spawns);
            tower.setInstanceId(world.getInstanceId());
            tower.setTowerRange(150);
            tower.spawnMe();
            getSpawnManager().addNpc(tower);
        });
    }


    private final class ScoreTask implements Runnable {

        @Override
        public void run() {
            if (currentConqueror == null) {
                return;
            }
            currentConqueror.increasePoints(getConfig().getScoreType(), 1);
            getPlayerEventManager().getAllEventPlayers().forEach(Siege.this::updateScore);
        }
    }

    private static final class TowerBehavior implements Function<List<L2PcInstance>, L2PcInstance> {

        @Override
        public L2PcInstance apply(List<L2PcInstance> l2PcInstances) {
            List<Player> players = l2PcInstances.stream().map(p -> CacheManager.getInstance().getPlayer(p, true)).collect(Collectors.toList());
            Map<Boolean, List<Player>> redBlueTeams = players.stream().collect(Collectors.partitioningBy(p -> p.getTeamType() == TeamType.BLUE));
            if (redBlueTeams.get(true).size() == redBlueTeams.get(false).size()) {
                return null;
            }
            if (redBlueTeams.get(true).size() > redBlueTeams.get(false).size()) {
                return redBlueTeams.get(true).get(Rnd.get(redBlueTeams.get(true).size())).getPcInstance();
            }
            return redBlueTeams.get(false).get(Rnd.get(redBlueTeams.get(false).size())).getPcInstance();
        }
    }

    private final class OnprogressUpdate implements Consumer<CaptureTower> {

        @Override
        public void accept(CaptureTower captureTower) {
            if (captureTower.getCapturer() == null) {
                return;
            }
            Team team = CacheManager.getInstance().getPlayer(captureTower.getCapturer(), true).getTeam();
            if (team == null) {
                return;
            }
            if (captureTower.getProgress() % 25 == 0) {
                Broadcast.toPlayersInInstance(new CreatureSay(0, Say2.ANNOUNCEMENT, "", team.getName() + " has captured " + captureTower.getProgress() + "% of the Altar"), world.getInstanceId());
            }
        }
    }

}
