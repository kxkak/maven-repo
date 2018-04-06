package com.k.common.dto;

import com.k.common.constant.ResultCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 结果返回对象
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ResultDTO<T> {

    /**
     * 数据，真正的结果对象
     */
    public T model;

    /**
     * 状态码
     * @see ResultCode
     */
    private int state;

    /**
     * 信息
     */
    private String msg;

    public static <T> ResultDTO<T> success() {
        return new ResultDTO(null, ResultCode.SUCCESS, null);
    }

    public static <T> ResultDTO<T> success(T model) {
        return new ResultDTO(model, ResultCode.SUCCESS, null);
    }

    public boolean isSuccess() {
        return ResultCode.SUCCESS == state;
    }

    public boolean isFail() {
        return ResultCode.SUCCESS != state;
    }

    public static <T> ResultDTO<T> fail(int resultCode, String msg) {
        return new ResultDTO(null, resultCode, msg);
    }

    public static ResultDTO fail(String s) {
        return fail(ResultCode.ILLEGAL_ARGS,s);
    }

}
