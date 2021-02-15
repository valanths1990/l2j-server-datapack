/*
 * Copyright © 2004-2021 L2J DataPack
 *
 * This file is part of L2J DataPack.
 *
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.datapack.custom.service.discord.listeners;

import com.l2jserver.datapack.custom.service.discord.DiscordBot;
import com.l2jserver.gameserver.model.events.Containers;
import com.l2jserver.gameserver.model.events.EventType;
import com.l2jserver.gameserver.model.events.impl.character.player.OnPlayerChat;
import com.l2jserver.gameserver.model.events.listeners.ConsumerEventListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;

import static com.l2jserver.gameserver.config.Configuration.discord;

/**
 * Chat Listener
 * @author Stalitsa
 * @version 2.6.2.0
 */
public class ChatListener extends ListenerAdapter {
    
    public ChatListener() {
        Containers.Global().addListener(new ConsumerEventListener(Containers.Global(), EventType.ON_PLAYER_CHAT, (OnPlayerChat event) -> {
            EmbedBuilder eb = new EmbedBuilder();
            String type = switch (event.getChatType()) {
                case 1 -> "Shout";
                case 8 -> "Trade";
                case 17 -> "Hero";
                default -> null;
            };
            if (type != null) {
                eb.setColor(Color.CYAN);
                eb.setTitle("***___" + event.getActiveChar().getName() + "___***");
                eb.setDescription("**" + type + ":** \n ``" + event.getText() + "``");
                DiscordBot.sendMessageTo(eb, discord().getGameChatChannelId());
            }
        }, this));
    }
}
