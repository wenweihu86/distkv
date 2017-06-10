package com.github.wenweihu86.distkv.example.config;

import com.github.wenweihu86.distkv.api.ProxyAPI;
import com.github.wenweihu86.rpc.client.RPCClient;
import com.github.wenweihu86.rpc.client.RPCProxy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenweihu86 on 2017/6/10.
 */
@Configuration
@ConfigurationProperties(prefix = "distkv.proxy")
public class RPCClientConfig {

    private List<String> servers = new ArrayList<>();

    @Bean
    public ProxyAPI proxyAPI() {
        System.out.println(servers.size());
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String ipPort : servers) {
            if (isFirst) {
                sb.append(ipPort);
            } else {
                sb.append(";").append(ipPort);
            }
        }
        String ipPorts = sb.toString();
        RPCClient rpcClient = new RPCClient(ipPorts);
        ProxyAPI proxyAPI = RPCProxy.getProxy(rpcClient, ProxyAPI.class);
        return proxyAPI;
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
