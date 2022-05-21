package com.l2jserver.datapack.autobots.behaviors.attributes;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface Healer {
    default void tryTargetingLowestHpTargetInRadius(Autobot player, int radius) {
        List<L2PcInstance> finalTargets = new ArrayList<>();
        finalTargets.add(player); //self
        List<L2PcInstance> targets = player.getKnownList().getKnownPlayersInRadius(radius).stream().filter(p -> p.isInParty() && player.isInParty() && player.getParty() == p.getParty()).collect(Collectors.toList());

        List<L2PcInstance> partyMembers = targets.stream()
                .filter(p -> p.isInParty() && player.isInParty() && player.getParty() == p.getParty() && p.getHpPercentage() < 80)
                .collect(Collectors.toList());
        if (partyMembers.isEmpty()) {
            List<L2PcInstance> clanMembers = targets.stream()
                    .filter(p -> p.getClan() != null && player.getClan() != null && player.getClanId() == p.getClanId() && p.getHpPercentage() < 80)
                    .collect(Collectors.toList());
            if (!clanMembers.isEmpty()) {
                finalTargets.addAll(clanMembers);
            }
        } else {
            finalTargets.addAll(partyMembers);
        }
        finalTargets.stream().filter(p -> !p.isDead()).min(Comparator.comparing(L2PcInstance::getHpPercentage)).ifPresent(p -> {
            if (p.getHpPercentage() < 80) {
                player.setTarget(p);
            } else {
                player.setTarget(player);
            }
        });

    }
}

//    fun tryTargetingLowestHpTargetInRadius(player:Player, radius: Int) {
//        val finalTargets = mutableListOf<Player>()
//
//        val targets = player.getKnownTypeInRadius(Player::class.java, radius).filter { (it.isInParty && player.isInParty && player.party == it.party) || (it.clan != null && player.clan != null && player.clanId == it.clanId) }.toMutableList()
//        finalTargets.add(player)
//
//        val partyMembers = targets.filter { it.isInParty && player.isInParty && it.party == player.party && it.getHealthPercentage() < 80 }
//
//        if(partyMembers.isEmpty()) {
//            val clanMembers = targets.filter { it.clan != null && player.clan != null && player.clanId == it.clanId && it.getHealthPercentage() < 80 }
//
//            if(clanMembers.isNotEmpty()) {
//                finalTargets.addAll(clanMembers)
//            }
//        } else {
//            finalTargets.addAll(partyMembers)
//        }
//
//        val target = finalTargets.filter { !it!!.isDead }.minBy { it!!.getHealthPercentage() } ?: return
//
//        //TODO fix this broken shit
//
//        if(!target.isFullHealth() && target.getHealthPercentage() < 80) {
//            player.target = target
//            return
//        }
//
//        if(target.isDead) {
//            player.target = null
//            return
//        }
//    }