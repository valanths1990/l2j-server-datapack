package com.l2jserver.datapack.autobots.ui.states;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.behaviors.preferences.ActivityPreferences;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ViewStates {
    private final Map<Integer, Map<String, ViewState>> states = new HashMap<>();

    private ViewStates() {

    }

    public IndexViewState indexViewState(L2PcInstance player) {
        ensureStateExists(player, "IndexViewState", IndexViewState::new);
        deactivateStates(player, "IndexViewState");

        IndexViewState state = (IndexViewState) states.get(player.getObjectId()).get("IndexViewState");
        if (state == null) return null;
        state.setIsActive(true);
        return state;
    }

    private static BotDetailsViewState.ActivityEditAction translate(ActivityPreferences.ActivityType activityType) {
        return switch (activityType) {
            case None -> BotDetailsViewState.ActivityEditAction.None;
            case Uptime -> BotDetailsViewState.ActivityEditAction.Uptime;
            case Schedule -> BotDetailsViewState.ActivityEditAction.Schedule;
        };
    }

    public BotDetailsViewState botDetailsViewState(L2PcInstance player, Autobot bot) {
        ensureStateExists(player, "BotDetailsViewState", () -> {
            BotDetailsViewState.ActivityEditAction activityEditAction = translate(bot.getCombatBehavior().getActivityPreferences().getActivityType());
            return new BotDetailsViewState(bot, true, activityEditAction);
        });
        deactivateStates(player, "BotDetailsViewState");
        BotDetailsViewState state;
        state = (BotDetailsViewState) states.get(player.getObjectId()).get("BotDetailsViewState");
        if (state == null) {
            state = new BotDetailsViewState(bot, true, translate(bot.getCombatBehavior().getActivityPreferences().getActivityType()));
            states.get(player.getObjectId()).put("BotDetailsViewState", state);
        }
        state.setIsActive(true);
        if (!Objects.equals(state.getActiveBot().getName(), bot.getName())) {
            state.reset();
            state.setActiveBot(bot);
            state.setActivityEditAction(translate(bot.getCombatBehavior().getActivityPreferences().getActivityType()));
        } else {
            state.setActiveBot(bot);
        }
        return state;
    }

    public CreateBotViewState createBotViewState(L2PcInstance player) {
        ensureStateExists(player, "CreateBotViewState", () -> {
            ViewState state = new CreateBotViewState();
            state.setIsActive(false);
            return state;
        });
        deactivateStates(player, "CreateBotViewState");
        CreateBotViewState state = (CreateBotViewState) states.get(player.getObjectId()).get("CreateBotViewState");
        if (!state.isActive()) {
            state.reset();
        }
        state.setIsActive(true);
        return state;
    }

    public SettingsViewState settingsViewState(L2PcInstance player) {
        ensureStateExists(player, "SettingsViewSState", SettingsViewState::new);
        deactivateStates(player, "SettingsViewState");
        ViewState state = states.get(player.getObjectId()).get("SettingViewState");
        state.setIsActive(true);
        return (SettingsViewState) state;
    }

    public ViewState getActiveState(L2PcInstance player) {
        if (!states.containsKey(player.getObjectId())) {
            states.put(player.getObjectId(), new HashMap<>() {{
                put("IndexViewState", new IndexViewState());
                put("BotDetailsViewState", null);
                put("CreateBotViewState", new CreateBotViewState());
                put("SettingsViewState", new SettingsViewState());
            }});
        }
        return states.get(player.getObjectId()).values().stream().filter(s -> s != null && s.isActive()).findFirst().orElse(null);
    }

    private void ensureStateExists(L2PcInstance player, String name, Supplier<ViewState> item) {
        if (!states.containsKey(player.getObjectId())) {
            states.put(player.getObjectId(), new HashMap<>());
        }
        if (!states.get(player.getObjectId()).containsKey(name)) {
            states.get(player.getObjectId()).put(name, item.get());
        }
    }

    private void deactivateStates(L2PcInstance player, String exceptForName) {

        if (!states.containsKey(player.getObjectId())) {
            states.put(player.getObjectId(), new HashMap<>());
        }
        states.get(player.getObjectId()).entrySet().stream().filter(e -> e.getValue() != null && !Objects.equals(e.getKey(), exceptForName)).forEach(e -> e.getValue().setIsActive(false));
    }

    public static ViewStates getInstance() {
        return ViewStates.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final ViewStates INSTANCE = new ViewStates();
    }
}
