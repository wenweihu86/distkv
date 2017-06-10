package com.github.wenweihu86.distkv.proxy;

import com.github.wenweihu86.distkv.api.MetaAPI;
import com.github.wenweihu86.distkv.api.StoreAPI;
import com.github.wenweihu86.rpc.client.EndPoint;
import com.github.wenweihu86.rpc.client.RPCClient;

import java.util.List;

/**
 * Created by wenweihu86 on 2017/6/10.
 */
public class ShardingClient {
    private int index;
    private List<EndPoint> servers;
    private RPCClient rpcClient;
    private MetaAPI metaAPI;
    private StoreAPI storeAPI;

    public ShardingClient(int index, List<EndPoint> servers) {
        this.index = index;
        this.servers = servers;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<EndPoint> getServers() {
        return servers;
    }

    public void setServers(List<EndPoint> servers) {
        this.servers = servers;
    }

    public RPCClient getRpcClient() {
        return rpcClient;
    }

    public void setRpcClient(RPCClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    public MetaAPI getMetaAPI() {
        return metaAPI;
    }

    public void setMetaAPI(MetaAPI metaAPI) {
        this.metaAPI = metaAPI;
    }

    public StoreAPI getStoreAPI() {
        return storeAPI;
    }

    public void setStoreAPI(StoreAPI storeAPI) {
        this.storeAPI = storeAPI;
    }

}
