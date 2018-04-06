package com.k.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 简单分页信息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDTO<T> implements Serializable {

    private static final long serialVersionUID = 5927210529005643519L;

    /**
     * 分页信息
     * total : 总记录数
     */
    int total = 0;
    /**
     * 分页信息
     * pagination : 页面索引(页码)
     */
    int pagination = 0;
    /**
     * 分页信息
     * pageSize : 页面大小(获取的记录数)
     */
    int pageSize = 20;

    /**
     * 分页数据(查询结果)
     */
    List<T> model;


    //期望页头记录索引、期望页尾记录索引
    public int getStartIdx() {
        return pagination * pageSize;
    }

    public int getEndIdx() {
        return (pagination + 1) * pageSize - 1;
    }

    //实际页头记录索引、实际页尾记录索引
    public int getRealStartIdx() {
        if (total <= 0) {
            return 0;
        }
        int startIdx = getStartIdx();
        if (startIdx < 0) {
            return 0;
        }
        return (startIdx < total ? startIdx : total - 1);
    }

    public int getRealEndIdx() {
        if (total <= 0) {
            return 0;
        }
        int endIdx = getEndIdx();
        if (endIdx < 0) {
            return 0;
        }
        return (endIdx < total ? endIdx : total - 1);
    }

    /**
     * 跳过记录数量
     * @return
     */
    public int getSkip() {
        return getStartIdx();
    }

}
