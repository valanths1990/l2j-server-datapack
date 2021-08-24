package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;

public abstract class ListenerEvent {

    private boolean mCanceled;

    public abstract ListenerType getType();

    public boolean isCanceled() {
        return mCanceled;
    }

    public void setCancel(boolean value) {
        mCanceled = value;
    }
}
