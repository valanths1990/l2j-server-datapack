package com.l2jserver.datapack.autobots.behaviors;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.AutobotData;
import com.l2jserver.datapack.autobots.behaviors.preferences.SocialPreferences;
import com.l2jserver.datapack.autobots.behaviors.sequences.ReturnToDeathLocationSequence;
import com.l2jserver.datapack.autobots.behaviors.sequences.TeleportToLocationSequence;
import com.l2jserver.datapack.autobots.behaviors.sequences.TradingSequence;
import com.l2jserver.datapack.autobots.models.AutobotLocation;
import com.l2jserver.datapack.autobots.utils.Util;

import java.util.List;


public class SocialBehavior {
    private final Autobot player;
    private SocialPreferences socialPreferences;

    public SocialBehavior(Autobot player, SocialPreferences socialPreferences) {
        this.player = player;
        this.socialPreferences = socialPreferences;
    }

    public void onUpdate() {
        if (player.getActiveSequence() != null) return;
        if (player.hasActiveTradeRequest() && player.getActiveTradeList() == null) {
            Util.sleep(2000);
            player.answerTradeRequest(true);
            new TradingSequence(player).execute();
            return;
        }
        if (player.getSocialBehavior().getSocialPreferences().townAction == SocialPreferences.TownAction.TeleToRandomLocation
                && Rnd.get(2500) == 0) {
            List<AutobotLocation> locs = AutobotData.getInstance().getTeleportLocations();
            AutobotLocation autobotLoc = locs.get(Rnd.get(locs.size()));
            new TeleportToLocationSequence(player, autobotLoc.getLocation()).execute();
        }
    }

    public void onRespawn() {
        switch (player.getRespawnAction()) {
            case None -> {
            }
            case ReturnToDeathLocation -> new ReturnToDeathLocationSequence(player).execute();
            case Logout -> {
                Util.sleep(Rnd.get(10000, 60000));
                player.despawn();
            }
        }
    }


    public Autobot getPlayer() {
        return player;
    }

    public SocialPreferences getSocialPreferences() {
        return socialPreferences;
    }

    public void setSocialPreferences(SocialPreferences socialPreferences) {
        this.socialPreferences = socialPreferences;
    }
}
