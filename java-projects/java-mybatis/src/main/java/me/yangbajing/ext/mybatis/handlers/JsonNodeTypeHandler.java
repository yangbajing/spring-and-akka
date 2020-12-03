package me.yangbajing.ext.mybatis.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import me.yangbajing.core.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@MappedTypes(JsonNode.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonNodeTypeHandler extends AbstractPgTypeHandler<JsonNode> {
    public JsonNodeTypeHandler(Class<JsonNode> rawClass) {
        super("jsonb", rawClass);
    }

    @Override
    protected String write(JsonNode value) {
        return JsonUtils.writeValueAsString(value);
    }

    @Override
    protected JsonNode parse(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        return JsonUtils.readValue(text, rawClass);
    }

}
