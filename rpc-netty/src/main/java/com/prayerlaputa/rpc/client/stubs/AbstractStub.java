package com.prayerlaputa.rpc.client.stubs;

import com.prayerlaputa.rpc.client.RequestIdSupport;
import com.prayerlaputa.rpc.client.ServiceStub;
import com.prayerlaputa.rpc.client.ServiceTypes;
import com.prayerlaputa.rpc.serialize.SerializeSupport;
import com.prayerlaputa.rpc.transport.Transport;
import com.prayerlaputa.rpc.transport.command.Code;
import com.prayerlaputa.rpc.transport.command.Command;
import com.prayerlaputa.rpc.transport.command.Header;
import com.prayerlaputa.rpc.transport.command.ResponseHeader;

import java.util.concurrent.ExecutionException;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class AbstractStub implements ServiceStub {
    private Transport transport;

    protected byte[] invokeRemote(RpcRequest request) {
        Header header = new Header(ServiceTypes.TYPE_RPC_REQUEST, 1, RequestIdSupport.next());
        byte[] payload = SerializeSupport.serialize(request);
        Command requestCommand = new Command(header, payload);

        try {
            Command responseCommand = transport.send(requestCommand).get();
            ResponseHeader responseHeader = (ResponseHeader) responseCommand.getHeader();
            if (responseHeader.getCode() == Code.SUCCESS.getCode()) {
                return responseCommand.getPayload();
            } else {
                throw new Exception(responseHeader.getError());
            }
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}
