package com.prayerlaputa.rpc.server;

import com.prayerlaputa.rpc.client.ServiceTypes;
import com.prayerlaputa.rpc.client.stubs.RpcRequest;
import com.prayerlaputa.rpc.serialize.SerializeSupport;
import com.prayerlaputa.rpc.spi.Singleton;
import com.prayerlaputa.rpc.transport.RequestHandler;
import com.prayerlaputa.rpc.transport.command.Code;
import com.prayerlaputa.rpc.transport.command.Command;
import com.prayerlaputa.rpc.transport.command.Header;
import com.prayerlaputa.rpc.transport.command.ResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.DocFlavor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
@Singleton
public class RpcRequestHandler implements RequestHandler, ServiceProviderRegistry {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    /**
     * service name --> service provider
     */
    private Map<String, Object> serviceProviders = new HashMap<>();

    @Override
    public <T> void addServiceProvider(Class<? extends T> serviceClass, T serviceProvider) {
        serviceProviders.put(serviceClass.getCanonicalName(), serviceProvider);
        logger.info("Add service: {}, provider: {}.",
                serviceClass.getCanonicalName(),
                serviceProvider.getClass().getCanonicalName());
    }

    @Override
    public Command handle(Command requestCommand) {
        Header header = requestCommand.getHeader();
        //从payload中反序列化RpcRequest
        RpcRequest rpcRequest = SerializeSupport.parse(requestCommand.getPayload());
        try {
            Object serviceProvider = serviceProviders.get(rpcRequest.getInterfaceName());
            if (serviceProvider != null) {
                // 找到服务提供者，利用Java反射机制调用服务的对应方法
                String arg = SerializeSupport.parse(rpcRequest.getSerializedArguments());
                Method method = serviceProvider.getClass().getMethod(rpcRequest.getMethodName(), String.class);
                String result = (String)method.invoke(serviceProvider, arg);
                // 把结果封装成响应命令并返回
                return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId()), SerializeSupport.serialize(result));
            }
            logger.warn("No service Provider of {}#{}(String)!", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.NO_PROVIDER.getCode(), "No provider!"), new byte[0]);
        } catch (Throwable t) {
            logger.warn("Exception: ", t);
            return new Command(new ResponseHeader(type(), header.getVersion(), header.getRequestId(), Code.UNKNOWN_ERROR.getCode(), t.getMessage()), new byte[0]);
        }
    }

    @Override
    public int type() {
        return ServiceTypes.TYPE_RPC_REQUEST;
    }
}
