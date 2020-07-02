package com.sap.content.dictation.apicontroller;

import com.sap.content.dictation.server.WebIATWS;
import com.sap.content.dictation.tool.MultipartFileToFile;
import com.sap.content.dictation.until.FileUtil;
import com.sap.content.dictation.until.HttpUtil;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import static com.sap.content.dictation.server.OcrServer.buildHttpHeader;
import static com.sap.content.dictation.server.WebIATWS.getAuthUrl;

;

/**
 * @author : Jenson.Liu
 * @date : 2020/5/26  1:54 下午
 */
@Controller
@Api(tags = "识别API详细")
@RequestMapping("/dictation")
public class DictationController {
    private static final String hostUrl = "https://iat-api.xfyun.cn/v2/iat"; //中英文，http url 不支持解析 ws/wss schema
    // private static final String hostUrl = "https://iat-niche-api.xfyun.cn/v2/iat";//小语种
    private static final String apiKey = "0a30253b8abadc6eb886ed5ae9da20f3"; //在控制台-我的应用-语音听写（流式版）获取
    private static final String apiSecret = "8305c1e6f9a377a1afe4e581fcd8abc7"; //在控制台-我的应用-语音听写（流式版）获取
    // OCR webapi 接口地址
    private static final String WEBOCR_URL = "https://webapi.xfyun.cn/v1/service/v1/ocr/general";

    private static final Logger logger = LoggerFactory.getLogger(DictationController.class);

    @Autowired
    ResourceLoader resourceLoader;

    @ResponseBody
    @ApiOperation(value = "开源语音识别接口",notes = "只支持WAV格式")
    @RequestMapping(value = "/identifyVoiceWithOS",method = RequestMethod.POST)
    public String identifyVoiceWithOS(@RequestParam(name = "file", required = true) @ApiParam(name = "file",value = "上传的语音文件",required = true) MultipartFile multipartFile) throws Exception {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        File file = MultipartFileToFile.multipartFileToFile(multipartFile);
        InputStream stream = new FileInputStream(file);
        recognizer.startRecognition(stream);
        SpeechResult result;
        String message = "";
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
            message+=result.getHypothesis();
        }
        file.delete();
        return message;
    }

    @ResponseBody
    @ApiOperation(value = "讯飞语音识别接口",notes = "需要上传语音文件")
    @RequestMapping(value = "/identifyVoice",method = RequestMethod.POST)
    public String identifyVoice(@RequestParam(name = "file", required = true) @ApiParam(name = "file",value = "上传的语音文件",required = true) MultipartFile multipartFile) throws Exception {
        File file = MultipartFileToFile.multipartFileToFile(multipartFile);
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        //将url中的 schema http://和https://分别替换为ws:// 和 wss://
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        //System.out.println(url);
        Request request = new Request.Builder().url(url).build();
        // System.out.println(client.newCall(request).execute());
        //System.out.println("url===>" + url);
        WebIATWS webIATWS = new WebIATWS(file);
        new Thread(()->{
            WebSocket webSocket = client.newWebSocket(request, webIATWS);
        }).start();
        Thread.sleep(4000);
        if (webIATWS.getMessage() == null){
            return "please try again";
        }else {
            return webIATWS.getMessage();
        }
    }

    @ResponseBody
    @ApiOperation(value = "讯飞图片识别接口",notes = "需要上传图片")
    @RequestMapping(value = "/identifyImg",method = RequestMethod.POST)
    public String identifyImg(@RequestParam(name = "file",required = true) @ApiParam(name = "file",value = "上传的图片文件",required = true) MultipartFile multipartFile) throws Exception {
        Map<String, String> header = buildHttpHeader();
        File file = MultipartFileToFile.multipartFileToFile(multipartFile);
        InputStream in = new FileInputStream(file);
        byte[] imageByteArray = FileUtil.inputStream2ByteArray(in);
        String imageBase64 = new String(Base64.encodeBase64(imageByteArray), "UTF-8");
        String result = HttpUtil.doPost1(WEBOCR_URL, header, "image=" + URLEncoder.encode(imageBase64, "UTF-8"));
        JSONObject jsonObject= JSONObject.fromObject(result);
        //获取data的json
        JSONObject dataJson = (JSONObject) jsonObject.get("data");
        /**
         * 获取data下block的jsonArray，取其第一个array
         */
        JSONArray jsonArray = dataJson.getJSONArray("block");
        Object blockObject =  jsonArray.get(0);
        /**
         * 将第一个array转化为JSONObject
         */
        JSONObject blockJson= JSONObject.fromObject(blockObject);
        JSONArray lines = blockJson.getJSONArray("line");
        String response = "";
        for (Object line:lines){
            JSONObject Json= JSONObject.fromObject(line);
            JSONArray words = Json.getJSONArray("word");
            for (Object word:words){
                JSONObject wordJson= JSONObject.fromObject(word);
                response += wordJson.get("content");
            }
            response += "\t";
        }
        return response.toString();

    }

    @ResponseBody
    @ApiOperation(value = "开源英文图片识别接口",notes = "识别图片为英文")
    @RequestMapping(value = "/identifyImgTess4jEnglish",method = RequestMethod.POST)
    public String identifyImgTess4jEnglish(@RequestParam(name = "file", required = true) @ApiParam(name = "file",value = "上传的图片文件",required = true) MultipartFile multipartFile) throws Exception {
        File file = MultipartFileToFile.multipartFileToFile(multipartFile);
        ITesseract tesseract = new Tesseract();
        String source = System.getProperty("user.dir");
        System.out.println(source);
        String path = source + "/tessdata";
        tesseract.setDatapath(path);
        tesseract.setLanguage("eng");
        String result = tesseract.doOCR(file);
        file.delete();
        return result;
    }

    @ResponseBody
    @ApiOperation(value = "开源中文图片识别接口",notes = "识别图片为中文")
    @RequestMapping(value = "/identifyImgTess4jChinese",method = RequestMethod.POST)
    public String identifyImgTess4jChinese(@RequestParam(name = "file", required = true) @ApiParam(name = "file",value = "上传的图片文件",required = true) MultipartFile multipartFile) throws Exception {
        File file = MultipartFileToFile.multipartFileToFile(multipartFile);
        ITesseract tesseract = new Tesseract();
        String source = System.getProperty("user.dir");
        System.out.println(source);
        String path = source + "/tessdata";
        System.out.println(path);
        tesseract.setDatapath(path);
        tesseract.setLanguage("chi_sim");
        String result = tesseract.doOCR(file);
        file.delete();
        return result;
    }

    @PostConstruct
    @RequestMapping(value = "/init",method = RequestMethod.POST)
    public void init () throws IOException {
        Resource chi_simResource = resourceLoader.getResource("classpath:tessdata/chi_sim.traineddata");
        Resource chi_sim_vertResource = resourceLoader.getResource("classpath:tessdata/chi_sim_vert.traineddata");
        Resource engResource = resourceLoader.getResource("classpath:tessdata/eng.traineddata");
        MultipartFileToFile.copyFile(chi_simResource);
        MultipartFileToFile.copyFile(chi_sim_vertResource);
        MultipartFileToFile.copyFile(engResource);
    }

}
