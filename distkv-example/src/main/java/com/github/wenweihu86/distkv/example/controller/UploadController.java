package com.github.wenweihu86.distkv.example.controller;

import com.github.wenweihu86.distkv.api.CommonMessage;
import com.github.wenweihu86.distkv.api.ProxyAPI;
import com.github.wenweihu86.distkv.api.ProxyMessage;
import com.github.wenweihu86.distkv.example.util.ExampleUtils;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private ProxyAPI proxyAPI;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        LOG.info("Request Received for Index Page ");
        return "index.html";
    }

    /**
     * POST /uploadFile -> receive and locally save a file.
     *
     * @param uploadFile The uploaded file as Multipart file parameter in the
     *                   HTTP request. The RequestParam name must be the same of the attribute
     *                   "name" in the input tag with type file.
     * @return An http OK status in case of success, an http 4xx status in case
     * of errors.
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> uploadFile(@RequestParam("uploadFile") MultipartFile uploadFile) {
        LOG.info("Request Received for File Upload ");
        try {
            String filename = uploadFile.getOriginalFilename();
            String key = ExampleUtils.getMd5(uploadFile.getBytes())
                    + "." + ExampleUtils.getFileExtension(filename);
            ProxyMessage.SetRequest request = ProxyMessage.SetRequest.newBuilder()
                    .setKey(ByteString.copyFrom(key.getBytes()))
                    .setValue(ByteString.copyFrom(uploadFile.getBytes()))
                    .build();
            ProxyMessage.SetResponse response = proxyAPI.set(request);
            if (response == null
                    || response.getBaseRes().getResCode()
                    != CommonMessage.ResCode.RES_CODE_SUCCESS) {
                LOG.warn("request proxy failed, fileName={}", filename);
                return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
            }
            LOG.info("upload file success, fileName={}", key);
            return new ResponseEntity<>(key, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = "/download/{key:.+}")
    @ResponseBody
    public ResponseEntity<?> downloadFile(@PathVariable String key) {
        ProxyMessage.GetRequest request = ProxyMessage.GetRequest.newBuilder()
                .setKey(ByteString.copyFrom(key.getBytes())).build();
        ProxyMessage.GetResponse response = proxyAPI.get(request);
        if (response == null
                || response.getBaseRes().getResCode()
                != CommonMessage.ResCode.RES_CODE_SUCCESS) {
            LOG.warn("request proxy failed, fileName={}", key);
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
        LOG.info("/download success, key={}", key);
        String fileExt = ExampleUtils.getFileExtension(key);
        String contentType = ExampleUtils.convertExtToContentType(fileExt);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(contentType));
        return new ResponseEntity<>(
                response.getValue().toByteArray(),
                headers, HttpStatus.CREATED);
    }


}

