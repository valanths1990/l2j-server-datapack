package com.l2jserver.datapack.eventengine.model.instance;

public class ItemInstance {

    private final int mObjectId;

    public static ItemInstance newInstance(int objectId) {
        return new ItemInstance(objectId);
    }

    private ItemInstance(int objectId) {
        mObjectId = objectId;
    }

    public int getObjectId() {
        return mObjectId;
    }
}
