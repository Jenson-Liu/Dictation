package com.sap.content.dictation.until;

/**
 * @author : Jenson.Liu
 * @date : 2020/6/11  5:45 下午
 */
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
public class TranscriberDemo {

    public static void main(String[] args) throws Exception {

        Configuration configuration = new Configuration();
//        configuration.setAcousticModelPath("resource:/zh-cn/zh-cn");
//        configuration.setDictionaryPath("resource:/zh-cn/zh-cn.dict");
//        configuration.setLanguageModelPath("resource:/zh-cn/zh-cn.lm.bin");
        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
//        configuration.setAcousticModelPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/zh-cn/zh-cn");
//        configuration.setDictionaryPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/zh-cn/zh-cn.dic");
//        configuration.setLanguageModelPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/zh-cn/zh-cn.lm.bin");
//        configuration.setAcousticModelPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/en-us/en-us");
//        configuration.setDictionaryPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/en-us/en-us.dict");
//        configuration.setLanguageModelPath("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/en-us/en-us.lm.bin");



        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        InputStream stream = new FileInputStream(new File("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/testFiles/export.wav"));

        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
        }
        recognizer.stopRecognition();
    }
}
