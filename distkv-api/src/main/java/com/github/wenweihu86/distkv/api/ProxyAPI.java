package com.github.wenweihu86.distkv.api;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public interface ProxyAPI {
    ProxyMessage.SetResponse set(ProxyMessage.SetRequest request);
    ProxyMessage.GetResponse get(ProxyMessage.GetRequest request);
    ProxyMessage.DeleteResponse delete(ProxyMessage.DeleteRequest request);
}
