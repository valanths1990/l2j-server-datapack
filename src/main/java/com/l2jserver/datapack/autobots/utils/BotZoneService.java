package com.l2jserver.datapack.autobots.utils;

import com.l2jserver.gameserver.model.actor.instance.L2PcInstance;
import com.l2jserver.gameserver.network.serverpackets.ExServerPrimitive;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BotZoneService {
    public static L2PcInstance player = null;
    public static BotGraph graph = new BotGraph();

    public static void sendZone(L2PcInstance player) {
        ExServerPrimitive packet = new ExServerPrimitive(player.getName() + "_", player.getX(), player.getY(), -65535);

        for (int i = 0; i < graph.points.size(); i++) {
            BotZonePoint point = graph.points.get(i);
            packet.addPoint(i+"", point.color, point.isNameColored, point.x, point.y , point.z);
            if (i + 1 < graph.points.size()) {
                BotZonePoint nextPoint = graph.points.get(i + 1);
                packet.addLine(point.color, point.x, point.y, point.z, nextPoint.x, nextPoint.y, nextPoint.z);
            }
        }
        player.sendPacket(packet);
    }

    public static final class BotGraph {
        public List<BotZonePoint> points = new ArrayList<>();
    }

    public static final class BotZonePoint {
        public int x;
        public int y;
        public int z;
        Color color = Color.GREEN;
        boolean isNameColored = true;

        public BotZonePoint(int x, int y, int z, Color color, boolean isNameColored) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.color = color;
            this.isNameColored = isNameColored;
        }

        public BotZonePoint(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
