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
package com.l2jserver.datapack.custom.service.discord.commands;

import com.l2jserver.datapack.custom.service.discord.AbstractCommand;
import com.l2jserver.gameserver.data.xml.impl.AdminData;
import com.l2jserver.gameserver.model.L2World;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Online Command.
 * @author Stalitsa
 * @version 2.6.2.0
 */
public class OnlineCommand extends AbstractCommand {

    @Override
    public List<String> getCommands() {
        List<String> commands = new ArrayList<>();
        commands.add("online");
        commands.add("on");
        return commands;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event, String[] args, String prefix) {

        if(args.length > 1) {
            event.getTextChannel().sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription("Please  use the command without any Arguments").build()).queue();
            event.getMessage().addReaction("\u274C").queue(); // Bot reacts with X mark.
            return;
        }
        event.getMessage().addReaction("\u2705").queue(); // Bot reacts with check mark.
        final int playersCount = L2World.getInstance().getAllPlayersCount();
        final int gmCount = AdminData.getInstance().getAllGms(true).size();
        // A command that the bot listens to and responds in an embed with online players and Gms
        EmbedBuilder eb = new EmbedBuilder().setColor(Color.CYAN);
        eb.setTitle(event.getAuthor().getName(), event.getAuthor().getEffectiveAvatarUrl());
        eb.setDescription("***___GAME INFO___***");
        eb.addField("Online Players", String.valueOf(playersCount), false);
        eb.addBlankField(false);
        eb.addField("Online GM's", String.valueOf(gmCount), false);
        event.getChannel().sendMessage(eb.build()).queue(); // this actually sends the information to discord.
    }
}
