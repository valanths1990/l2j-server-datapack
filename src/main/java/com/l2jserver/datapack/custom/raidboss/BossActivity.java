package com.l2jserver.datapack.custom.raidboss;

import com.l2jserver.gameserver.custom.Activity.IActivity;
import com.l2jserver.gameserver.custom.Activity.Priority;

public class BossActivity implements IActivity {
    @Override
    public String getBypass() {
        return "Bypass -h raidboss;homepage";
    }

    @Override
    public String getImage() {
        return "Crest.crest_%serverId%_%imageName:"+CustomGrandBossManager.getInstance().getBossName().toLowerCase()+"%";
    }

    @Override
    public String getName() {
        return CustomGrandBossManager.getInstance().getBossName();
    }

    @Override
    public Priority getPriority() {
        return Priority.VERY_HIGH;
    }
}
