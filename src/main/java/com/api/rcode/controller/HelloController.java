package com.api.rcode.controller;

import com.api.rcode.enums.RequestMapping;
import com.api.rcode.enums.RestController;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 10:49 上午
 */
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(String hp1,String hp2,String hp3){
        return "hello";
    }

}
