package com.l2jserver.datapack.autobots.behaviors.sequences;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.utils.CancellationToken;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.data.sql.impl.TeleportLocationTable;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2TeleporterInstance;
import com.l2jserver.gameserver.model.zone.ZoneId;

import java.util.Comparator;
import java.util.Optional;

public class ReturnToDeathLocationSequence implements Sequence {

    private final Autobot player;
    private CancellationToken cancellationToken;

    public ReturnToDeathLocationSequence(Autobot player) {
        this.player = player;
    }

    @Override
    public Autobot getPlayer() {
        return player;
    }

    @Override
    public CancellationToken getCancellationToken() {
        return cancellationToken;
    }

    @Override
    public void setCancellationToken(CancellationToken cancellationToken) {
        this.cancellationToken = cancellationToken;
    }

    @Override
    public void definition() {
        if (player.getLastLocation() == null) return;
        Location deathLocation = player.location();
        Util.sleep(Rnd.get(5000, 8000));
        Optional<L2TeleporterInstance> gatekeeper = player.findClosestGatekeeper();
        if (gatekeeper.isEmpty()) return;

        player.setTarget(gatekeeper.get());
        Location l = gatekeeper.get().getLocation();
        player.moveto(l.getX(), l.getY(), l.getZ());
        while (player.calculateDistance(gatekeeper.get().getLocation(), false, false) >= 110) {
            if (player.isDead()) return;
            if (player.calculateDistance(gatekeeper.get(), false, false) <= 1000) gatekeeper.get().onAction(player);
            Util.sleep(Rnd.get(4000, 6000));
        }
        Util.sleep(Rnd.get(2000, 4000));

        Optional<Location> closestTeleport = TeleportLocationTable.getInstance().getAllTeleportLocations().stream().filter(t -> !t.getIsForNoble()).min(Comparator.comparing(t -> player.calculateDistance(new Location(t.getLocX(), t.getLocY(), t.getLocZ()), false, false))).map(t -> new Location(t.getLocX(), t.getLocY(), t.getLocZ(), player.getInstanceId()));
        closestTeleport.ifPresent(player::teleToLocation);
        Util.sleep(Rnd.get(5000, 8000));
        if (player.isInsideZone(ZoneId.TOWN)) {
            player.getSocialBehavior().onRespawn();
            return;
        }
        player.moveto(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());
        while (player.calculateDistance(deathLocation, false, false) >= 1500) {
            if (player.isDead()) return;
            if (player.isInCombat()) return;
            if (!player.isMoving()) player.moveto(deathLocation.getX(), deathLocation.getY(), deathLocation.getZ());
            Util.sleep(Rnd.get(2000, 4000));
        }
    }
}