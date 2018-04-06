package com.k.common.persistent.dtos;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.k.common.persistent.SQLJoiner2;
import com.k.common.persistent.constants.Operator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 基本查询参数
 * 请避免将继承BaseQueryParam的查询参数直接暴露给前端, 如有需要, 请拦截从前端传入BaseQueryParam的参数(具体可参考supplier-web的ControllerAdvice类实现)
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class BaseQueryParam implements Serializable {
    /**
     * 起始查询时间字段
     * eg:startTimeField = "createTime >= ";
     */
    String startTimeField;
    /**
     * 起始查询时间
     */
    Date startTime;
    /**
     * 终止查询时间字段
     * eg:endTimeField = "createTime <= ";
     */
    String endTimeField;
    /**
     * 终止查询时间
     */
    Date endTime;
    /**
     * 排序字段依据（多个字段用,分隔）
     * SQL:ORDER BY ?
     */
    String orderBy;
    /**
     * SQL:GROUP BY ?
     */
    String groupBy;
    /**
     * SQL:HAVING ?
     */
    String having;
    /**
     * 判断条件里需要排除的Java bean属性名称
     * 例子：
     * //@SQLField("names")
     * private Set< String > names;
     */
    Set<String> excludeFields = Collections.emptySet();
    /**
     * 查询字段，多个字段以,分割，默认：*
     */
    String queryFields;
    /**
     * key是Java bean属性名称, value是操作符
     * 该参数仅能用于SELECT语句, INSERT和UPDATE均不能使用
     *
     * Usage:
     * {'partsNames': Operator.LIKE} -> ... partsName LIKE '...'
     * {'partsNames': Operator.NOT_IN} -> ... partsName NOT IN (...)
     * {'partsNames': Operator.IS_NULL} -> ... partsName IS NULL
     */
    Map<String, Operator> operatorTypeMap = Collections.emptyMap();
    /**
     * 是否需要总记录数，默认关闭
     */
    boolean needTotalCount = false;
    /**
     * 是否需要分页，默认分页
     */
    boolean needPagination = true;
    /**
     * 分页索引，默认0
     */
    int page;
    /**
     * 分页大小
     */
    Integer pageSize;
    /**
     * 设置分页索引
     */
    public void setPage(int page) {
        Preconditions.checkArgument(page >= 0, "page must >= 0");
        this.page = page;
    }
    /**
     * 设置分页大小
     */
    public void setPageSize(Integer pageSize) {
        Preconditions.checkArgument(pageSize == null || pageSize >= 0, "pageSize must >= 0");
        this.pageSize = pageSize;
    }
    /**
     * 当没有指定查询字段时，默认查询所有字段
     */
    public String getQueryFields() {
        return StringUtils.hasText(queryFields) ? queryFields : "*";
    }

    public Operator putOperator(String fieldName, Operator operator) {
        if (CollectionUtils.isEmpty(operatorTypeMap)) {
            operatorTypeMap = Maps.newHashMap();
        }
        return operatorTypeMap.put(fieldName, operator);
    }
    /**
     * 当所有值为空,则认为该查询参数为空
     * PS:采用SQLJoiner2.IS_NULL判断是否为空
     */
    protected boolean isEmpty(Supplier<?>...suppliers) {
        if (suppliers == null) {
            return true;
        }
        for (Supplier<?> supplier : suppliers) {
            Object obj = supplier.get();
            if (!SQLJoiner2.IS_NULL.test(obj)) {
                return false;
            }
        }
        return true;
    }
    /**
     * 判断当前查询参数是否为空
     * Usage:
     * 以PartsQueryParam为例
     * return super.isEmpty(this::getPartsIds, this::getPartsCodes, this::getBrandNames, this::getNameIds, this::getPartsNames);
     */
    public boolean isEmpty() {
        return false;
    }
}
