package me.yangbajing.core.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import me.yangbajing.core.exception.JacksonException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Yang Jing <a href="mailto:yang.xunjing@qq.com">yangbajing</a>
 * @date 2020-12-03 17:18:16
 */
public final class JsonUtils {
    public final static ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .setTimeZone(TimeZone.getTimeZone("Asia/Chongqing"))
//            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
            .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);

    public final static ObjectMapper objectMapperNotNull = objectMapper.copy()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonUtils() {
    }

    public static byte[] writeValueAsBytes(Object value) {
        try {
            if (value instanceof String) {
                JsonNode node = readTree((String) value);
                return objectMapper.writeValueAsBytes(node);
            }
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new JacksonException(e);
        }
    }

    public static String writeValueAsString(Object value) {
        try {
            if (value instanceof String) {
                String text = (String) value;
                JsonNode node = readTree(text);
                return objectMapper.writeValueAsString(node);
            }
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new JacksonException(e);
        }
    }

    public static <T> T readValue(String value, Class<T> valueType) {
        try {
            return objectMapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new JacksonException(String.format("Read [%s] error.", value), e);
        }
    }

    public static <T> T readValue(byte[] value, Class<T> valueType) {
        try {
            return objectMapper.readValue(value, valueType);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static <T> T readValue(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static JsonNode readTree(String text) {
        try {
            return objectMapper.readTree(text);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static <T> T treeToValue(TreeNode node, Class<T> valueType) {
        try {
            return objectMapper.treeToValue(node, valueType);
        } catch (JsonProcessingException e) {
            throw new JacksonException(e);
        }
    }

    public static JsonNode readTree(byte[] bytes) {
        try {
            return objectMapper.readTree(bytes);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static JsonNode readTree(InputStream in) {
        try {
            return objectMapper.readTree(in);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static ObjectNode readObjectNode(String text) {
        return (ObjectNode) readTree(text);
    }

    public static ArrayNode readArrayNode(String text) {
        return (ArrayNode) readTree(text);
    }

    public static JsonNode readTree(URL in) {
        try {
            return objectMapper.readTree(in);
        } catch (IOException e) {
            throw new JacksonException(e);
        }
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    public static JsonNode valueToTree(Object obj) {
        return objectMapper.valueToTree(obj);
    }

    public static String stringify(Object data) {
        return writeValueAsString(data);
    }

    public static String getString(JsonNode node) {
        return Objects.isNull(node) ? null : node.asText();
    }

    public static String getString(JsonNode node, String deft) {
        return Objects.isNull(node) ? deft : node.asText(deft);
    }

    public static Long getLong(JsonNode node) {
        return Objects.isNull(node) ? null : node.asLong();
    }

    public static Long getLong(JsonNode node, Long deft) {
        return Objects.isNull(node) ? deft : node.asLong(deft);
    }

    public static Integer getInteger(JsonNode node) {
        return Objects.isNull(node) ? null : node.asInt();
    }

    public static Integer getInteger(JsonNode node, Integer deft) {
        return Objects.isNull(node) ? deft : node.asInt(deft);
    }

    public static JsonNode objToJsonNode(Object obj) {
        return objToJsonNode(objectMapper, obj);
    }

    @SneakyThrows
    public static JsonNode objToJsonNode(ObjectMapper objectMapper, Object obj) {
        if (obj instanceof JsonNode) {
            return (JsonNode) obj;
        } else if (obj instanceof String) {
            return objectMapper.readTree((String) obj);
        } else {
            return objectMapper.valueToTree(obj);
        }
    }

}
