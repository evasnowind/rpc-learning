package com.prayerlaputa.rpc.serialize;

/**
 * @author chenglong.yu
 * created on 2020/9/5
 */
public class SerializeException extends RuntimeException {

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
