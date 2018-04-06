package com.k.common.persistent.constants;

/**
 * For MyBatis
 * Enum <-> Object
 */
public interface MyBatisEnum<E> {
    /**
     * Enum -> db value
     */
    Object getValue();

    /**
     * db value -> Enum
     */
    E getSelf(Object value);
}
