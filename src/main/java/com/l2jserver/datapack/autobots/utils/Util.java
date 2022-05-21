package com.l2jserver.datapack.autobots.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.l2jserver.gameserver.config.Configuration;
import com.l2jserver.gameserver.datatables.ItemTable;
import com.l2jserver.gameserver.model.items.type.CrystalType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Util {
    public static final ObjectMapper mapper;

    static {
        JacksonXmlModule jackson = new JacksonXmlModule();
        jackson.setDefaultUseWrapper(false);
        mapper = new XmlMapper(jackson);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true).
                configure(SerializationFeature.INDENT_OUTPUT, true);
    }


    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String readFileText(String fileName) {
        try {
            return Files.readString(Paths.get(Configuration.server().getDatapackRoot() + "/data/autobots/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getOrNull(String[] value, int n) {
        if (n >= 0 && n < value.length) return value[n];
        return null;
    }

    public static <T> T getOrNull(String[] value, int n, Class<T> type) {
        if (n >= 0 && n < value.length) return type.cast(value[n]);
        return null;
    }

    //    public static Optional<String> getOrElse(String[] value, int n) {
//        if (n >= 0 && n <= value.length) return Optional.of(value[n]);
//        return Optional.empty();
//    }
    public static <T> Optional<T> getOrElse(String[] value, int n, Class<T> type) {
        return getOrElse(Arrays.asList(value), n, type);
    }

    public static <T> Optional<T> getOrElse(List<String> value, int n, Class<T> type) {
        if (value == null || value.isEmpty() || n >= value.size()) return Optional.empty();

        if (Boolean.class == type) return Optional.of(type.cast(Boolean.parseBoolean(value.get(n))));
        if (Byte.class == type) return Optional.of(type.cast(Byte.parseByte(value.get(n))));
        if (Short.class == type) return Optional.of(type.cast(Short.parseShort(value.get(n))));
        if (Integer.class == type) return Optional.of(type.cast(Integer.parseInt(value.get(n))));
        if (Long.class == type) return Optional.of(type.cast(Long.parseLong(value.get(n))));
        if (Float.class == type) return Optional.of(type.cast(Float.parseFloat(value.get(n))));
        if (Double.class == type) return Optional.of(type.cast(Double.parseDouble(value.get(n))));
        if (String.class == type) return Optional.of(type.cast(value.get(n)));

        return Optional.empty();
    }

}
