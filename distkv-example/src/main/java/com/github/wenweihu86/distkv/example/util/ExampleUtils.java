package com.github.wenweihu86.distkv.example.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wenweihu86 on 2017/6/8.
 */
public class ExampleUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleUtils.class);
    private static final JsonFormat.Printer PRINTER = JsonFormat.printer().omittingInsignificantWhitespace();

    public static String protoToJson(MessageOrBuilder message) {
        try {
            return PRINTER.print(message);
        } catch (InvalidProtocolBufferException ex) {
            LOG.warn("get exception: ", ex);
            return "";
        }
    }

}
