package com.l2jserver.datapack.eventengine.dispatcher.events;

import com.l2jserver.datapack.eventengine.enums.ListenerType;
import com.l2jserver.datapack.eventengine.model.entity.Player;

public class OnDlgAnswer extends ListenerEvent {
    private final Player player;
    private final int answer;

    public OnDlgAnswer(Player player, int answer) {
        this.player = player;
        this.answer = answer;
    }

    public Player getPlayer() {
        return player;
    }

    public int getAnswer() {
        return answer;
    }

    @Override
    public ListenerType getType() {
        return ListenerType.ON_DLG_ANSWER;
    }
}
