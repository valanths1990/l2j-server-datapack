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

public class TeleportToLocationSequence implements Sequence {
    private final Autobot player;
    private final Location targetLocation;
    private CancellationToken cancellationToken;

    public TeleportToLocationSequence(Autobot player, Location targetLocation) {
        this.player = player;
        this.targetLocation = targetLocation;
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

        if (!player.isInsideZone(ZoneId.TOWN)) return;
        Util.sleep(Rnd.get(5000, 8000));
        Optional<L2TeleporterInstance> gatekeeper = player.findClosestGatekeeper();
        if (gatekeeper.isEmpty()) return;

        player.setTarget(gatekeeper.get());
        Location l = gatekeeper.get().getLocation();
        player.moveto(l.getX(), l.getY(), l.getZ());
        while (player.calculateDistance(l, false, false) >= 110) {
            if (player.isDead()) return;
            if (player.calculateDistance(l, false, false) <= 1000) gatekeeper.get().onAction(player);
            Util.sleep(Rnd.get(4000, 6000));
        }
        Util.sleep(Rnd.get(2000, 4000));
        Optional<Location> closestTeleport = TeleportLocationTable.getInstance().getAllTeleportLocations().stream().filter(t -> !t.getIsForNoble())
                .min(Comparator.comparing(t -> com.l2jserver.gameserver.util.Util.calculateDistance(targetLocation, new Location(t.getLocX(), t.getLocY(), t.getLocZ()), false, false)))
                .map(t -> new Location(t.getLocX(), t.getLocY(), t.getLocZ(), targetLocation.getInstanceId()));
        closestTeleport.ifPresent(player::teleToLocation);
        Util.sleep(Rnd.get(5000, 8000));
        if (player.isInsideZone(ZoneId.TOWN)) {
            definition();
            return;
        }
        player.moveto(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
        while (player.calculateDistance(targetLocation, false, false) >= 1500) {
            if (player.isDead()) return;
            if (player.isInCombat()) return;
            if (!player.isMoving()) player.moveto(targetLocation.getX(), targetLocation.getY(), targetLocation.getZ());
            Util.sleep(Rnd.get(2000, 4000));

        }
    }
}
