package com.prayerlaputa.rpc.server;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public interface ServiceProviderRegistry {

    <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider);
}
