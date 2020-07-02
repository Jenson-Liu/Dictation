package com.sap.content.dictation.server;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Jenson.Liu
 * @date : 2020/5/27  1:49 下午
 */
@Service
public class OcrServer {
    // 应用ID  (必须为webapi类型应用，并印刷文字识别服务，参考帖子如何创建一个webapi应用：http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=36481)
    private static final String APPID = "5ec5e508";
    // 接口密钥 (webapi类型应用开通印刷文字识别多语种服务后，控制台--我的应用---印刷文字识别多语种---服务的apikey)
    private static final String API_KEY = "72c734b1a3c40f4928b0f0fecc355f41";
    // 是否返回位置信息
    private static final String LOCATION = "false";
    // 语种(可选值：en（英文），cn|en（中文或中英混合)
    private static final String LANGUAGE = "cn|en";

    /**
     * OCR WebAPI 调用示例程序
     *
     * @param args
     * @throws IOException
     */

    /**
     * 组装http请求头
     */
    public static Map<String, String> buildHttpHeader() throws UnsupportedEncodingException {
        String curTime = System.currentTimeMillis() / 1000L + "";
        String param = "{\"location\":\"" + LOCATION + "\",\"language\":\"" + LANGUAGE + "\"}";
        String paramBase64 = new String(Base64.encodeBase64(param.getBytes("UTF-8")));
        String checkSum = DigestUtils.md5Hex(API_KEY + curTime + paramBase64);
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        header.put("X-Param", paramBase64);
        header.put("X-CurTime", curTime);
        header.put("X-CheckSum", checkSum);
        header.put("X-Appid", APPID);
        return header;
    }
}
