package com.github.wenweihu86.distkv.api;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public interface StoreAPI {
    StoreMessage.SetResponse set(StoreMessage.SetRequest request);
    StoreMessage.GetResponse get(StoreMessage.GetRequest request);
    StoreMessage.DeleteResponse delete(StoreMessage.DeleteRequest request);
}
