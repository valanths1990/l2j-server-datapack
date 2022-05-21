package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.preferences.ArcherCombatPreferences;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.GeoData;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.model.Location;

public interface Kiter {

    default void kite(Autobot player) {
        if (player.getTarget() == null) return;

        if (!(player.getCombatBehavior().getCombatPreferences() instanceof ArcherCombatPreferences)) return;
        ArcherCombatPreferences prefs = (ArcherCombatPreferences) player.getCombatBehavior().getCombatPreferences();
        if (!prefs.isKiting()) return;

        long kiteRange = prefs.getKiteRadius();
        long distance = (long) player.calculateDistance(player.getTarget().getLocation(), false, false);
        if (distance >= kiteRange) return;

        int posX = (int) (player.getX() + player.getTarget().getX() < player.getX() ? kiteRange : -kiteRange);
        int posY = (int) (player.getY() + player.getTarget().getY() < player.getY() ? kiteRange : -kiteRange);
        int posZ = player.getZ() + 50;
        if (GeoData.getInstance().canMove(player.getX(), player.getY(), player.getZ(), posX, posY, posZ, player.getInstanceId())) {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(posX, posY, posZ));
            Util.sleep(prefs.getKitingDelay());
        }
    }

}
