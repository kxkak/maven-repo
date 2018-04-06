package com.k.common.constant;

/**
 * 结果状态码
 */
public interface ResultCode {

    /**
     * 成功
     */
    int SUCCESS = 0;

    /**
     * 系统错误
     */
    int SYSTEM_ERROR = 1;

    /**
     * 请求参数错误
     */
    int ILLEGAL_ARGS = 2;

    /**
     * 结果集为空
     */
    int RESULT_IS_EMPTY = 3;
}
