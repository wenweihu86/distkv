package com.github.wenweihu86.distkv.meta;

import com.github.wenweihu86.raft.proto.RaftMessage;
import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class GlobalConf {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalConf.class);
    private static GlobalConf instance;

    private Toml toml;

    public GlobalConf() {
        String fileName = "/meta.toml";
        File file = new File(getClass().getResource(fileName).getFile());
        toml = new Toml().read(file);
    }

    public static GlobalConf getInstance() {
        if (instance == null) {
            instance = new GlobalConf();
        }
        return instance;
    }

    public String getString(String key) {
        return toml.getString(key);
    }

    public RaftMessage.Server getLocalServer() {
        RaftMessage.Server.Builder serverBuilder = RaftMessage.Server.newBuilder();
        RaftMessage.EndPoint.Builder endPointBuilder = RaftMessage.EndPoint.newBuilder();
        Toml localServerConf = toml.getTable("local_server");
        endPointBuilder.setHost(localServerConf.getString("ip"));
        endPointBuilder.setPort(localServerConf.getLong("port").intValue());
        serverBuilder.setEndPoint(endPointBuilder);
        serverBuilder.setServerId(localServerConf.getLong("id").intValue());
        RaftMessage.Server localServer = serverBuilder.build();
        LOG.info("read local_server conf={}", ProtobufUtils.protoToJson(localServer));
        return localServer;
    }

    public List<RaftMessage.Server> getServers() {
        List<RaftMessage.Server> servers = new ArrayList<>();
        List<Toml> serverConfList = toml.getTables("servers");
        for (Toml serverConf : serverConfList) {
            RaftMessage.EndPoint endPoint = RaftMessage.EndPoint.newBuilder()
                    .setHost(serverConf.getString("ip"))
                    .setPort(serverConf.getLong("port").intValue())
                    .build();
            RaftMessage.Server server = RaftMessage.Server.newBuilder()
                    .setEndPoint(endPoint)
                    .setServerId(serverConf.getLong("id").intValue())
                    .build();
            LOG.info("read conf server={}", ProtobufUtils.protoToJson(server));
            servers.add(server);
        }
        return servers;
    }

}
