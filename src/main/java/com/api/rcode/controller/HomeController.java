package com.api.rcode.controller;

import com.alibaba.fastjson.JSON;
import com.api.rcode.enums.RequestMapping;
import com.api.rcode.enums.RestController;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 10:49 上午
 */
@RestController("/home")
public class HomeController {


    @RequestMapping("/h1")
    public String h1(String p1,Integer p2){
        System.out.println(p1+" "+p2);
        return "h1";
    }

    @RequestMapping("/h2")
    public String h2(boolean p1, int p2){
        System.out.println(p1+" "+p2);
        return "h2 p1: "+p1+" p2:"+p2;
    }

    @RequestMapping("/h3")
    public String h3(int[] a2){
        return "h2 p1: "+ JSON.toJSONString(a2);
    }

}
