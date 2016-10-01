package org.tamal.deviceinformation;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

    private static final String TAG = "Utils";

    private Utils() {
    }

    public static <T> Map<String, T> findConstants(Class<?> classType, @Nullable Class<T> fieldType, @Nullable String regex) {
        Map<String, T> map = new HashMap<>();
        Pattern pattern = regex == null ? null : Pattern.compile(regex);
        for (Field field : classType.getDeclaredFields()) {
            boolean isPublic = Modifier.isPublic(field.getModifiers());
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            boolean isFinal = Modifier.isFinal(field.getModifiers());
            if (!isPublic || !isStatic || !isFinal) {
                continue;
            }
            if (fieldType != null && field.getType() != fieldType) {
                continue;
            }
            String name = field.getName();
            if (pattern != null) {
                Matcher matcher = pattern.matcher(name);
                if (!matcher.find()) {
                    continue;
                }
                if (matcher.groupCount() == 1) {
                    name = matcher.group(1);
                }
            }
            try {
                map.put(name, (T) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public static Map<String, Object> findProperties(Object object) {
        Map<String, Object> map = new HashMap<>();
        for (Method method : object.getClass().getMethods()) {
            boolean isPublic = Modifier.isPublic(method.getModifiers());
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (!isPublic || isStatic || method.getParameterTypes().length != 0
                    || Object.class == method.getDeclaringClass()) {
                continue;
            }
            String name = method.getName();
            if (name.startsWith("is")) {
                name = name.substring(2);
            } else if (name.startsWith("get")) {
                name = name.substring(3);
            } else {
                continue;
            }
            try {
                Object value = method.invoke(object);
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                map.put(name, value);
            } catch (IllegalAccessException e) {
                Log.d(TAG, e.getMessage());
            } catch (InvocationTargetException e) {
                Log.e(TAG, e.getMessage());
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

    public static String toString(Object obj) {
        return toString(obj, null, null, null, null);
    }

    public static String toString(Object obj, String separator, String start, String end, String keyValSep) {
        if (obj == null) {
            return "null";
        }
        if (separator == null) {
            separator = ", ";
        }
        if (start == null) {
            start = "[";
        }
        if (end == null) {
            end = "]";
        }
        if (keyValSep == null) {
            keyValSep = ":";
        }
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                String val = toString(Array.get(obj, i));
                sb.append(separator).append(val);
            }
            if (sb.length() == 0) {
                sb.insert(0, start);
            } else {
                sb.replace(0, separator.length(), start);
            }
            sb.append(end);
            return sb.toString();
        }
        if (obj instanceof Collection) {
            Collection<?> collection = (Collection<?>) obj;
            StringBuilder sb = new StringBuilder();
            for (Object item : collection) {
                String val = toString(item);
                sb.append(separator).append(val);
            }
            if (sb.length() == 0) {
                sb.insert(0, start);
            } else {
                sb.replace(0, separator.length(), start);
            }
            sb.append(end);
            return sb.toString();
        }
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = toString(entry.getKey());
                String value = toString(entry.getValue());
                sb.append(separator).append(key).append(keyValSep).append(value);
            }
            if (sb.length() == 0) {
                sb.insert(0, start);
            } else {
                sb.replace(0, separator.length(), start);
            }
            sb.append(end);
            return sb.toString();
        }

        try {
            if (obj.getClass().getMethod("toString").getDeclaringClass() == Object.class) {
                return "";
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return String.valueOf(obj);
    }

}
