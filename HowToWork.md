# How To Work

## 源码分析  

### Server类

（1）RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
        创建NettyRpcAccessPoint对象
            会通过SPI加载ServiceProviderRegistry（实际对应RpcRequestHandler）
            
（2）rpcAccessPoint.startServer()
        server = ServiceSupport.load(TransportServer.class);
            额外引入一层，没有与NettyServer绑定，方便扩展
        server.start(RequestHandlerRegistry.getInstance(), port);
            创建NettyServer对象，使其获得RequestHandlerRegistry对象
                通过SPI注册了一个RpcRequestHandler
                    RpcRequestHandler保存service name --> service provider 关系；拿到一个Command后，解析、调用具体服务
                    目前的实现，RpcRequestHandler是单例
            start 开始监听

（3）rpcAccessPoint.getNameService
    通过SPI实例化NameService对象，目前只有一个LocalFileNameService
    实例化时会进行nameService.connect操作
        对于LocalFileNameService，就是创建本地文件File对象


(4)创建HelloServiceImpl对象，注册到rpcAccessPoint中
    rpcAccessPoint.addServiceProvider(helloService, HelloService.class);
        serviceProviderRegistry.addServiceProvider(serviceClass, service);
            RpcRequestHandler的serviceProviders中保存了service name --> service provider 关系

（5）注册服务，给出服务-> 服务URI  
    nameService.registerService(serviceName, uri);
        读取文件内容，反序列，获取已经写入的内容，生成一个Metadata对象（是一个HashMap）
        将本次要注册的serviceName --> uri写入到这个Metadata对象中
        由于需要保证数据一致、并发安全，采用了文件锁FileLock


### Client类  

（1）RpcAccessPoint rpcAccessPoint = ServiceSupport.load(RpcAccessPoint.class);
    创建NettyRpcAccessPoint对象
        会通过SPI加载ServiceProviderRegistry（实际对应RpcRequestHandler）
（2）NameService nameService = rpcAccessPoint.getNameService(file.toURI());
    通过SPI创建nameService对象
        只有一个LocalFileNameService，将会读取本地文件，拿到File对象

（3）URI uri = nameService.lookupService(serviceName);
    锁上文件，读取文件内容，反序列化出一个Metadata对象，并在这个Metadata中利用serviceName查找URI列表

（4）HelloService helloService = rpcAccessPoint.getRemoteService(uri, HelloService.class);
    根据URI、以及接口class，去获取远程服务对象
        如果本地没有，创建一个NettyTransport，该对象中包含根据URI创建的channel、在途请求表InFlightRequests
            在途请求表InFlightRequests有调度任务，若超过时间则会将其移除
        NettyTransport对象会缓存在本地

        根据transport创建本地stub
            目前是通过硬编码、直接生成java代码字符串、然后生成java代码，再编译获得stub，实际RPC项目（用java实现的RPC，比如dubbo）一般采用动态代理
            将transport赋值给stub

(5) 调用service 的对应方法（此处是HelloService.hello）
    此处将会调用通过调用stub的invoke方法，来对应方法（hello），invoke方法在生成的stub中，该方法主要包含：
        1. 将参数序列化
        2. 发起远程调用（通过transport的send方法来操作）
        3. 将结果反序列化，返回调用结果



### Stub示例

```java
package com.prayerlaputa.rpc.client.stubs;
import com.prayerlaputa.rpc.serialize.SerializeSupport;

public class HelloServiceStub extends AbstractStub implements com.prayerlaputa.rpc.hello.HelloService {
    @Override
    public String hello(String arg) {
        return SerializeSupport.parse(
                invokeRemote(
                        new RpcRequest(
                                "com.prayerlaputa.rpc.hello.HelloService",
                                "hello",
                                SerializeSupport.serialize(arg)
                        )
                )
        );
    }
}
```

 