/*
 * Copyright (c) 2021 iTopZ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.l2jserver.datapack.votesystem.donate;

import com.l2jserver.commons.database.ConnectionFactory;
import com.l2jserver.datapack.votesystem.gui.Gui;
import com.l2jserver.datapack.votesystem.util.Utilities;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.L2World;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.model.items.L2Item;
import com.l2jserver.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author Nightwolf
 * iToPz Discord: https://discord.gg/KkPms6B5aE
 * @Author Rationale
 * Base structure credits goes on Rationale Discord: Rationale#7773
 * <p>
 * Vote Donation System
 * Script website: https://itopz.com/
 * Script version: 1.3
 * Pack Support: L2JServer
 * <p>
 * Personal Donate Panels: https://www.denart-designs.com/
 * Free Donate panel: https://itopz.com/
 */
public class DonateTaskManager implements Runnable
{
	// logger
	private static final Logger LOG = LoggerFactory.getLogger(DonateTaskManager.class);

	private final String DELETE = "DELETE FROM donate_holder WHERE no=? LIMIT 1";
	private final String SELECT = "SELECT no, id, count, playername FROM donate_holder";

	@Override
	public void run()
	{
		start();
	}

	/**
	 * reward player if donation is received
	 */
	private void start()
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(SELECT);
		     ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final L2PcInstance player = L2World.getInstance().getPlayer(rset.getString("playername"));
				final int no = rset.getInt("no");
				final int id = rset.getInt("id");
				final int count = rset.getInt("count");

				Optional.ofNullable(player).ifPresent(s ->
				{
					if (removeDonation(no))
					{
						final L2Item item = ItemTable.getInstance().getTemplate(id);

						if (Objects.nonNull(item))
						{
							Gui.getInstance().ConsoleWrite("Donation: " + player.getName() + " received " + count + "x " + item.getName());
							player.addItem("Donation", id, count, player, true);
							player.sendPacket(ActionFailed.STATIC_PACKET);
						}
					}
				});
			}
		} catch (final Exception e)
		{
			String error = e.getMessage();
			LOG.warn("Check donate items failed. " + error);

			if (error.contains("doesn't exist") && error.contains("donate_holder"))
			{
				Utilities.deleteTable(Utilities.DELETE_DONATE_TABLE, "Donate");
				Utilities.createTable(Utilities.CREATE_DONATE_TABLE, "Donate");
			}
		}
	}

	/**
	 * Remove donation from database
	 *
	 * @param id int
	 * @return boolean
	 */
	private boolean removeDonation(int id)
	{
		try (Connection con = ConnectionFactory.getInstance().getConnection();
		     PreparedStatement statement = con.prepareStatement(DELETE))
		{
			statement.setInt(1, id);
			statement.execute();
			return true;
		} catch (SQLException e)
		{
			LOG.warn("Failed to remove donation from database of donation id: " + id);
			LOG.warn(e.getMessage());
		}

		return false;
	}
}