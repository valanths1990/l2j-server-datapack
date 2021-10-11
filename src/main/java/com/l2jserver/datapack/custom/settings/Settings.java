package com.l2jserver.datapack.custom.settings;

import com.l2jserver.gameserver.cache.HtmCache;
import com.l2jserver.gameserver.custom.skin.SkinManager;
import com.l2jserver.gameserver.custom.skin.Visibility;
import com.l2jserver.gameserver.handler.CommunityBoardHandler;
import com.l2jserver.gameserver.handler.IBypassHandler;
import com.l2jserver.gameserver.model.actor.L2Character;
import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;

import java.util.Arrays;

public class Settings implements IBypassHandler {
	private String[] COMMANDS = {
		"settings;homepage"
	};

	@Override public boolean useBypass(String command, L2PcInstance activeChar, L2Character bypassOrigin) {
		String resultHmtl = "";
		String []splitted =command.split(" ");
		if (splitted.length == 1) {
			resultHmtl = getHomepage(activeChar);
		}

		else if (splitted.length == 3){

			if(splitted[1].equals("visibility")){
					Visibility v = Visibility.valueOf(splitted[2]);
					changeVisibility(activeChar,v);
					resultHmtl = getHomepage(activeChar);
			}

		}


		CommunityBoardHandler.separateAndSend(resultHmtl, activeChar);
		return false;
	}
	private void changeVisibility(L2PcInstance pc,Visibility newVisibility ){
		SkinManager.getInstance().setVisibility(pc,newVisibility);
		pc.broadcastUserInfo();
	}
	private String getHomepage(L2PcInstance pc) {
		String resultHmtl = HtmCache.getInstance().getHtm(pc.getHtmlPrefix(), "data/custom/settings/homepage.html");
		Visibility currentV = SkinManager.getInstance().isEnabled(pc);
		resultHmtl = resultHmtl.replace("%current%", currentV.name());
		for (Visibility v : Visibility.values()) {
			resultHmtl = resultHmtl.replace("%" + v.name() + "%", v.name());
		}
		return resultHmtl;
	}

	@Override public String[] getBypassList() {
		return COMMANDS;
	}
}
