package com.prayerlaputa.rpc.transport.command;

import java.nio.charset.StandardCharsets;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class ResponseHeader extends Header {

    private int code;
    private String error;

    public ResponseHeader(int type, int version, int requestId, int code, String error) {
        super(type, version, requestId);
        this.code = code;
        this.error = error;
    }

    public ResponseHeader(int type, int version, int requestId) {
        this(type, version, requestId, Code.SUCCESS.getCode(), null);
    }

    public ResponseHeader(int type, int version, int requestId, Throwable throwable) {
        this(type, version, requestId, Code.UNKNOWN_ERROR.getCode(), throwable.getMessage());
    }

    @Override
    public int length() {
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + Integer.BYTES
                //error引用所占用位置
                + Integer.BYTES
                //error字符串字符所占用位置
                + (null == error ? 0 : error.getBytes(StandardCharsets.UTF_8).length);
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }
}
