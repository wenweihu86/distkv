# distkv
分布式KV存储系统，也适用于存储小文件，比如图片。

# 架构图
![distkv架构图](https://github.com/wenweihu86/distkv/blob/master/docs/distkv-arch.png)

# 各模块功能介绍
* distkv-store-server：存储实际key/value数据，是多组raft集群，每组是一个分片。
* distkv-meta-server：存储key对应到distkv-store-server的哪个分片上。
* distkv-proxy-server：client请求代理层，负责和distkv-meta-server以及distkv-store-server通信。

# 主要接口的实现
## 写入接口实现
* client发送set请求给proxy-server
* proxy-server根据store-server分片数量，给请求key分配一个分片id（shardingIndex）
* proxy-server请求meta-server，将key和分片id写入到meta-server中。
* proxy-server请求分片id对应的store-server集群，将key和value写入到该分片集群中。
* proxy-server返回给client成功。

## 读取接口实现
* client发送get请求给proxy-server
* proxy-server请求meta-server，获取key对应的分片id
* proxy请求分片id对应的store-server集群，获取key对应的value
* proxy将value返回给client

# 部署
* 执行mvn clean package，然后将target下的zip包解压到部署目录，执行./bin/run.sh即可。
* 对于一个模块多节点部署的，需要更改conf和run.sh中的端口，保证端口不要重复。
* meta-server和store-server都是raft集群，建议部署三节点以上。
* proxy-server是无状态的，可以部署任意个节点。
* example模块是spring boot应用，提供了基本的key/value写入/读取使用方法，同时也提供了图片写入/读取的使用方法。
