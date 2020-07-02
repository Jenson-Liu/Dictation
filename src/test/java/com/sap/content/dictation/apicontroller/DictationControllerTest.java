package com.sap.content.dictation.apicontroller;

import com.sap.content.dictation.DictationApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * @author : Jenson.Liu
 * @date : 2020/6/10  1:37 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DictationApplication.class)
@AutoConfigureMockMvc
public class DictationControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        //MockMvcBuilders.webAppContextSetup(WebApplicationContext context)：指定WebApplicationContext，将会从该上下文获取相应的控制器并得到相应的MockMvc；
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();//建议使用这种
    }


    @Test
    public void identifyImgTess4jChinese() {
        try {
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .post("/dictation/identifyImgTess4jChinese")
                    .requestAttr("file", new File("/Users/i501695/GitHUbProject/EN_ProductIntergration/Dictation/src/main/resources/testFiles/Chi.jpeg"))
            ).andReturn();
            System.out.println(mvcResult.getResponse());
            System.out.println(mvcResult.getResponse().getContentAsString());
            System.out.println(mvcResult.getResponse().getStatus());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get() {
        try {
            MvcResult mvcResult =  mockMvc.perform(MockMvcRequestBuilders
                    .get("/dictation/hi")).andReturn();
            System.out.println(mvcResult.getResponse());
            System.out.println(mvcResult.getResponse().getContentAsString());
            System.out.println(mvcResult.getResponse().getStatus());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}