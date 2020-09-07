package com.prayerlaputa.rpc.spi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author chenglong.yu
 * created on 2020/9/5
 */
public class ServiceSupport {

    private final static Map<String, Object> singletonServices = new HashMap<>();

    public synchronized static <S> S load(Class<S> service) {
        /*
        SPI机制动态加载对象，但由于SPI的ServiceLoader.load是延迟加载，需要有一个遍历操作。
        下面是通过lambda的形式遍历
         */
        return StreamSupport
                .stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .findFirst()
                .orElseThrow(ServiceLoadException::new);
    }

    public synchronized static <S> Collection<S> loadAll(Class<S> service) {
        return StreamSupport
                .stream(ServiceLoader.load(service).spliterator(), false)
                .map(ServiceSupport::singletonFilter)
                .collect(Collectors.toList());
    }

    public synchronized static <S> S singletonFilter(S service) {
        if (service.getClass().isAnnotationPresent(Singleton.class)) {
            String className = service.getClass().getCanonicalName();
            Object singletonInstance = singletonServices.putIfAbsent(className, service);
            return singletonInstance == null ? service : (S) singletonInstance;
        } else {
            return service;
        }
    }
}
