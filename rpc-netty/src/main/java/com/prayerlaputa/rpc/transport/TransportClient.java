package com.prayerlaputa.rpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeoutException;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public interface TransportClient extends Closeable {

    Transport createTransport(SocketAddress address, long connectionTimeout) throws InterruptedException, TimeoutException;

    @Override
    void close();
}
