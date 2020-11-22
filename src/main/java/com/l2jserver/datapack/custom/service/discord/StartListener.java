/*
 * Copyright © 2004-2020 L2J DataPack
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
package com.l2jserver.datapack.custom.service.discord;

import static com.l2jserver.gameserver.config.Configuration.discord;

import java.awt.Color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.model.L2World;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Basic command Listener
 * @author Stalitsa
 * @version 2.6.2.0
 */
public class StartListener extends ListenerAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(DiscordBot.class);
	
	@Override
	public void onReady(ReadyEvent event) {
		LOG.info("Joined Guilds: " + event.getGuildTotalCount());
	}
	
	@Override
	public void onDisconnect(DisconnectEvent event) {
		if (event.isClosedByServer()) {
			LOG.info(event.getJDA().getSelfUser().getName() + " disconnected (closed by the server) with code: " + event.getServiceCloseFrame().getCloseCode() + " " + event.getCloseCode());
		}
	}
	
	@Override
	public void onReconnect(ReconnectedEvent event) {
		LOG.info(event.getJDA().getSelfUser().getName() + " has reconnected.");
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (event.getAuthor().isBot()) {
			return;
		}
		
		final int playersCount = L2World.getInstance().getAllPlayersCount();
		final int gmCount = AdminData.getInstance().getAllGms(true).size();
		// Basic command that the bot listens to and responds in an embed with online players and Gms
		if (event.getMessage().getContentRaw().startsWith(discord().getPrefix() + "online")) {
			EmbedBuilder eb = new EmbedBuilder().setColor(Color.CYAN);
			eb.setTitle(event.getAuthor().getName());
			eb.addField("Online Players", String.valueOf(playersCount), false);
			eb.addBlankField(false);
			eb.addField("Online GM's", String.valueOf(gmCount), false);
			event.getChannel().sendMessage(eb.build()).queue(); // this actually sends the information to discord.
		}
	}
}
