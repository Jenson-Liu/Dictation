package com.sap.content.dictation.apicontroller;

import com.sap.content.dictation.APi.ZkApi;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author : Jenson.Liu
 * @date : 2020/6/2  11:23 上午
 */
@RequestMapping("/zk")
@Controller
public class ZkController {

    @Resource
    ZkApi zkApi;

    @ResponseBody
    @RequestMapping(value = "/addNode",method = RequestMethod.GET)
    public boolean addNode(@RequestParam("path")String path, @RequestParam("data")String data){
        return zkApi.createNode(path,data);
    }

    @ResponseBody
    @RequestMapping(value = "/deleteNode",method = RequestMethod.GET)
    public boolean deleteNode(@RequestParam("path")String path, @RequestParam("data")String data){
        return zkApi.createNode(path,data);
    }
}
