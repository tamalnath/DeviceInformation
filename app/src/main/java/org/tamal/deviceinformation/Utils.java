package org.tamal.deviceinformation;

import android.support.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

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

    public static <K, V> Map<V, K> reverseMap(Map<K, V> map) {
        Map<V, K> reverse = new HashMap<>(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            reverse.put(entry.getValue(), entry.getKey());
        }
        return reverse;
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultValue;
    }
}
