package com.css.cvds.cmu.web.bean;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "统一返回结果")
public class WVPResult<T> {

    public WVPResult() {
    }

    public WVPResult(int code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    @Schema(description = "错误码，0为成功")
    private int code;
    @Schema(description = "描述，错误时描述错误原因")
    private String message;
    @Schema(description = "数据")
    private T data;

    public static <T> WVPResult<T> success() {
        return success(null, ErrorCode.SUCCESS.getMsg());
    }

    public static <T> WVPResult<T> success(T t, String msg) {
        return new WVPResult<>(ErrorCode.SUCCESS.getCode(), msg, t);
    }

    public static <T> WVPResult<T> success(T t) {
        return success(t, ErrorCode.SUCCESS.getMsg());
    }

    public static <T> WVPResult<T> fail(int code, String msg) {
        return new WVPResult<>(code, msg, null);
    }

    public static <T> WVPResult<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> WVPResult<T> fail(String msg) {
        return fail(ErrorCode.ERROR100.getCode(), msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
