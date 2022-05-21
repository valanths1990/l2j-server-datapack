package com.l2jserver.datapack.autobots.utils;

import com.l2jserver.gameserver.custom.images.DDSConverter;
import com.l2jserver.gameserver.data.sql.impl.CrestTable;
import com.l2jserver.gameserver.idfactory.IdFactory;
import com.l2jserver.gameserver.model.L2Crest;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class CrestService {

    public static int uploadCret(String urlString, L2Crest.CrestType crestType) {
        try {
            InputStream input = new URL(urlString).openConnection().getInputStream();
            ByteBuffer buffer = DDSConverter.convertToDDS(input);
            if (buffer == null) return 0;
            buffer.position(0);
            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            arr[12] = 16;

            byte[] header = Arrays.copyOfRange(arr, 0, 128);
            byte[] middle = new byte[32];
            byte[] last = Arrays.copyOfRange(arr, 128, arr.length);
            arr = plus(header, plus(middle, last));

            L2Crest crestId = CrestTable.getInstance().createCrest(arr, crestType);
            assert crestId != null;
            return crestId.getId();
        } catch (Exception e) {
            return 0;
        }
    }

    private static byte[] plus(byte[] original, byte[] elements) {
        int thisSize = original.length;
        int arraySize = elements.length;
        byte[] result = java.util.Arrays.copyOf(original, thisSize + arraySize);
        System.arraycopy(elements, 0, result, thisSize, arraySize);
        return result;
    }
}
