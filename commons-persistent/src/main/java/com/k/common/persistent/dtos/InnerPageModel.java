package com.k.common.persistent.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页数据模型(用于DAO层接收数据),不能被正常序列化
 *
 * @Author: 黄志泉
 * @Datetime: 2016-03-01 00:58
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InnerPageModel<T> extends ArrayList<T> {
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

    public static <T> InnerPageModel<T> build(List<T> data, int totalCount, int page, Integer pageSize) {
        return new InnerPageModel(data, totalCount, page, pageSize);
    }

    public PageModel<T> brother() {
        return PageModel.build(data, totalCount, page, pageSize);
    }
}