package com.k.common.persistent;

import com.k.common.persistent.constants.Operator;
import com.k.common.persistent.dtos.BaseQueryParam;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author: 黄志泉
 * @Datetime: 2016-01-21 11:03
 */
public class BaseProvider {
    protected final String TABLE_NAME;

    public BaseProvider(String tableName) {
        this.TABLE_NAME = tableName;
    }

    protected SQL getSimpleQuerySQL(BaseQueryParam queryParam, String tableName) {
        if (!StringUtils.hasText(tableName)) {
            tableName = TABLE_NAME;
        }
        String queryFields = queryParam.getQueryFields();
        Set<String> excludeFields = queryParam.getExcludeFields();
        Map<String, Operator> operatorTypeMap = queryParam.getOperatorTypeMap();
        SQL sql = new SQL().SELECT(queryFields).FROM(tableName);
        SQLJoiner2.Option option = new SQLJoiner2.Option();
        option.setExcludeFields(excludeFields);
        option.setOperatorTypeMap(operatorTypeMap);
        SQLJoiner2.WHERE(sql, queryParam, option);
        SQLJoiner2.joinAllInOne(sql, queryParam);
        return sql;
    }

    protected SQL getSimpleQuerySQL(BaseQueryParam queryParam) {
        return getSimpleQuerySQL(queryParam, TABLE_NAME);
    }

    public String simpleQuery(BaseQueryParam queryParam) {
        return getSimpleQuerySQL(queryParam).toString();
    }
}
