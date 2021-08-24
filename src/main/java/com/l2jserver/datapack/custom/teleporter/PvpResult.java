package com.l2jserver.datapack.custom.teleporter;

import com.l2jserver.gameserver.model.holders.Participant;
import com.l2jserver.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.List;

public class PvpResult extends L2GameServerPacket {
	private static int id = 25;
	private List<Participant> participants;

	public PvpResult(List<Participant> participants) {
		this.participants = participants;
	}

	@Override protected void writeImpl() {
//		List<Participant> players = new ArrayList<>();
		//		IntStream.range(0, 100).forEach(i -> players.add(new Participant(player, i)));
		//		player.sendPacket(new PvpResult(players));
		//		player.sendPacket(new ExShowScreenMessage(null, 0));
//		writeC(0xFE);
//		writeH(id++);
//		//		writeD(11);
//		System.out.println(id);
		// 16 big message on screen
		// 36 current seed manor
		// 39 seed on sale
		//44 crop purchase
		///138 open
		//139 close

		//		writeC(0xFE);
		//		writeH(0x89);
		//		writeD(0);
		//		writeD(participants.size());
		//
		//		participants.forEach(p -> {
		//			writeS(p.getPlayer().getName());
		//			writeD(p.getPoints());
		//		});

	}
}
