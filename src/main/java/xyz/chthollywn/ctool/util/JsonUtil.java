package xyz.chthollywn.ctool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
public class JsonUtil {
    // 单例 objectMapper
    private static ObjectMapper objectMapper;

    private static synchronized ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            // 当遇到未知属性时是否抛出 UnrecognizedPropertyException
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return objectMapper;
    }

    /**
     * 将对象序列化为JSON字符串
     * @param obj 需要序列化的对象
     */
    public static String toJson(Object obj) {
        try {
            ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON string " + e);
        }
    }

    /**
     * 将JSON字符串反序列化为对象
     * @param json JSON字符串
     * @param objClass 反序列化的对象类型
     */
    public static <T> T toObj(String json, Class<T> objClass) {
        _assertNotNull(json);

        try {
            ObjectMapper objectMapper = getObjectMapper();
            return objectMapper.readValue(json, objClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to Object " + e);
        }
    }

    /**
     * 将JSON字符串反序列化为List
     * @param json JSON字符串
     * @param elementClass List内元素的类型
     */
    public static <T> List<T> toList(String json, Class<T> elementClass) {
        _assertNotNull(json);

        try {
            ObjectMapper objectMapper = getObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(json, typeFactory.constructCollectionType(ArrayList.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to List " + e);
        }
    }

    /**
     * 将JSON字符串反序列化为Map
     * @param json JSON字符串
     * @param keyClass Map的key类型
     * @param valueClass Map的value类型
     */
    public static <K, V> LinkedHashMap<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        _assertNotNull(json);

        try {
            ObjectMapper objectMapper = getObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            return objectMapper.readValue(json, typeFactory.constructMapType(LinkedHashMap.class, keyClass, valueClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON string to Map " + e);
        }
    }

    private static void _assertNotNull(Object src) {
        if (src == null) {
            throw new IllegalArgumentException(String.format("argument \"%s\" is null", "json"));
        }
    }
}
