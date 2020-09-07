package com.prayerlaputa.rpc.serialize.impl;

import com.prayerlaputa.rpc.nameservice.Metadata;
import com.prayerlaputa.rpc.serialize.Serializer;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class MetadataSerializer implements Serializer<Metadata> {
    @Override
    public int size(Metadata entry) {
        return Short.BYTES
                + entry.entrySet()
                        .stream()
                        .mapToInt(this::entrySize)
                        .sum();
    }

    private int entrySize(Map.Entry<String, List<URI>> e) {
        return Short.BYTES
                + e.getKey().getBytes().length
                + Short.BYTES
                + e.getValue().stream()
                .mapToInt(uri -> {
                    return Short.BYTES
                            + uri.toASCIIString().getBytes(StandardCharsets.UTF_8).length;
                }).sum();
    }

    @Override
    public void serialize(Metadata entry, byte[] bytes, int offset, int length) {
        //实现自定义序列号
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);
        buffer.putShort(toShortSafely(entry.size()));

        entry.forEach((k, v) -> {
           byte[] keyBytes = k.getBytes(StandardCharsets.UTF_8);
           buffer.putShort(toShortSafely(keyBytes.length));
           buffer.put(keyBytes);

           buffer.putShort(toShortSafely(v.size()));
           for (URI uri : v) {
               byte[] uriBytes = uri.toASCIIString().getBytes(StandardCharsets.UTF_8);
               buffer.putShort(toShortSafely(uriBytes.length));
               buffer.put(uriBytes);
           }
        });
    }

    private short toShortSafely(int v) {
        assert v < Short.MAX_VALUE;
        return (short) v;
    }

    @Override
    public Metadata parse(byte[] bytes, int offset, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, length);

        Metadata metadata = new Metadata();
        int sizeOfMap = buffer.getShort();
        for (int i = 0; i < sizeOfMap; i++) {
            //利用ByteBuffer的getShort()方法，获取字节数组的前两个字节，根据我们自定义的序列化协议，此处是拿到关键字字符串的长度
            int keyLength = buffer.getShort();
            byte[] keyBytes = new byte[keyLength];
            buffer.get(keyBytes);
            String key = new String(keyBytes, StandardCharsets.UTF_8);

            //那栋value list的长度
            int uriListSize = buffer.getShort();
            List<URI> uriList = new ArrayList<>(uriListSize);
            for (int j = 0; j < uriListSize; j++) {
                int uriLength = buffer.getShort();
                byte[] uriBytes = new byte[uriLength];
                buffer.get(uriBytes);
                URI uri = URI.create(new String(uriBytes, StandardCharsets.UTF_8));
                uriList.add(uri);
            }
            metadata.put(key, uriList);
        }
        return metadata;
    }

    @Override
    public byte type() {
        return Types.TYPE_METADATA;
    }

    @Override
    public Class<Metadata> getSerializeClass() {
        return Metadata.class;
    }
}
