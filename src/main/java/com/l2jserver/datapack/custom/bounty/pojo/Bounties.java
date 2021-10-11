package com.l2jserver.datapack.custom.bounty.pojo;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Bounties {

    private final Map<String, Bounty> bounties;

    public Map<String, Bounty> getBounties() {
        return bounties;
    }

    @ConstructorProperties("bounties")
    public Bounties(List<Bounty> bounties) {
        Objects.requireNonNull(bounties);
        this.bounties = new HashMap<>();
        bounties.forEach(b -> {
            this.bounties.put(b.getType(), b);
        });
    }

}
