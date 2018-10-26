package com.github.wenweihu86.distkv.store;

import com.github.wenweihu86.distkv.api.CommonMessage;
import com.github.wenweihu86.distkv.api.StoreMessage;
import com.github.wenweihu86.raft.StateMachine;
import com.google.protobuf.ByteString;
import org.apache.commons.io.FileUtils;
import org.rocksdb.Checkpoint;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class StoreStateMachine implements StateMachine {
    private static final Logger LOG = LoggerFactory.getLogger(StoreStateMachine.class);
    private String dataDir;

    public StoreStateMachine(String dataDir) {
        this.dataDir = dataDir + File.separator + "rocksdb_data";;
    }

    static {
        RocksDB.loadLibrary();
    }

    private RocksDB db;

    @Override
    public void writeSnapshot(String snapshotDir) {
        Checkpoint checkpoint = Checkpoint.create(db);
        try {
            checkpoint.createCheckpoint(snapshotDir);
        } catch (Exception ex) {
            ex.printStackTrace();
            LOG.warn("writeSnapshot meet exception, dir={}, msg={}",
                    snapshotDir, ex.getMessage());
        }
    }

    @Override
    public void readSnapshot(String snapshotDir) {
        try {
            // copy snapshot dir to data dir
            if (db != null) {
                db.close();
            }
            File dataFile = new File(dataDir);
            if (dataFile.exists()) {
                FileUtils.deleteDirectory(dataFile);
            }
            File snapshotFile = new File(snapshotDir);
            if (snapshotFile.exists()) {
                FileUtils.copyDirectory(snapshotFile, dataFile);
            }
            // open rocksdb data dir
            Options options = new Options();
            options.setCreateIfMissing(true);
            db = RocksDB.open(options, dataDir);
        } catch (Exception ex) {
            LOG.warn("meet exception, msg={}", ex.getMessage());
        }
    }

    @Override
    public void apply(byte[] dataBytes) {
        RaftDataPacket packet = RaftDataPacket.decode(dataBytes);
        try {
            if (packet.getMeta().getRequestType() == CommonMessage.RequestType.SET) {
                StoreMessage.SetRequest request = (StoreMessage.SetRequest) packet.getMessage();
                db.put(request.getKey().toByteArray(), request.getValue().toByteArray());
            } else if (packet.getMeta().getRequestType() == CommonMessage.RequestType.DELETE) {
                StoreMessage.DeleteRequest request = (StoreMessage.DeleteRequest) packet.getMessage();
                db.delete(request.getKey().toByteArray());
            }
        } catch (Exception ex) {
            LOG.warn("apply meet exception: ", ex);
            throw new RuntimeException(ex);
        }
    }

    public StoreMessage.GetResponse get(StoreMessage.GetRequest request) {
        try {
            StoreMessage.GetResponse.Builder responseBuilder = StoreMessage.GetResponse.newBuilder();
            byte[] keyBytes = request.getKey().toByteArray();
            byte[] valueBytes = db.get(keyBytes);
            if (valueBytes != null) {
                responseBuilder.setValue(ByteString.copyFrom(valueBytes));
            }
            StoreMessage.GetResponse response = responseBuilder.build();
            return response;
        } catch (RocksDBException ex) {
            LOG.warn("read rockdb exception: ", ex);
            return null;
        }
    }

}
