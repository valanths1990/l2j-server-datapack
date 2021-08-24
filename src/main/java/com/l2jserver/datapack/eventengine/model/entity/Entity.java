package com.l2jserver.datapack.eventengine.model.entity;

import java.util.Objects;

public class Entity {

    private final int mObjectId;

    public Entity(int objectId) {
        mObjectId = objectId;
    }

    public int getObjectId() {
        return mObjectId;
    }


    @Override public int hashCode() {
        return Objects.hash(mObjectId);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Entity entity = (Entity) o;
        return mObjectId == entity.mObjectId;
    }
}
