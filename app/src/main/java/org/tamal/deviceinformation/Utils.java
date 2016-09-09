package org.tamal.deviceinformation;

import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Utils {
    private Utils() {
    }

    public static <T> Map<String, T> findConstants(Class<?> classType, @Nullable Class<T> fieldType, @Nullable String regex) {
        Map<String, T> map = new HashMap<>();
        for (Field field : classType.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            if (!isStatic || !isFinal) {
                continue;
            }
            if (field.getType() != fieldType) {
                continue;
            }
            if (regex == null || field.getName().matches(regex)) {
                try {
                    map.put(field.getName(), (T) field.get(null));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return map;
    }

    public static <K, V> Set<K> findKeys(Map<K, V> map, V value) {
        Set<K> keys = new HashSet<>();
        for(Map.Entry<K, V> entry : map.entrySet()) {
            if (value == null) {
                if (entry.getValue() == null) {
                    keys.add(entry.getKey());
                }
            } else {
                if (value.equals(entry.getValue())) {
                    keys.add(entry.getKey());
                }
            }
        }
        return keys;
    }

    public static <K, V> K findKey(Map<K, V> map, V value, K defaultKey) {
        for (K key : findKeys(map, value)) {
            return key;
        }
        return defaultKey;
    }
}
