package com.prayerlaputa.rpc.client;

import com.prayerlaputa.rpc.transport.Transport;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public interface StubFactory {

    <T> T createStub(Transport transport, Class<T> serviceClass);
}
