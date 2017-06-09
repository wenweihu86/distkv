package com.github.wenweihu86.distkv.api;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public interface MetaAPI {
    MetaMessage.SetResponse set(MetaMessage.SetRequest request);
    MetaMessage.GetResponse get(MetaMessage.GetRequest request);
    MetaMessage.DeleteResponse delete(MetaMessage.DeleteRequest request);
}
