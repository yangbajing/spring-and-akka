package me.yangbajing.ext.mybatis.extension.handlers;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yang Jing <a href="mailto://yang.xunjing@qq.com">yangbajing</a>
 * @since 2020-09-11 17:44
 */
public class IciyunMybatisEnumTypeHandler<E extends Enum<?>> extends BaseTypeHandler<Enum<?>> {

    private static ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    private static final Map<String, String> TABLE_METHOD_OF_ENUM_TYPES = new ConcurrentHashMap<>();

    private final Class<E> type;

    private Invoker invoker = null;

    public IciyunMybatisEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        MetaClass metaClass = MetaClass.forClass(type, reflectorFactory);
        String name = "value";
        if (!IEnum.class.isAssignableFrom(type)) {
            Optional<String> nameOptional = findEnumValueFieldName(this.type);
            if (!nameOptional.isPresent()) {
                nameOptional = findJsonValueFieldName(this.type);
            }
            nameOptional.ifPresent(s -> this.invoker = metaClass.getGetInvoker(s));
            //name = nameOptional.orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", this.type.getName())));
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Enum<?> parameter, JdbcType jdbcType)
            throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            // see r3589
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        if (null == rs.getObject(columnName) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        if (null == rs.getObject(columnIndex) && rs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, rs.getObject(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        if (null == cs.getObject(columnIndex) && cs.wasNull()) {
            return null;
        }
        return this.valueOf(this.type, cs.getObject(columnIndex));
    }

    /**
     * 查找标记标记EnumValue字段
     *
     * @param clazz class
     * @return EnumValue字段
     * @since 3.3.1
     */
    public static Optional<String> findEnumValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(className, key -> {
                Optional<Field> optional = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(EnumValue.class))
                        .findFirst();
                return optional.map(Field::getName).orElse(null);
            }));
        }
        return Optional.empty();
    }

    /**
     * 查找标记标记 {@link JsonValue} 字段
     */
    public static Optional<String> findJsonValueFieldName(Class<?> clazz) {
        if (clazz != null && clazz.isEnum()) {
            String className = clazz.getName();
            return Optional.ofNullable(TABLE_METHOD_OF_ENUM_TYPES.computeIfAbsent(className, key -> {
                Optional<Field> optional = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(JsonValue.class))
                        .findFirst();
                return optional.map(Field::getName).orElse(null);
            }));
        }
        return Optional.empty();
    }

    /**
     * 判断是否为MP枚举处理
     *
     * @param clazz class
     * @return 是否为MP枚举处理
     * @since 3.3.1
     */
    public static boolean isMpEnums(Class<?> clazz) {
        return clazz != null && clazz.isEnum() && (findJsonValueFieldName(clazz).isPresent() || IEnum.class.isAssignableFrom(clazz) || findEnumValueFieldName(clazz).isPresent());
    }

    private E valueOf(Class<E> enumClass, Object value) {
        E[] es = enumClass.getEnumConstants();
        return Arrays.stream(es).filter((e) -> equalsValue(value, getValue(e))).findAny().orElse(null);
    }

    /**
     * 值比较
     *
     * @param sourceValue 数据库字段值
     * @param targetValue 当前枚举属性值
     * @return 是否匹配
     * @since 3.3.0
     */
    protected boolean equalsValue(Object sourceValue, Object targetValue) {
        String sValue = Objects.toString(sourceValue);
        String tValue = Objects.toString(targetValue);
        if (sourceValue instanceof Number && targetValue instanceof Number
                && new BigDecimal(sValue).compareTo(new BigDecimal(tValue)) == 0) {
            return true;
        }
        return Objects.equals(sValue, tValue);
    }

    private Object getValue(Enum<?> object) {
        try {
            if (invoker == null) {
                return object.name();
            } else {
                return invoker.invoke(object, new Object[0]);
            }
        } catch (ReflectiveOperationException e) {
            throw ExceptionUtils.mpe(e);
        }
    }
}

