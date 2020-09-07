package com.prayerlaputa.rpc.server;

import com.prayerlaputa.rpc.hello.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenglong.yu
 * created on 2020/9/5
 */
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);


    @Override
    public String hello(String name) {
        logger.info("HelloServiceImpl收到：{}.", name);
        String ret = "Hello, " + name;
        return ret;
    }
}
