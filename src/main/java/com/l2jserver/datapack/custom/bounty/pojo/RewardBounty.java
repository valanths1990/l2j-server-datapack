package com.l2jserver.datapack.custom.bounty.pojo;

import java.beans.ConstructorProperties;

public class RewardBounty {

    private int id;
    private int count;
    private Boolean multiply;

    @ConstructorProperties({"id", "count", "multiply"})
    public RewardBounty(int id, int count, Boolean multiply) {
        this.id = id;
        this.count = count;
        this.multiply = multiply == null || multiply;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isMultiply() {
        return multiply;
    }

    public void setMultiply(boolean multiply) {
        this.multiply = multiply;
    }

}
