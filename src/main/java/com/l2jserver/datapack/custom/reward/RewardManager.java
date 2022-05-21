package com.l2jserver.datapack.custom.reward;

import com.l2jserver.datapack.custom.Json;
import com.l2jserver.datapack.custom.reward.pojo.Reward;
import com.l2jserver.datapack.custom.reward.pojo.Rewards;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.instancemanager.MailManager;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.entity.Message;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureKill;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class RewardManager {
    private Rewards rewards;
    private final Random rand = new Random();

    private RewardManager() {
        reload();
        Containers.Players().addListener(new ConsumerEventListener(Containers.Players(), EventType.ON_PLAYER_PVP_KILL, this::onPlayerPvpKill, this));
    }

    public void reload() {
        try {
            rewards = null;
            rewards = Json.fromJson(new File(Configuration.server().getDatapackRoot() + "/data/custom/reward/reward.json"), Rewards.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onPlayerPvpKill(IBaseEvent event) {
        OnPlayerPvPKill pvpKill = (OnPlayerPvPKill) event;
        L2PcInstance player = pvpKill.getActiveChar();
        long clanMembersAroundPlayer = 0;
        int range = 5000;
        if (player.getClan() != null) {
            List<L2PcInstance> clanMembers = player.getClan().getOnlineMembers(player.getObjectId());
            clanMembersAroundPlayer = clanMembers.stream().filter(m -> player.calculateDistance(m.getLocation(), false, false) <= range).count();
        }
        double decreaseReward = 0.4 * (clanMembersAroundPlayer * clanMembersAroundPlayer);
        decreaseReward /= 100;
        if (decreaseReward >= 1) {
            decreaseReward = Configuration.customs().getPvPMaxRewardDecreaseInPercent();
        }
        double finalDecreaseReward = decreaseReward;
        rewards.getReward().get("pvp").items.stream().filter(item -> item.chance == 0 || item.chance >= rand.nextInt(100)).forEach(item -> player.addItem("Reward", item.id, (long) (item.count - (item.count * finalDecreaseReward)), 0, null, true));
    }

    public void rewardPlayer(L2PcInstance player, String rewardType) {
        if (!rewards.getReward().containsKey(rewardType)) {
            return;
        }
        rewards.getReward().get(rewardType).items.stream().filter(item -> item.chance == 0 || item.chance >= rand.nextInt(100)).forEach(item -> player.addItem("Reward", item.id, item.count, 0, null, true));
    }

    public void rewardPlayerWithMail(int id, String rewardType, String title, String content) {
        if (!rewards.getReward().containsKey(rewardType)) {
            return;
        }
        Message msg = new Message(id, title, content, Message.SendBySystem.NEWS);
        rewards.getReward().get(rewardType).items.stream().limit(8).forEach(item -> {
            Objects.requireNonNull(msg.createAttachments()).addItem("Reward", item.id, item.count, -1, null, null);
        });
        MailManager.getInstance().sendMessage(msg);
    }

    public static RewardManager getInstance() {
        return RewardManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final RewardManager instance = new RewardManager();
    }

    public static void main(String[] args) throws IOException {
        RewardManager.getInstance();
    }
}
