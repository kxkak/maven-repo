package com.k.common.persistent.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作符匹配模式
 */
@Slf4j
@AllArgsConstructor
@Getter
public enum Operator implements ConverterEnum<Operator> {
    EQUAL(1, " = ", "精确"),
    LIKE(2, " LIKE ", "模糊"),
    IN(3, " IN ", "IN"),
    NOT_IN(4, " NOT IN ", "NOT IN"),
    IS_NULL(5, " IS NULL ", "空"),
    IS_NOT_NULL(6, " IS NOT NULL ", "非空"),
    REGEXP(7, " REGEXP ", "正则匹配"),
    GT(8, " > ", "大于"),
    GTE(9, " >= ", "大于等于"),
    LT(10, " < ", "小于"),
    LTE(11, " <= ", "小于等于"),
    NOT_EQUAL(12, " <> ", "不等于");

    private int id;
    private String operator;
    private String description;

    public static Operator getById(int id) {
        for (Operator operator : values()) {
            if (operator.getId() == id) {
                return operator;
            }
        }
        log.warn("id({}) is not match", id);
        return EQUAL;
    }

    @Override
    public Operator convert(String value) {
        return getById(Integer.valueOf(value));
    }
}
