package com.github.wenweihu86.distkv.proxy;

import com.github.wenweihu86.distkv.api.ProxyAPI;
import com.github.wenweihu86.rpc.server.RPCServer;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class ProxyMain {
    public static void main(String[] args) {
        // read conf
        GlobalBean globalBean = GlobalBean.getInstance();

        // 初始化RPCServer
        RPCServer server = new RPCServer(globalBean.getPort());
        // 注册应用自己提供的服务
        ProxyAPI proxyAPI = new ProxyAPIImpl();
        server.registerService(proxyAPI);
        // 启动RPCServer，初始化Raft节点
        server.start();

        // make server keep running
        synchronized (ProxyMain.class) {
            try {
                ProxyMain.class.wait();
            } catch (Throwable e) {
            }
        }
    }

}
