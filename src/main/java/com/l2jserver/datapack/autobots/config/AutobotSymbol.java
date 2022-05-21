package com.l2jserver.datapack.autobots.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.l2jserver.gameserver.model.base.ClassId;

import java.util.Arrays;

public class AutobotSymbol {
    private final int[] symbolIds;
    private final ClassId classId;
    private final String symbols;

    public AutobotSymbol(@JsonProperty("classId")ClassId classId, @JsonProperty("symbols")String symbols) {
        this.classId = classId;
        this.symbols = symbols;
        symbolIds = Arrays.stream(symbols.split(";")).mapToInt(Integer::parseInt).toArray();
    }

    public int[] getSymbolIds() {
        return symbolIds;
    }

    public ClassId getClassId() {
        return classId;
    }

    public String getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return "AutobotSymbol{" +
                "symbolIds=" + Arrays.toString(symbolIds) +
                ", classId=" + classId +
                ", symbols='" + symbols + '\'' +
                '}';
    }
}

