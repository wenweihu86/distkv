package com.github.wenweihu86.distkv.example.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.print.attribute.standard.Media;
import java.math.BigInteger;

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

    public static String getMd5(byte[] bytes) {
        String md5String = DigestUtils.md5Hex(bytes);
        return md5String;
    }

    public static String getFileExtension(String fileName) {
        String ext = null;
        String[] arr = fileName.split("\\.");
        if (arr.length >= 2) {
            ext = arr[arr.length - 1].toLowerCase();
        }
        return ext;
    }

    public static String convertExtToContentType(String ext) {
        String contentType = "text/plain; charset=utf-8";

        if (StringUtils.isNotBlank(ext)) {
            ext = ext.toLowerCase();
            if ("js".equals(ext)) {
                contentType = "text/javascript; charset=utf-8";
            } else if ("html".equals(ext) || "htm".equals(ext)) {
                contentType = "text/html; charset=utf-8";
            } else if ("css".equals(ext)) {
                contentType = "text/css; charset=utf-8";
            } else if ("jpg".equals(ext) || "jpeg".equals(ext)) {
                contentType = "image/jpeg";
            } else if ("png".equals(ext)) {
                contentType = "image/png";
            } else if ("gif".equals(ext)) {
                contentType = "image/gif";
            } else if ("bmp".equals(ext)) {
                contentType = "image/bmp";
            } else if ("tif".equals(ext)) {
                contentType = "image/tiff";
            } else if ("json".equals(ext)) {
                contentType = "text/plain; charset=utf-8";
            } else if ("pdf".equals(ext)) {
                contentType = "application/pdf; charset=utf-8";
            } else if ("zip".equals(ext) || "rar".equals(ext)) {
                contentType = "application/zip; charset=utf-8";
            } else {
                contentType = "application/x-" + ext + "; charset=utf-8";
            }
        }

        return contentType;
    }

}
