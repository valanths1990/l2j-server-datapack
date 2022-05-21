package com.l2jserver.datapack.autobots;

import com.l2jserver.commons.util.Rnd;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.data.sql.impl.CharNameTable;
import com.l2jserver.gameserver.data.xml.impl.NpcData;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import static com.l2jserver.gameserver.config.Configuration.character;

public class AutobotNameService {

    private final List<String> fakePlayerNames = new ArrayList<>();

    public AutobotNameService() {
        loadWordlist();
    }

    private void loadWordlist() {
        fakePlayerNames.clear();
        try (LineNumberReader reader = new LineNumberReader(new FileReader(Configuration.server().getDatapackRoot() + "/data/autobots/fakenamewordlist.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.startsWith("#")) {
                    fakePlayerNames.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getRandomAvailableName() {
        String name = getRandomNameFromWordlist();

        while (!nameIsValid(name)) {
            name = getRandomNameFromWordlist();
        }

        return name;
    }

    private String getRandomNameFromWordlist() {
        return fakePlayerNames.get(Rnd.get( fakePlayerNames.size()));
    }

    public boolean nameAlreadyExists(String name) {
        return CharNameTable.getInstance().doesCharNameExist(name);
    }

    public boolean nameIsValid(String name) {
        if (!character().getPlayerNameTemplate().matcher(name).matches()) {
            return false;
        }

        if (NpcData.getInstance().getTemplateByName(name) != null) {
            return false;
        }

        return !nameAlreadyExists(name);
    }

    public static AutobotNameService getInstance() {
        return AutobotNameService.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static final AutobotNameService INSTANCE = new AutobotNameService();
    }

}
