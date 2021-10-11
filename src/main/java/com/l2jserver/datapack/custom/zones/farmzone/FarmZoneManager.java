package com.l2jserver.datapack.custom.zones.farmzone;

import com.l2jserver.datapack.ai.npc.ForgeOfTheGods.TarBeetleSpawn;
import com.l2jserver.datapack.custom.zones.AbstractTimeZone;
import com.l2jserver.gameserver.ThreadPoolManager;
import com.l2jserver.gameserver.ai.CtrlIntention;
import com.l2jserver.gameserver.ai.L2CharacterAI;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.datatables.SpawnTable;
import com.l2jserver.gameserver.handler.BypassHandler;
import com.l2jserver.gameserver.instancemanager.ZoneManager;
import com.l2jserver.gameserver.model.AggroInfo;
import com.l2jserver.gameserver.model.L2Spawn;
import com.l2jserver.gameserver.model.Location;
import com.l2jserver.gameserver.model.actor.L2Attackable;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.L2Playable;
import com.l2jserver.gameserver.model.actor.instance.L2CustomMonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2MonsterInstance;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.actor.templates.L2NpcTemplate;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.ListenerRegisterType;
import com.l2jserver.gameserver.model.events.annotations.*;
import com.l2jserver.gameserver.model.events.impl.IBaseEvent;
import com.l2jserver.gameserver.model.events.impl.character.OnCreatureKill;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.OnAttackableAttack;
import com.l2jserver.gameserver.model.events.impl.character.npc.attackable.OnAttackableKill;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import com.l2jserver.gameserver.model.holders.MinionHolder;
import com.l2jserver.gameserver.model.zone.L2ZoneType;
import com.l2jserver.gameserver.model.zone.form.ZoneCylinder;
import com.l2jserver.gameserver.model.zone.type.L2FarmZone;
import com.l2jserver.gameserver.taskmanager.Task;
import com.l2jserver.gameserver.util.Util;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FarmZoneManager extends AbstractTimeZone {
	private final L2NpcTemplate primaryRaidboss;//27413;
	private final L2NpcTemplate secondaryRaidboss; //27414;

	private static final Map<Integer, Integer[]> zoneMobIds = new HashMap<>();
	private final int attackRange = 2000;
	List<L2NpcTemplate> templates;
	List<L2MonsterInstance> spawnedMob = new CopyOnWriteArrayList<>();

	static {

		zoneMobIds.put(1, new Integer[] { 27412, 27405 });
		zoneMobIds.put(2, new Integer[] { 22544, 22541 });

	}

	private FarmZoneManager() {
		super();
		this.zoneTime = Configuration.customs().getFarmZoneTime();
		zones = new ArrayList<>(ZoneManager.getInstance().getAllZones(L2FarmZone.class));
		zones.forEach(z -> z.setEnabled(false));
		Collections.shuffle(zones);
		handler = new FarmZoneHandler();
		BypassHandler.getInstance().registerHandler(handler);
		templates = NpcData.getInstance().getAllNpcOfClassType("L2CustomMonster");
		primaryRaidboss = NpcData.getInstance().getTemplate(27413);
		secondaryRaidboss = NpcData.getInstance().getTemplate(27414);
		SpawnTable.getInstance().parseDatapackDirectory("data/custom/farmzone/custom", true);
		scheduleNewZone();
	}

	@Override protected void registerListeners() {
		super.registerListeners();
	}

	@Override protected void startNewZone() {
		super.startNewZone();
		Set<L2Spawn> spawns = Stream.of(zoneMobIds.get(currentZone.getId())).map(npcId -> SpawnTable.getInstance().getSpawns(npcId)).flatMap(Set::stream).collect(Collectors.toSet());
		spawns.forEach(this::spawnMobAndAddListener);
	}

	private void spawnMobAndAddListener(L2Spawn s) {
		L2MonsterInstance mob = (L2MonsterInstance) s.doSpawn();
		ConsumerEventListener listener = new ConsumerEventListener(Containers.Monsters(), EventType.ON_ATTACKABLE_KILL, this::onCreatureKill, this);
		mob.setOnKillDelay(100);
		mob.addListener(listener);
		spawnedMob.add(mob);
	}

	@Override protected void clearCurrentZone() {
		spawnedMob.forEach(L2MonsterInstance::deleteMe);
		spawnedMob.clear();
		super.clearCurrentZone();
	}

	private void onCreatureKill(IBaseEvent event) {
		OnAttackableKill onAttackableKill = (OnAttackableKill) event;

		if (onAttackableKill.getAttacker() == null || onAttackableKill.getTarget() == null) {
			return;
		}

		if (!(onAttackableKill.getTarget() instanceof L2CustomMonsterInstance)) {
			return;
		}
		L2MonsterInstance mob = (L2MonsterInstance) onAttackableKill.getTarget();
		if (Configuration.customs().getPrimaryRaidBossSpawnRate() > rand.nextInt(100)) {
			try {
				L2Spawn primarySpawn = new L2Spawn(primaryRaidboss);
				primarySpawn.setLocation(mob.getLocation());
				primarySpawn.setCustom(true);
				primarySpawn.setHeading(mob.getHeading());
				L2MonsterInstance primaryMonster = (L2MonsterInstance) primarySpawn.doSpawn();
				primaryMonster.setIsRaid(true);
				primaryMonster.getKnownList().addKnownObject(onAttackableKill.getAttacker());
				primaryMonster.addAttackerToAttackByList(onAttackableKill.getAttacker());
				primaryMonster.doAttack(onAttackableKill.getAttacker());
				primaryMonster.addListener(new ConsumerEventListener(Containers.Players(),EventType.ON_ATTACKABLE_ATTACK,this::onRaidAttack,this));
			} catch (ClassNotFoundException | NoSuchMethodException e) {
				e.printStackTrace();
			}
			return;
		}
		if (Configuration.customs().getSecondaryRaidBossRate() > rand.nextInt(100)) {
			try {
				L2Spawn secondarySpawn = new L2Spawn(secondaryRaidboss);
				secondarySpawn.setLocation(mob.getLocation());
				secondarySpawn.setCustom(true);
				secondarySpawn.setHeading(mob.getHeading());
				L2MonsterInstance secondaryMonster = (L2MonsterInstance) secondarySpawn.doSpawn();
				secondaryMonster.getKnownList().addKnownObject(onAttackableKill.getAttacker());
				secondaryMonster.addAttackerToAttackByList(onAttackableKill.getAttacker());
				secondaryMonster.doAttack(onAttackableKill.getAttacker());
			} catch (ClassNotFoundException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public void onRaidAttack(IBaseEvent event){

		OnAttackableAttack onAttackableAttack = (OnAttackableAttack) event;
		L2PcInstance attacker = onAttackableAttack.getAttacker();
		attacker.updatePvPStatus();

	}

	@Override protected void teleportPlayersOut() {
		if (currentZone == null) {
			return;
		}
		currentZone.getPlayersInside().forEach(p -> handler.useBypass("farmzone;homepage leave", p, null));
	}


	public static FarmZoneManager getInstance() {
		return FarmZoneManager.SingletonHolder.instance;
	}

	private static class SingletonHolder {
		protected static final FarmZoneManager instance = new FarmZoneManager();
	}

	public static void main(String[] args) {
		FarmZoneManager.getInstance();
	}

}
