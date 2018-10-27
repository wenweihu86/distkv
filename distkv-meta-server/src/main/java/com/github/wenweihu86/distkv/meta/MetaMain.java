package com.github.wenweihu86.distkv.meta;

import com.github.wenweihu86.distkv.api.MetaAPI;
import com.github.wenweihu86.raft.RaftNode;
import com.github.wenweihu86.raft.RaftOptions;
import com.github.wenweihu86.raft.proto.RaftMessage;
import com.github.wenweihu86.raft.service.RaftClientService;
import com.github.wenweihu86.raft.service.RaftConsensusService;
import com.github.wenweihu86.raft.service.impl.RaftClientServiceImpl;
import com.github.wenweihu86.raft.service.impl.RaftConsensusServiceImpl;
import com.github.wenweihu86.rpc.server.RPCServer;

import java.util.List;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class MetaMain {
    public static void main(String[] args) {
        // read conf
        GlobalConf conf = GlobalConf.getInstance();
        RaftMessage.Server localServer = conf.getLocalServer();
        List<RaftMessage.Server> servers = conf.getServers();
        String dataDir = conf.getString("data_dir");

        // 初始化RPCServer
        RPCServer server = new RPCServer(localServer.getEndPoint().getPort());
        // 应用状态机
        MetaStateMachine stateMachine = new MetaStateMachine(dataDir);
        RaftOptions options = new RaftOptions();
        // 设置数据目录
        options.setDataDir(dataDir);
        // 初始化RaftNode
        RaftNode raftNode = new RaftNode(options, servers, localServer, stateMachine);
        // 注册Raft节点之间相互调用的服务
        RaftConsensusService raftConsensusService = new RaftConsensusServiceImpl(raftNode);
        server.registerService(raftConsensusService);
        // 注册给Client调用的Raft服务
        RaftClientService raftClientService = new RaftClientServiceImpl(raftNode);
        server.registerService(raftClientService);
        // 注册应用自己提供的服务
        MetaAPI metaAPI = new MetaAPIImpl(raftNode, stateMachine);
        server.registerService(metaAPI);
        // 启动RPCServer，初始化Raft节点
        server.start();
        raftNode.init();
    }

}
