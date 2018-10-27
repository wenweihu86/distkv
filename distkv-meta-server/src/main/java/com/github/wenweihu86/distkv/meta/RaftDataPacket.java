package com.github.wenweihu86.distkv.meta;

import com.github.wenweihu86.distkv.api.CommonMessage;
import com.github.wenweihu86.distkv.api.MetaMessage;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 存储在raft集群中每个entry对应的data数据，数据格式是：
 * 固定长度RafaDataHeader 8字节 + RaftMetaData + Message
 * 其中Message类型根据RaftMetaData.requestType确定。
 */
@Setter
@Getter
@Slf4j
public class RaftDataPacket {
    @Setter
    @Getter
    public static class RaftDataHeader {
        private int metaLength;
        private int messageLenth;
    }

    public static final int FIXED_HEADER_LEN = 8;

    private RaftDataHeader header;
    private CommonMessage.RaftMetaData meta;
    private Message message;

    public static RaftDataPacket buildPacket(CommonMessage.RequestType requestType, Message message) {
        RaftDataPacket packet = new RaftDataPacket();
        CommonMessage.RaftMetaData.Builder metaBuilder = CommonMessage.RaftMetaData.newBuilder();
        metaBuilder.setRequestType(requestType);
        packet.setMeta(metaBuilder.build());
        packet.setMessage(message);
        return packet;
    }

    public static byte[] encode(RaftDataPacket packet) {
        int metaLen = packet.getMeta().getSerializedSize();
        int messageLen = packet.getMessage().getSerializedSize();
        int totalLen = FIXED_HEADER_LEN + metaLen + messageLen;
        byte[] outBytes = new byte[totalLen];
        ByteBuf outBuf = Unpooled.wrappedBuffer(outBytes);
        outBuf.clear();
        outBuf.writeInt(metaLen);
        outBuf.writeInt(messageLen);

        byte[] metaBytes = packet.getMeta().toByteArray();
        outBuf.writeBytes(metaBytes);

        byte[] messageBytes = packet.getMessage().toByteArray();
        outBuf.writeBytes(messageBytes);

        return outBytes;
    }

    public static RaftDataPacket decode(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        RaftDataPacket packet = new RaftDataPacket();

        RaftDataHeader header = new RaftDataHeader();
        header.setMetaLength(byteBuf.readInt());
        header.setMessageLenth(byteBuf.readInt());
        packet.setHeader(header);

        if (byteBuf.readableBytes() != header.getMetaLength() + header.getMessageLenth()) {
            log.error("invalid raft data");
            throw new RuntimeException("invalid raft data");
        }

        try {
            ByteBufInputStream inputStream = new ByteBufInputStream(byteBuf, header.getMetaLength());
            CommonMessage.RaftMetaData metaData = CommonMessage.RaftMetaData.newBuilder().mergeFrom(inputStream).build();
            packet.setMeta(metaData);

            inputStream = new ByteBufInputStream(byteBuf, header.getMessageLenth());
            Message message;
            if (metaData.getRequestType() == CommonMessage.RequestType.SET) {
                message = MetaMessage.SetRequest.newBuilder().mergeFrom(inputStream).build();
            } else if (metaData.getRequestType() == CommonMessage.RequestType.DELETE) {
                message = MetaMessage.DeleteRequest.newBuilder().mergeFrom(inputStream).build();
            } else {
                log.error("invalid request type GET");
                throw new RuntimeException("invalid request type GET");
            }
            packet.setMessage(message);
            return packet;
        } catch (Exception ex) {
            log.error("invalid raft data");
            throw new RuntimeException("invalid raft data");
        }
    }
}
