package com.github.wenweihu86.distkv.proxy;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class ProxyUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyUtils.class);
    private static final JsonFormat.Printer PRINTER = JsonFormat.printer().omittingInsignificantWhitespace();

    public static String protoToJson(MessageOrBuilder message) {
        try {
            return PRINTER.print(message);
        } catch (InvalidProtocolBufferException ex) {
            LOG.warn("get exception: ", ex);
            return "";
        }
    }

    public static long getMd5Sign(byte[] bytes) {
        String md5String = DigestUtils.md5Hex(bytes);
        BigInteger bigInt = new BigInteger(md5String, 16);
        long sign = bigInt.longValue();
        if (sign == Long.MIN_VALUE) {
            sign = Long.MAX_VALUE;
        } else if (sign < 0) {
            sign = 0 - sign;
        }
        return sign;
    }

}
