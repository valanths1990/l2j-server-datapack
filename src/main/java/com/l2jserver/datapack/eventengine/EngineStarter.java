package com.l2jserver.datapack.eventengine;

import com.l2jserver.commons.util.Util;
import com.l2jserver.datapack.eventengine.eventsimpl.teamvsteam.config.TvTEventConfig;
import com.luksdlt92.winstonutils.GsonHelper;

import java.io.File;
import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.server;

public class EngineStarter {
	private static final String DATAPACK = "-dp";

	private static final String SCRIPT = "-s";

	public static void main(String[] args) {
//		final String datapackRoot = Util.parseArg(args, DATAPACK, true);
//		if (datapackRoot != null) {
//			server().setProperty("DatapackRoot", datapackRoot);
//		}
//
//		final String scriptRoot = Util.parseArg(args, SCRIPT, true);
//		if (scriptRoot != null) {
//			server().setProperty("ScriptRoot", scriptRoot);
//		}
		EventEngineManager.getInstance();
	}
}
