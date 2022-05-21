package com.l2jserver.datapack.autobots.ui;

import com.l2jserver.datapack.autobots.Autobot;
import com.l2jserver.datapack.autobots.autofarm.AutofarmManager;
import com.l2jserver.datapack.autobots.utils.Util;
import com.l2jserver.gameserver.network.serverpackets.TutorialCloseHtml;
import com.l2jserver.gameserver.network.serverpackets.TutorialShowHtml;

public class AutofarmUi {


    public static void index(Autobot player) {
        String html = Util.readFileText("html/views/autofarm_main.htv")
                .replace("{{startstopcmd}}", AutofarmManager.getInstance().isAutoFarming(player) ? "stop" : "start")
                .replace("{{startstoptext}}", AutofarmManager.getInstance().isAutoFarming(player) ? "Stop" : "Start")
                .replace("{{closebtn}}", AutofarmManager.getInstance().isAutoFarming(player) ? "" : "<button action=\"bypass autofarm close\" value=\"Close\" width=74 height=21 back=\"L2UI_ct1.button_df_down\" fore=\"L2UI_ct1.button_df\"><br>");
        sendQuestWindow(player, html);
    }

    public static void closeWindow(Autobot player) {
        player.sendPacket(TutorialCloseHtml.STATIC_PACKET);
    }

    public static void sendQuestWindow(Autobot player, String text) {
        player.sendPacket(new TutorialShowHtml(text));
    }
}
