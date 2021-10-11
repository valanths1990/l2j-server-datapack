package com.l2jserver.datapack.custom.achievement.pojo;

import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;

import java.beans.ConstructorProperties;
import java.util.Optional;

public class ItemPojo implements IRewardOperation {

    private final int id;
    private final long amount;
    private final int enchant;

    @ConstructorProperties({"id", "amount", "enchant"})
    public ItemPojo(int id, long amount, int enchant) {
        this.id = id;
        this.amount = amount;
        this.enchant = enchant;
    }

    @Override
    public String toString() {
        return "ItemPojo [amount=" + amount + ", enchant=" + enchant + ", id=" + id + "]";
    }

    @Override
    public void executeOperation(L2PcInstance pc) {
        pc.addItem("Achievement", id, amount, enchant, null, true);
    }

    @Override
    public String getRewardIcon() {
        return Optional.ofNullable(ItemTable.getInstance().getTemplate(id)).map(L2Item::getIcon).orElse("");
    }

    @Override
    public Long getCount() {
        return amount;
    }
}
