package com.l2jserver.datapack.eventengine.interfaces;

import com.l2jserver.datapack.eventengine.dispatcher.events.*;

public interface IListenerSubscriber {

	void listenerOnLogin(OnLogInEvent event);

	void listenerOnLogout(OnLogOutEvent event);

	void listenerOnInteract(OnInteractEvent event);

	void listenerOnKill(OnKillEvent event);

	void listenerOnDeath(OnDeathEvent event);

	void listenerOnAttack(OnAttackEvent event);

	void listenerOnUseSkill(OnUseSkillEvent event);

	void listenerOnUseItem(OnUseItemEvent event);

	void listenerOnPlayableHit(OnPlayableHitEvent event);

	void listenerOnUnequipItem(OnUnequipItem event);

	void listenerOnUseTeleport(OnUseTeleport event);

	void listenerOnTowerCaptured(OnTowerCapturedEvent event);

	void listenerOnDoorAction(OnDoorActionEvent event);

	void listenerOnDlgAnswer(OnDlgAnswer event);

	void listenerOnPlayerMove(OnPlayerMoveEvent event);

}
