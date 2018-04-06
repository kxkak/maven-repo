package com.k.common.persistent.constants;

public interface ConverterEnum<E> {
    /**
     * string -> enum
     */
    E convert(String value);
}
