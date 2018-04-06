package com.k.common.persistent;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.k.common.persistent.annotation.SQLField;
import com.k.common.persistent.annotation.SQLTable;
import com.k.common.persistent.constants.MyBatisEnum;
import com.k.common.persistent.constants.Operator;
import com.k.common.persistent.dtos.BaseQueryParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SQL Joiner 2.0
 * 简化了内部实现, 减少了很多不必要的分支判断, 增加了拼接异常的可能性, 提早发现SQL问题
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SQLJoiner2 {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Option DEFAULT_OPTION = new Option();

    /**
     * order by
     */
    public static SQL joinOrderBy(SQL sql, String orderBy) {
        if (StringUtils.hasText(orderBy)) {
            sql.ORDER_BY(orderBy);
        }
        return sql;
    }

    /**
     * group by, having
     */
    public static SQL joinGroupByAndHaving(SQL sql, String groupBy, String having) {
        if (StringUtils.hasText(groupBy)) {
            sql.GROUP_BY(groupBy);
            if (StringUtils.hasText(having)) {
                sql.HAVING(having);
            }
        }
        return sql;
    }

    /**
     * eg:createTime >= ? AND createTime <= ?
     */
    public static SQL joinRangeTime(SQL sql, BaseQueryParam queryParam) {
        String startTimeField = queryParam.getStartTimeField();
        Date startTime = queryParam.getStartTime();
        if (StringUtils.hasText(startTimeField) && startTime != null) {
            sql.WHERE(startTimeField + "'" + FORMATTER.format(startTime) + "'");
        }
        String endTimeField = queryParam.getEndTimeField();
        Date endTime = queryParam.getEndTime();
        if (StringUtils.hasText(endTimeField) && endTime != null) {
            sql.WHERE(endTimeField + "'" + FORMATTER.format(endTime) + "'");
        }
        return sql;
    }

    /**
     * N合一……
     */
    public static SQL joinAllInOne(SQL sql, BaseQueryParam queryParam) {
        joinOrderBy(sql, queryParam.getOrderBy());
        joinGroupByAndHaving(sql, queryParam.getGroupBy(), queryParam.getHaving());
        joinRangeTime(sql, queryParam);
        return sql;
    }

    public static void WHERE(SQL sql, Object obj) {
        WHERE(sql, obj, DEFAULT_OPTION);
    }

    public static void WHERE(SQL sql, Object obj, Option option) {
        if (sql == null || obj == null) {
            return;
        }
        if (option == null) {
            option = DEFAULT_OPTION;
        }
        SafeFunction<Object, String> objConverter = MoreObjects.firstNonNull(option.getObjConverter(), TO_ESCAPE_STRING);
        Predicate<Object> nullPredicater = MoreObjects.firstNonNull(option.getNullPredicater(), IS_NULL);
        Map<Field, Object> fieldMap = getFieldMap(obj, option);

        LinkedCaseInsensitiveMap<Operator> operatorTypeMap = null;
        if (!CollectionUtils.isEmpty(option.getOperatorTypeMap())) {
            operatorTypeMap = new LinkedCaseInsensitiveMap();
            operatorTypeMap.putAll(option.getOperatorTypeMap());
            filterByOperator(fieldMap, operatorTypeMap);
        }

        SQLTable sqlTable = AnnotationUtils.getAnnotation(obj.getClass(), SQLTable.class);
        for (Map.Entry<Field, Object> entry : fieldMap.entrySet()) {
            Field field = entry.getKey();
            Object value = entry.getValue();
            String fieldName = getFieldName(field);
            if (sqlTable != null && StringUtils.hasText(sqlTable.alias())) {
                fieldName = sqlTable.alias() + "." + fieldName;
            }
            Operator operator = Operator.IN;
            if (!CollectionUtils.isEmpty(operatorTypeMap)) {
                operator = operatorTypeMap.getOrDefault(field.getName(), Operator.IN);
                if (operator == Operator.IS_NULL || operator == Operator.IS_NOT_NULL) {
                    sql.WHERE(fieldName + operator.getOperator());
                    continue;
                }
            }
            if (nullPredicater.test(value)) {
                continue;
            }
            String finalValue = getValue(value, objConverter, nullPredicater);
            if (finalValue == null) {
                continue;
            }
            sql.WHERE(fieldName + operator.getOperator() + finalValue);
        }
    }

    public static void VALUES(SQL sql, Object obj) {
        VALUES(sql, obj, DEFAULT_OPTION);
    }

    public static void VALUES(SQL sql, Object obj, Option option) {
        if (sql == null || obj == null) {
            return;
        }
        if (option == null) {
            option = DEFAULT_OPTION;
        }
        SafeFunction<Object, String> objConverter = MoreObjects.firstNonNull(option.getObjConverter(), TO_ESCAPE_STRING);
        Predicate<Object> nullPredicater = MoreObjects.firstNonNull(option.getNullPredicater(), IS_NULL);
        Map<Field, Object> fieldMap = getFieldMap(obj, option);

        for (Map.Entry<Field, Object> entry : fieldMap.entrySet()) {
            Object value = entry.getValue();
            if (nullPredicater.test(value)) {
                continue;
            }
            Field field = entry.getKey();
            String fieldName = getFieldName(field);
            String finalValue = getValue(value, objConverter, nullPredicater);
            if (finalValue == null) {
                continue;
            }
            sql.VALUES(fieldName, finalValue);
        }
    }

    public static void SET(SQL sql, Object obj, boolean keepNull) {
        SET(sql, obj, keepNull, DEFAULT_OPTION);
    }

    public static void SET(SQL sql, Object obj, boolean keepNull, Option option) {
        if (sql == null || obj == null) {
            return;
        }
        if (option == null) {
            option = DEFAULT_OPTION;
        }
        SafeFunction<Object, String> objConverter = MoreObjects.firstNonNull(option.getObjConverter(), TO_ESCAPE_STRING);
        Predicate<Object> nullPredicater = MoreObjects.firstNonNull(option.getNullPredicater(), IS_NULL);
        Map<Field, Object> fieldMap = getFieldMap(obj, option);

        for (Map.Entry<Field, Object> entry : fieldMap.entrySet()) {
            Field field = entry.getKey();
            Object value = entry.getValue();
            String fieldName = getFieldName(field);
            String finalValue = getValue(value, objConverter, nullPredicater);
            if (finalValue == null && !keepNull) {
                continue;
            }
            sql.SET(fieldName + Operator.EQUAL.getOperator() + finalValue);
        }
    }

    /**
     * 当操作符为LIKE或REGEXP时, 如果fieldMap中的value是Collection,则只取第一个值作为匹配值
     * 注意:如果确定某字段使用LIKE或REGEXP,应确保传入的Collection有且只有一个对象,否则Set不一定能保证顺序
     */
    private static void filterByOperator(Map<Field, Object> fieldMap, LinkedCaseInsensitiveMap<Operator> operatorTypeMap) {
        fieldMap.forEach((field, value) -> {
            if (!(value instanceof Collection) || CollectionUtils.isEmpty((Collection<?>) value)) {
                return;
            }
            String fieldName = field.getName();
            if (!operatorTypeMap.containsKey(fieldName)) {
                return;
            }
            Operator operator = operatorTypeMap.get(fieldName);
            if (operator == Operator.LIKE || operator == Operator.REGEXP) {
                Object newValue = Iterables.getFirst(((Collection<?>) value), null);
                if (newValue != null) {
                    fieldMap.replace(field, newValue);
                }
            }
        });
    }

    private static String getFieldName(Field field) {
        SQLField sqlField = AnnotationUtils.getAnnotation(field, SQLField.class);
        if (sqlField != null && StringUtils.hasText(sqlField.value())) {
            return sqlField.value();
        }
        return field.getName();
    }

    private static Map<Field, Object> getFieldMap(Object obj, Option option) {
        Integer deep = option.getDeep();
        Set<String> excludeFields = option.getExcludeFields().stream().map(String::toUpperCase).collect(Collectors.toSet());
        Class clazz = obj.getClass();
        Map<Field, Object> fieldMap = Maps.newLinkedHashMap();
        for (int i = 0; i < deep; i++) {
            if (i > 0) {
                clazz = clazz.getSuperclass();
            }
            SQLTable sqlTable = AnnotationUtils.getAnnotation(clazz, SQLTable.class);
            if (sqlTable != null) {
                excludeFields.addAll(Stream.of(sqlTable.ignoredFields()).map(String::toUpperCase).collect(Collectors.toSet()));
            }
            Stream.of(clazz.getDeclaredFields())
                    .peek(ReflectionUtils::makeAccessible)
                    .filter(field -> !excludeFields.contains(field.getName().toUpperCase()))
                    .filter(field -> {
                        SQLField sqlField = AnnotationUtils.getAnnotation(field, SQLField.class);
                        return sqlField == null || !sqlField.ignored();
                    })
                    .forEach(v -> {
                        Object fieldValue = ReflectionUtils.getField(v, obj);
                        fieldMap.putIfAbsent(v, fieldValue);
                    });
        }
        return fieldMap;
    }

    private static String getValue(Object obj, SafeFunction<Object, String> objConverter, Predicate<Object> nullPredicater) {
        Stream<?> stream;
        if (obj instanceof Collection) {
            stream = ((Collection) obj).stream();
        } else {
            stream = Stream.of(obj);
        }
        String value = stream
                .filter(MoreObjects.firstNonNull(nullPredicater, IS_NULL).negate())
                .map(MoreObjects.firstNonNull(objConverter, TO_ESCAPE_STRING))
                .collect(Collectors.joining(","));
        if ("".equals(value)) {
            return null;
        }
        return "(" + value + ")";
    }

    public static String onDuplicateKeyUpdate(Object obj) {
        return onDuplicateKeyUpdate(obj, false, DEFAULT_OPTION);
    }

    public static String onDuplicateKeyUpdate(Object obj, boolean keepNull) {
        return onDuplicateKeyUpdate(obj, keepNull, DEFAULT_OPTION);
    }

    public static String onDuplicateKeyUpdate(Object obj, boolean keepNull, Option option) {
        if (obj == null) {
            return "";
        }
        if (option == null) {
            option = DEFAULT_OPTION;
        }
        SafeFunction<Object, String> objConverter = MoreObjects.firstNonNull(option.getObjConverter(), TO_ESCAPE_STRING);
        Predicate<Object> nullPredicater = MoreObjects.firstNonNull(option.getNullPredicater(), IS_NULL);
        Map<Field, Object> fieldMap = getFieldMap(obj, option);
        List<String> expressions = Lists.newArrayList();

        for (Map.Entry<Field, Object> entry : fieldMap.entrySet()) {
            Field field = entry.getKey();
            Object value = entry.getValue();
            String fieldName = getFieldName(field);
            String finalValue = getValue(value, objConverter, nullPredicater);
            if (finalValue == null && !keepNull) {
                continue;
            }
            String expression = fieldName + Operator.EQUAL.getOperator() + finalValue;
            expressions.add(expression);
        }
        if (CollectionUtils.isEmpty(expressions)) {
            return "";
        }
        return " ON DUPLICATE KEY UPDATE " + expressions.stream().collect(Collectors.joining(", "));
    }

    public static final Predicate<Object> IS_NULL = (obj) -> {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Collection<?>) {
            return !((Collection<?>) obj).stream()
                    .filter(Objects::nonNull)
                    .findAny()
                    .isPresent();
        }
        return false;
    };

    public static final SafeFunction<Object, String> TO_ESCAPE_STRING = (obj) -> {
        if (obj instanceof MyBatisEnum) {
            obj = ((MyBatisEnum) obj).getValue();
            if (obj == null) {
                return "";
            }
        }
        if (obj instanceof Date) {
            return "'" + FORMATTER.format(obj) + "'";
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj ? "1" : "0";
        }
        if (obj instanceof Number) {
            return obj.toString();
        }
        return quoteAndEscape(obj.toString());
    };
    /**
     * Similar to com.mysql.jdbc.PreparedStatement#setString
     */
    private static String quoteAndEscape(String input) {
        if (!isEscapeNeededForString(input)) {
            return StringUtils.quote(input);
        }
        StringBuilder buf = new StringBuilder((int) (input.length() * 1.1));
        buf.append("'");
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case 0: { /* Must be escaped for 'mysql' */
                    buf.append('\\');
                    buf.append('0');
                    break;
                }
                case '\n': { /* Must be escaped for logs */
                    buf.append('\\');
                    buf.append('n');
                    break;
                }
                case '\r': {
                    buf.append('\\');
                    buf.append('r');
                    break;
                }
                case '\\': {
                    buf.append('\\');
                    buf.append('\\');
                    break;
                }
                case '\'': {
                    buf.append('\\');
                    buf.append('\'');
                    break;
                }
                case '"': { /* Better safe than sorry */
                    buf.append('\\');
                    buf.append('"');
                    break;
                }
                case '\032': { /* This gives problems on Win32 */
                    buf.append('\\');
                    buf.append('Z');
                    break;
                }
                default: {
                    buf.append(c);
                }
            }
        }
        buf.append("'");
        return buf.toString();
    }
    /**
     * Similar to com.mysql.jdbc.PreparedStatement#isEscapeNeededForString
     */
    private static boolean isEscapeNeededForString(String input) {
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (c) {
                case 0: /* Must be escaped for 'mysql' */
                case '\n': /* Must be escaped for logs */
                case '\r':
                case '\\':
                case '\'':
                case '"': /* Better safe than sorry */
                case '\032': /* This gives problems on Win32 */
                    return true;
            }
        }
        return false;
    }

    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Option {
        Integer deep = 1;
        /**
         * 放入excludeFields内的java bean属性名称将不会被SQLJoiner解析
         */
        Set<String> excludeFields = Collections.emptySet();
        SafeFunction<Object, String> objConverter = TO_ESCAPE_STRING;
        Predicate<Object> nullPredicater = IS_NULL;
        Map<String, Operator> operatorTypeMap = Collections.emptyMap();

        public Integer getDeep() {
            return (deep == null || deep < 1) ? 1 : deep;
        }

        public Set<String> getExcludeFields() {
            return CollectionUtils.isEmpty(excludeFields) ? Collections.emptySet() : excludeFields;
        }

        public SafeFunction<Object, String> getObjConverter() {
            return MoreObjects.firstNonNull(objConverter, TO_ESCAPE_STRING);
        }

        public Predicate<Object> getNullPredicater() {
            return MoreObjects.firstNonNull(nullPredicater, IS_NULL);
        }

        public Map<String, Operator> getOperatorTypeMap() {
            return CollectionUtils.isEmpty(operatorTypeMap) ? Collections.emptyMap() : operatorTypeMap;
        }
    }
}
