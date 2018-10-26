package com.github.wenweihu86.distkv.store;

import com.github.wenweihu86.distkv.api.CommonMessage;
import com.github.wenweihu86.distkv.api.StoreAPI;
import com.github.wenweihu86.distkv.api.StoreMessage;
import com.github.wenweihu86.raft.RaftNode;
import com.github.wenweihu86.raft.proto.RaftMessage;
import com.github.wenweihu86.rpc.client.RPCClient;
import com.github.wenweihu86.rpc.client.RPCProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class StoreAPIImpl implements StoreAPI {

    private static final Logger LOG = LoggerFactory.getLogger(StoreAPIImpl.class);

    private RaftNode raftNode;
    private StoreStateMachine stateMachine;

    public StoreAPIImpl(RaftNode raftNode, StoreStateMachine stateMachine) {
        this.raftNode = raftNode;
        this.stateMachine = stateMachine;
    }

    @Override
    public StoreMessage.SetResponse set(StoreMessage.SetRequest request) {
        StoreMessage.SetResponse.Builder responseBuilder = StoreMessage.SetResponse.newBuilder();
        CommonMessage.BaseResponse.Builder baseResBuilder = CommonMessage.BaseResponse.newBuilder();
        // 如果自己不是leader，将写请求转发给leader
        if (raftNode.getLeaderId() <= 0) {
            baseResBuilder.setResCode(CommonMessage.ResCode.RES_CODE_FAIL);
            baseResBuilder.setResMsg("leader not exists");
            responseBuilder.setBaseRes(baseResBuilder);
        } else if (raftNode.getLeaderId() != raftNode.getLocalServer().getServerId()) {
            RPCClient rpcClient = raftNode.getPeerMap().get(raftNode.getLeaderId()).getRpcClient();
            StoreAPI storeAPI = RPCProxy.getProxy(rpcClient, StoreAPI.class);
            StoreMessage.SetResponse responseFromLeader = storeAPI.set(request);
            responseBuilder.mergeFrom(responseFromLeader);
        } else {
            // 数据同步写入raft集群
            RaftDataPacket packet = RaftDataPacket.buildPacket(CommonMessage.RequestType.SET, request);
            byte[] data = RaftDataPacket.encode(packet);
            boolean success = raftNode.replicate(data, RaftMessage.EntryType.ENTRY_TYPE_DATA);
            baseResBuilder.setResCode(
                    success ? CommonMessage.ResCode.RES_CODE_SUCCESS
                            : CommonMessage.ResCode.RES_CODE_FAIL);
            responseBuilder.setBaseRes(baseResBuilder);
        }

        StoreMessage.SetResponse response = responseBuilder.build();
        LOG.info("set request, key={}, resCode={}, resMsg={}",
                new String(request.getKey().toByteArray()),
                response.getBaseRes().getResCode(),
                response.getBaseRes().getResMsg());
        return response;
    }

    @Override
    public StoreMessage.GetResponse get(StoreMessage.GetRequest request) {
        StoreMessage.GetResponse response = stateMachine.get(request);
        if (response != null) {
            LOG.info("get request, key={}, resCode={}, resMsg={}",
                    new String(request.getKey().toByteArray()),
                    response.getBaseRes().getResCode(),
                    response.getBaseRes().getResMsg());
        } else {
            LOG.warn("get request failed, key={}, response=null",
                    new String(request.getKey().toByteArray()));
        }
        return response;
    }

    @Override
    public StoreMessage.DeleteResponse delete(StoreMessage.DeleteRequest request) {
        StoreMessage.DeleteResponse.Builder responseBuilder = StoreMessage.DeleteResponse.newBuilder();
        CommonMessage.BaseResponse.Builder baseResBuilder = CommonMessage.BaseResponse.newBuilder();
        // 如果自己不是leader，将写请求转发给leader
        if (raftNode.getLeaderId() <= 0) {
            baseResBuilder.setResCode(CommonMessage.ResCode.RES_CODE_FAIL);
            baseResBuilder.setResMsg("leader not exists");
            responseBuilder.setBaseRes(baseResBuilder);
        } else if (raftNode.getLeaderId() != raftNode.getLocalServer().getServerId()) {
            RPCClient rpcClient = raftNode.getPeerMap().get(raftNode.getLeaderId()).getRpcClient();
            StoreAPI storeAPI = RPCProxy.getProxy(rpcClient, StoreAPI.class);
            StoreMessage.DeleteResponse responseFromLeader = storeAPI.delete(request);
            responseBuilder.mergeFrom(responseFromLeader);
        } else {
            // 数据同步写入raft集群
            RaftDataPacket packet = RaftDataPacket.buildPacket(CommonMessage.RequestType.DELETE, request);
            byte[] data = RaftDataPacket.encode(packet);
            boolean success = raftNode.replicate(data, RaftMessage.EntryType.ENTRY_TYPE_DATA);
            baseResBuilder.setResCode(
                    success ? CommonMessage.ResCode.RES_CODE_SUCCESS
                            : CommonMessage.ResCode.RES_CODE_FAIL);
            responseBuilder.setBaseRes(baseResBuilder);
        }

        StoreMessage.DeleteResponse response = responseBuilder.build();
        LOG.info("delete request, key={}, resCode={}, resMsg={}",
                new String(request.getKey().toByteArray()),
                response.getBaseRes().getResCode(),
                response.getBaseRes().getResMsg());
        return response;
    }

}
