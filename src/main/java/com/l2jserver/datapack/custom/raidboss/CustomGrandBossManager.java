package com.l2jserver.datapack.custom.raidboss;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.datapack.instances.Antharas.Antharas;
import com.l2jserver.datapack.instances.Baium.Baium;
import com.l2jserver.datapack.instances.Beleth.Beleth;
import com.l2jserver.datapack.instances.GrandBossInstance;
import com.l2jserver.datapack.instances.QueenAnt.QueenAnt;
import com.l2jserver.datapack.instances.Valakas.Valakas;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.drops.DropListScope;
import com.l2jserver.gameserver.model.drops.GeneralDropItem;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.OnGrandBossKill;
import com.l2jserver.gameserver.model.events.listeners.AbstractEventListener;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.model.zone.type.L2NoRestartZone;
import com.l2jserver.gameserver.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.l2jserver.gameserver.config.Configuration.general;

public class CustomGrandBossManager {
    private static final List<Class<? extends GrandBossInstance>> raidbosses = new ArrayList<>();
    private Class<? extends GrandBossInstance> currentGrandBossClass = null;
    private GrandBossInstance grandBossInstance = null;
    private final RaidbossHandler handler;
    private Status status = Status.UPCOMING;
    private ScheduledFuture<?> future;
    private AbstractEventListener onAttackableKill;

    static {
        raidbosses.add(Antharas.class);
        raidbosses.add(Baium.class);
        raidbosses.add(Valakas.class);
		raidbosses.add(QueenAnt.class);
		raidbosses.add(Beleth.class);
//        raidbosses.add()
//		raidbosses.add(Core.class);
//		raidbosses.add(Orfen.class);
        Collections.shuffle(raidbosses);

    }

    private CustomGrandBossManager() {
        scheduleNextGrandBoss();
        handler = new RaidbossHandler();
        BypassHandler.getInstance().registerHandler(handler);
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_GRANDBOSS_KILL, this::onGrandBossKill, this));
    }

    private void scheduleNextGrandBoss() {
//        clearCurrentBoss();
        if (grandBossInstance != null) {
            grandBossInstance.unload(true);
        }
        currentGrandBossClass = raidbosses.get(Rnd.get(raidbosses.size()));
//		ThreadPoolManager.getInstance().scheduleGeneral(this::spawnGrandBoss, getNextSpawnTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), TimeUnit.MILLISECONDS);
        future = ThreadPoolManager.getInstance().scheduleGeneral(this::spawnGrandBoss, 0, TimeUnit.SECONDS);
        status = Status.UPCOMING;
    }

    private void spawnGrandBoss() {
        try {
            grandBossInstance = currentGrandBossClass.getDeclaredConstructor().newInstance();
            status = Status.ALIVE;
            ThreadPoolManager.getInstance().scheduleGeneral(() -> grandBossInstance.unload(true), 2000, TimeUnit.SECONDS);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
//		future=	ThreadPoolManager.getInstance().scheduleGeneral(this::scheduleNextGrandBoss, Configuration.grandBoss().getGrandBossAliveTime(), TimeUnit.SECONDS);
        future = ThreadPoolManager.getInstance().scheduleGeneral(this::scheduleNextGrandBoss, 2100, TimeUnit.SECONDS);
    }

    private void onGrandBossKill(IBaseEvent event) {
        if (grandBossInstance != null && ((OnGrandBossKill) event).getBoss().getId() == grandBossInstance.getGrandBossId()) {
            status = Status.DEAD;
            if (future == null || future.isCancelled()) {
                return;
            }
            future.cancel(true);
            future = ThreadPoolManager.getInstance().scheduleGeneral(this::scheduleNextGrandBoss, general().getInstanceFinishTime(), TimeUnit.SECONDS);
        }
    }


    public boolean enterGrandBoss(L2PcInstance pc) {
        if (grandBossInstance == null) {
            return false;
        }
        String html = grandBossInstance.onAdvEvent("enter", null, pc);
        Util.sendHtml(pc, html);
        return false;
    }

    private LocalDateTime getNextSpawnTime() {
        Map<DayOfWeek, Integer> tmp = Configuration.grandBoss().getGrandbossSpawnTimes();
        Optional<DayOfWeek> foundDayOfWeek = tmp.keySet().stream().sorted().filter(d -> d.ordinal() >= LocalDate.now().getDayOfWeek().ordinal()).findFirst();
        int hourOfDay = foundDayOfWeek.map(tmp::get).orElse(20);
        int dayDiff = foundDayOfWeek.orElse(DayOfWeek.FRIDAY).ordinal() - LocalDateTime.now().getDayOfWeek().ordinal();
        int hourDiff = hourOfDay - LocalDateTime.now().getHour();
        if (dayDiff < 0 || hourDiff < 0) {
            dayDiff += 7;
        }
        return LocalDate.now().atTime(hourOfDay, 0).plusDays(dayDiff);
    }

    public Status getStatus() {
        return status;
    }

    public String getBossName() {
        return currentGrandBossClass.getSimpleName();
    }

    public String getConqueror() {
        if (grandBossInstance == null || grandBossInstance.getTower() == null || grandBossInstance.getTower().getCapturer() == null) {
            return "";
        }
        return grandBossInstance.getTower().isConquerorIsClan() ? grandBossInstance.getTower().getCapturer().getClan().getName() : grandBossInstance.getTower().getCapturer().getName();
    }

    public List<Location> getSpawns() {
        return grandBossInstance.getZone().map(L2NoRestartZone::getSpawns).orElse(Collections.emptyList());
    }

    public LocalDateTime getSpawningTime() {
        return this.getNextSpawnTime();
    }

    public List<L2Item> getDrops() {
        if (grandBossInstance == null) {
            return Collections.emptyList();
        }
        L2NpcTemplate template = NpcData.getInstance().getTemplate(grandBossInstance.getGrandBossId());
        return template.getDropList(DropListScope.DEATH).stream().filter(d -> d instanceof GeneralDropItem).map(d -> ItemTable.getInstance().getTemplate(((GeneralDropItem) d).getItemId())).collect(Collectors.toList());
    }

    public static CustomGrandBossManager getInstance() {
        return CustomGrandBossManager.SingletonHolder.instance;
    }

    private static class SingletonHolder {
        protected static final CustomGrandBossManager instance = new CustomGrandBossManager();
    }


    public static void main(String[] args) {
        CustomGrandBossManager.getInstance();
    }

}
