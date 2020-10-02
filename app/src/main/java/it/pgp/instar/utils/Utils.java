package it.pgp.instar.utils;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static Map mapOf(Object... keyValueItems) {
        if (keyValueItems.length % 2 != 0) {
            throw new IllegalArgumentException("Number of arguments must be even (key1, value1, ..., key_n, value_n)");
        }
        Map h = new HashMap<>();
        for (int i = 0; i < keyValueItems.length; i += 2) {
            h.put(keyValueItems[i], keyValueItems[i + 1]);
        }
        return h;
    }
}
