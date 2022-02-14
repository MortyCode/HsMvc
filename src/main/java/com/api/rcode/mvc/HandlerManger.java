package com.api.rcode.mvc;

import com.api.rcode.enums.RequestMapping;
import com.api.rcode.enums.RestController;
import com.api.rcode.utils.ASMUtils;
import com.api.rcode.utils.ClassUtils;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 10:47 上午
 */
public class HandlerManger {

    private static final Map<String,HandlerDP> handlerDPMap = new HashMap<>();

    public static HandlerDP getHandlerDP(String url){
        return handlerDPMap.get(url);
    }

    public static void init() throws ClassNotFoundException {
        Class<RestController> restControllerClass = RestController.class;
        List<Class<?>> allControllerClass = ClassUtils.getAllClass("com.api.rcode.controller", e -> {
            RestController restController = e.getAnnotation(restControllerClass);
            if (restController == null) {
                return false;
            }
            System.out.println(e.getName());
            return true;
        });

        Class<RequestMapping> requestMappingClass = RequestMapping.class;

        for (Class<?> controllerClass : allControllerClass) {
            RestController restController = controllerClass.getAnnotation(restControllerClass);
            Method[] methods = controllerClass.getMethods();
            Object object = null;
            try {
                object = controllerClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Map<String, String[]> parameterMap = getParameterMap(controllerClass);

            for (Method method : methods) {
                RequestMapping requestMapping = method.getAnnotation(requestMappingClass);
                if (requestMapping==null){
                    continue;
                }
                String path = restController.value()+ requestMapping.value();
                handlerDPMap.put(path,new HandlerDP(object,parameterMap.get(method.getName()),method));
            }
        }
    }

    public static void main(String[] args) throws ClassNotFoundException {
        init();
    }


    private static Map<String,String[]> getParameterMap(Class<?> controllerClass){
        Map<Executable, String[]> parameterMap = ASMUtils.getParameterMap(controllerClass);
        if (parameterMap==null){
            throw new RuntimeException("获取参数失败");
        }

        Map<String,String[]> data = new HashMap<>();
        parameterMap.forEach((k,v)->{
            data.put(k.getName(),v);
        });
        return data;
    }


}
