package com.github.wenweihu86.distkv.example.config;

import com.github.wenweihu86.distkv.api.ProxyAPI;
import com.github.wenweihu86.rpc.client.RPCClient;
import com.github.wenweihu86.rpc.client.RPCProxy;
import org.apache.commons.lang.StringUtils;
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
        String ipPorts = StringUtils.join(servers, ",");
        RPCClient rpcClient = new RPCClient(ipPorts);
        return RPCProxy.getProxy(rpcClient, ProxyAPI.class);
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
