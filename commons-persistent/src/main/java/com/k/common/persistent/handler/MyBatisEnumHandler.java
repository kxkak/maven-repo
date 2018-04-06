package com.k.common.persistent.handler;

import com.k.common.persistent.constants.MyBatisEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatisEnum <-> DB value
 *
 * @Author: 黄志泉
 * @Datetime: 2015-08-12 9:28
 */
public class MyBatisEnumHandler<E extends MyBatisEnum<E>> extends BaseTypeHandler<E> {
    private final E myBatisEnum;

    public MyBatisEnumHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.myBatisEnum = type.getEnumConstants()[0];
        if (this.myBatisEnum == null) {
            throw new IllegalArgumentException(type.getSimpleName()
                    + " does not represent an enum type.");
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return value == null ? null : myBatisEnum.getSelf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return value == null ? null : myBatisEnum.getSelf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return value == null ? null : myBatisEnum.getSelf(value);
    }
}
