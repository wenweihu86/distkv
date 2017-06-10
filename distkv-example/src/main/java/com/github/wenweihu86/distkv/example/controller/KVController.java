package com.github.wenweihu86.distkv.example.controller;

import com.github.wenweihu86.distkv.api.CommonMessage;
import com.github.wenweihu86.distkv.api.ProxyAPI;
import com.github.wenweihu86.distkv.api.ProxyMessage;
import com.github.wenweihu86.distkv.example.vo.CommonVo;
import com.github.wenweihu86.distkv.example.vo.KVGetVo;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wenweihu86 on 2017/6/10.
 */
@RestController
public class KVController {

    private static final Logger LOG = LoggerFactory.getLogger(KVController.class);

    @Autowired
    private ProxyAPI proxyAPI;

    @RequestMapping("/kv/put")
    public CommonVo kvPut(@RequestParam(value = "key") String key,
                        @RequestParam(value = "value") String value) {
        ProxyMessage.SetRequest request = ProxyMessage.SetRequest.newBuilder()
                .setKey(ByteString.copyFrom(key.getBytes()))
                .setValue(ByteString.copyFrom(value.getBytes())).build();
        ProxyMessage.SetResponse response = proxyAPI.set(request);

        CommonVo commonVo = new CommonVo();
        if (response == null
                || response.getBaseRes().getResCode()
                != CommonMessage.ResCode.RES_CODE_SUCCESS) {
            commonVo.setResCode(CommonMessage.ResCode.RES_CODE_FAIL_VALUE);
            return commonVo;
        }
        commonVo.setResCode(CommonMessage.ResCode.RES_CODE_SUCCESS_VALUE);
        return commonVo;
    }

    @RequestMapping("/kv/get")
    public KVGetVo kvGet(@RequestParam(value = "key") String key) {
        ProxyMessage.GetRequest request = ProxyMessage.GetRequest.newBuilder()
                .setKey(ByteString.copyFrom(key.getBytes())).build();
        ProxyMessage.GetResponse response = proxyAPI.get(request);

        KVGetVo kvGetVo = new KVGetVo();
        kvGetVo.setKey(key);

        if (response == null
                || response.getBaseRes().getResCode()
                != CommonMessage.ResCode.RES_CODE_SUCCESS) {
            kvGetVo.setResCode(CommonMessage.ResCode.RES_CODE_FAIL_VALUE);
            return kvGetVo;
        }
        kvGetVo.setResCode(CommonMessage.ResCode.RES_CODE_SUCCESS_VALUE);
        String value = new String(response.getValue().toByteArray());
        kvGetVo.setValue(value);
        LOG.info("/kv/get, key={}, resCode={}, value={}",
                key, response.getBaseRes().getResCode(),
                new String(response.getValue().toByteArray()));
        return kvGetVo;
    }

    @RequestMapping("/kv/delete")
    public CommonVo kvDelete(@RequestParam(value = "key") String key) {
        ProxyMessage.DeleteRequest request = ProxyMessage.DeleteRequest.newBuilder()
                .setKey(ByteString.copyFrom(key.getBytes())).build();
        ProxyMessage.DeleteResponse response = proxyAPI.delete(request);
        CommonVo commonVo = new CommonVo();
        if (response == null
                || response.getBaseRes().getResCode()
                != CommonMessage.ResCode.RES_CODE_SUCCESS) {
            commonVo.setResCode(CommonMessage.ResCode.RES_CODE_FAIL_VALUE);
            return commonVo;
        }
        commonVo.setResCode(CommonMessage.ResCode.RES_CODE_SUCCESS_VALUE);
        return commonVo;
    }

}
