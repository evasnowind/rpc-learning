package com.prayerlaputa.rpc.transport;

import com.prayerlaputa.rpc.transport.command.Command;

/**
 * @author chenglong.yu
 * created on 2020/9/5
 */
public interface RequestHandler {

    /**
     * 处理请求
     *
     * @param requestCommand 请求命令
     * @return 响应命令
     */
    Command handle(Command requestCommand);

    /**
     * 支持的请求类型
     * @return
     */
    int type();
}
