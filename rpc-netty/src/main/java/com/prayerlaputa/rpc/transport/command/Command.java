package com.prayerlaputa.rpc.transport.command;

/**
 * @author chenglong.yu
 * created on 2020/9/4
 */
public class Command {

    protected Header header;
    private byte[] payload;

    public Command(Header header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
