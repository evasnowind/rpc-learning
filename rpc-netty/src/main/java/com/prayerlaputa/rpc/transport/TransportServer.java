package com.prayerlaputa.rpc.transport;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public interface TransportServer {

    void start(RequestHandlerRegistry requestHandlerRegistry, int port) throws Exception;
    void stop();
}
