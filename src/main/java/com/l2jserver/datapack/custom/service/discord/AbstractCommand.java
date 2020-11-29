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

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.discord;

/**
 * Abstract Command.
 * @author Stalitsa
 * @version 2.6.2.0
 */
public abstract class AbstractCommand extends ListenerAdapter {

    public abstract List<String> getCommands();

    public abstract void executeCommand(MessageReceivedEvent event, String[] args, String prefix);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.getChannelType().equals(ChannelType.PRIVATE))
        {
            return;
        }
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (isCommand(args, discord().getPrefix())) {
            executeCommand(event, args, discord().getPrefix());
        }
    }

    private boolean isCommand(String[] args, String prefix) {
        List<String> commands = new ArrayList<>();
        for (String cmd : getCommands()) {
            commands.add(prefix + cmd);
        }
        return commands.contains(args[0]);
    }

}
