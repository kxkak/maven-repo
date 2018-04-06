package com.k.common.persistent.dtos;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页数据模型
 * User: ko
 * Date: 15/7/14
 * Time: 上午11:17
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageModel<T> implements Serializable {
    /**
     * 空数据
     */
    private static final PageModel EMPTY = new PageModel();
    /**
     * 分页页面数据
     */
    private List<T> data = Collections.emptyList();
    /**
     * 数据总数
     */
    private int totalCount;
    /**
     * 分页索引，默认0
     */
    private int page;
    /**
     * 分页大小
     */
    private Integer pageSize;

    public static <T> PageModel<T> build(List<T> data, int totalCount) {
        return build(data, totalCount, 0, null);
    }

    public static <T> PageModel<T> build(List<T> data, int totalCount, int page, Integer pageSize) {
        return new PageModel(data, totalCount, page, pageSize);
    }
    /**
     * 空数据
     */
    @SuppressWarnings("unchecked")
    public static <T> PageModel<T> emptyModel() {
        return (PageModel<T>) EMPTY;
    }
    /**
     * 带page和pageSize的空数据
     */
    public static <T> PageModel<T> emptyModel(int page, Integer pageSize) {
        return (PageModel<T>) build(Collections.emptyList(), 0, page, pageSize);
    }

    public boolean hasData() {
        return data.size() > 0;
    }
}