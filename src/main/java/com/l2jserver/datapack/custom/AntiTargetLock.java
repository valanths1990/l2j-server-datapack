package com.l2jserver.datapack.custom;

import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.enums.IllegalActionPunishmentType;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogin;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerLogout;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTarget;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerTargetCancel;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AntiTargetLock {

    private final Map<L2PcInstance, List<Long>> playersAvgTargetingTime = new ConcurrentHashMap<>();
    private final Map<L2PcInstance, ScheduledFuture<?>> scheduledRemoveOfTime = new ConcurrentHashMap<>();
    private final Map<L2PcInstance, Long> playersTimeMeasuring = new ConcurrentHashMap<>();

    public AntiTargetLock() {
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGOUT, this::onPlayerLogout, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_LOGIN, this::onPlayerLogin, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_TARGET_CANCEL, this::onPlayerTargetCancel, this));
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_TARGET, this::onPlayerTarget, this));
    }

    public void onPlayerTargetCancel(IBaseEvent event) {
        playersTimeMeasuring.put(((OnPlayerTargetCancel) event).getTarget(), System.currentTimeMillis());
    }

    public void onPlayerTarget(IBaseEvent event) {
        L2PcInstance pc = ((OnPlayerTarget) event).getActiveChar();
        if (!playersTimeMeasuring.containsKey(pc)) {
            return;
        }
        Long passedTimeInMs = System.currentTimeMillis() - playersTimeMeasuring.get(pc);
        List<Long> tmpList = playersAvgTargetingTime.computeIfAbsent(pc, k -> new ArrayList<>(10));
        tmpList.add(passedTimeInMs);
        if (tmpList.size() < 5) {
            return;
        }
        LongSummaryStatistics statistics = tmpList.stream().mapToLong((x) -> x).summaryStatistics();
        if (statistics.getAverage() < 100 && statistics.getMin() < 100 && statistics.getMax() < 100) {
            Util.handleIllegalPlayerAction(pc, "Player seems to be using Target Lock", IllegalActionPunishmentType.BROADCAST);
        }
        System.out.println(statistics);
        tmpList.clear();
        playersTimeMeasuring.clear();
        playersAvgTargetingTime.clear();

    }

    public void onPlayerLogout(IBaseEvent event) {
        L2PcInstance pc = ((OnPlayerLogout) event).getActiveChar();
        ScheduledFuture<?> f = ThreadPoolManager.getInstance().scheduleGeneral(() -> playersAvgTargetingTime.remove(pc), 10, TimeUnit.MINUTES);
        scheduledRemoveOfTime.put(pc, f);
    }

    public void onPlayerLogin(IBaseEvent event) {
        L2PcInstance pc = ((OnPlayerLogin) event).getActiveChar();

        if (!scheduledRemoveOfTime.containsKey(pc)) {
            return;
        }
        ScheduledFuture<?> f = scheduledRemoveOfTime.get(pc);
        if (f.isDone()) {
            return;
        }
        f.cancel(true);
    }


    public static AntiTargetLock getInstance() {
        return AntiTargetLock.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final AntiTargetLock instance = new AntiTargetLock();
    }

    public static void main(String[] args) {
        AntiTargetLock.getInstance();
    }
}
