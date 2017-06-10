package com.github.wenweihu86.distkv.proxy;

import com.github.wenweihu86.distkv.api.MetaAPI;
import com.github.wenweihu86.distkv.api.StoreAPI;
import com.github.wenweihu86.rpc.client.EndPoint;
import com.github.wenweihu86.rpc.client.RPCClient;
import com.github.wenweihu86.rpc.client.RPCProxy;
import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class GlobalBean {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalBean.class);
    private static GlobalBean instance;

    private Toml toml;
    private List<ShardingClient> metaServerShadings;
    private List<ShardingClient> storeServerShardings;

    public GlobalBean() {
        String fileName = "/proxy.toml";
        File file = new File(getClass().getResource(fileName).getFile());
        toml = new Toml().read(file);
        readServerShardingsConf();
        initRPCClient();
    }

    public static GlobalBean getInstance() {
        if (instance == null) {
            instance = new GlobalBean();
        }
        return instance;
    }

    public int getPort() {
        return (int) toml.getLong("port").longValue();
    }

    public List<ShardingClient> getMetaServerShadings() {
        return metaServerShadings;
    }

    public List<ShardingClient> getStoreServerShardings() {
        return storeServerShardings;
    }

    private void initRPCClient() {
        for (ShardingClient shardingClient : metaServerShadings) {
            RPCClient rpcClient = new RPCClient(shardingClient.getServers());
            shardingClient.setRpcClient(rpcClient);
            MetaAPI metaAPI = RPCProxy.getProxy(rpcClient, MetaAPI.class);
            shardingClient.setMetaAPI(metaAPI);
        }

        for (ShardingClient shardingClient : storeServerShardings) {
            RPCClient rpcClient = new RPCClient(shardingClient.getServers());
            shardingClient.setRpcClient(rpcClient);
            StoreAPI storeAPI = RPCProxy.getProxy(rpcClient, StoreAPI.class);
            shardingClient.setStoreAPI(storeAPI);
        }
    }

    private void readServerShardingsConf() {
        metaServerShadings = new ArrayList<>();
        Toml metaServerConf = toml.getTable("meta_server");
        List<Toml> shardingConfList = metaServerConf.getTables("sharding");
        for (Toml shardingConf : shardingConfList) {
            metaServerShadings.add(readShardingConf(shardingConf));
        }

        storeServerShardings = new ArrayList<>();
        Toml storeServerConf = toml.getTable("store_server");
        shardingConfList = storeServerConf.getTables("sharding");
        for (Toml shardingConf : shardingConfList) {
            storeServerShardings.add(readShardingConf(shardingConf));
        }
    }

    private ShardingClient readShardingConf(Toml shardingConf) {
        int index = shardingConf.getLong("index").intValue();
        List<Toml> serverConfList = shardingConf.getTables("server");
        List<EndPoint> servers = new ArrayList<>();
        for (Toml serverConf : serverConfList) {
            servers.add(readServerConf(serverConf));
        }
        ShardingClient sharding = new ShardingClient(index, servers);
        return sharding;
    }

    private EndPoint readServerConf(Toml serverConf) {
        String ip = serverConf.getString("ip");
        int port = serverConf.getLong("port").intValue();
        EndPoint endPoint = new EndPoint(ip, port);
        LOG.info("read conf server, ip={}, port={}", ip, port);
        return endPoint;
    }

}
