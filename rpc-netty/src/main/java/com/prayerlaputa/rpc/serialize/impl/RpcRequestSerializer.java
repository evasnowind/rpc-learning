package com.prayerlaputa.rpc.serialize.impl;

import com.prayerlaputa.rpc.client.stubs.RpcRequest;
import com.prayerlaputa.rpc.serialize.Serializer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author chenglong.yu
 * created on 2020/9/7
 */
public class RpcRequestSerializer implements Serializer<RpcRequest> {
    @Override
    public int size(RpcRequest request) {
        //interfaceName变量引用
        return Integer.BYTES
                //interface字符串的长度
                + request.getInterfaceName().getBytes(StandardCharsets.UTF_8).length
                //methodName变量引用
                + Integer.BYTES
                //methodName字符串的长度
                + request.getMethodName().getBytes(StandardCharsets.UTF_8).length
                //serializedArguments变量引用
                + Integer.BYTES
                //serializedArguments 已经是byte数组，直接返回数组长度即可
                + request.getSerializedArguments().length;

    }

    @Override
    public void serialize(RpcRequest request, byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        byte[] tmpBytes = request.getInterfaceName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        tmpBytes = request.getMethodName().getBytes(StandardCharsets.UTF_8);
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);

        tmpBytes = request.getSerializedArguments();
        buffer.putInt(tmpBytes.length);
        buffer.put(tmpBytes);
    }

    @Override
    public RpcRequest parse(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        int len = buffer.getInt();
        byte[] tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String interfaceName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        String methodName = new String(tmpBytes, StandardCharsets.UTF_8);

        len = buffer.getInt();
        tmpBytes = new byte[len];
        buffer.get(tmpBytes);
        byte[] serializedArgs = tmpBytes;

        return new RpcRequest(interfaceName, methodName, serializedArgs);
    }

    @Override
    public byte type() {
        return Types.TYPE_RPC_REQUEST;
    }

    @Override
    public Class<RpcRequest> getSerializeClass() {
        return RpcRequest.class;
    }
}
