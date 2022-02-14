package com.api.rcode.mvc;

import com.api.rcode.utils.ASMUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 11:57 上午
 */
public class HandlerDP {


    private final Method method;
    private final Object object;
    private  List<InterceptorDP> interceptorList;
    private final Map<String,Parameter> parameterMap = new HashMap<>();

    public HandlerDP(Object object,String[] parameterName,Method method) {
        this.method = method;
        this.object = object;
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            this.parameterMap.put(parameterName[i],parameters[i]);
        }
    }

    public Object invoke(Map<String,String[]>  argsMap) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Object[] args = new Object[parameterMap.size()];
        for (Map.Entry<String, Parameter> entry : parameterMap.entrySet()) {
            String k = entry.getKey();
            Parameter parameter = entry.getValue();

            String argIndex = parameter.getName().replace("arg", "");
            int index = Integer.parseInt(argIndex);
            Class<?> type = parameter.getType();
            //数组判断
            boolean array = type.isArray();
            String[] reqArg = argsMap.get(k);
            if (array){
                if (reqArg!=null){
                    Class<?> componentType = type.getComponentType();
                    Object argData = Array.newInstance(componentType, reqArg.length);
                    for (int i = 0; i < reqArg.length; i++) {
                        Array.set(argData,i,primitiveConvert(componentType, reqArg[i]));
                    }
                    args[index] = argData;
                }
            }else{
                if (reqArg==null||reqArg.length==0){
                    args[index] = primitiveConvert(type, null);
                }else{
                    args[index] = primitiveConvert(type, reqArg[0]);
                }
            }
        }
        return method.invoke(object,args);
    }

    public List<InterceptorDP> getInterceptorList() {
        return interceptorList;
    }

    public void preHandler(){
        for (InterceptorDP interceptorDP : interceptorList) {
            interceptorDP.preHandle();
        }
    }

    public void postHandler(){
        for (int i = interceptorList.size() - 1; i >= 0; i--) {
            interceptorList.get(i).postHandle();
        }
    }


    public void addInterceptorDP(InterceptorDP interceptorDP) {
        if (this.interceptorList==null){
            this.interceptorList = new ArrayList<>();
        }
        this.interceptorList.add(interceptorDP);
        this.interceptorList.sort(Comparator.comparing(InterceptorDP::getOrder));
    }

    public Object  primitiveConvert(Class<?> type, String o){
        //基础类型判断
        boolean primitive = type.isPrimitive();
        if (primitive&&o==null){
            switch (type.getName()){
                case "boolean":
                    return false;
                case "int":
                case "byte":
                case "short":
                    return 0;
                case "long":
                    return 0L;
                default: throw new RuntimeException("type error ");
            }
        }else{
            switch (type.getName()){
                case "boolean":
                case "java.lang.Boolean":
                    return Boolean.valueOf(o);
                case "byte":
                case "java.lang.Byte":
                    return Byte.valueOf(o);
                case "int":
                case "java.lang.Integer":
                case "short":
                case "java.lang.Short":
                    return Integer.valueOf(o);
                case "long":
                case "java.lang.Long":
                    return Long.valueOf(o.toString());
                default: throw new RuntimeException("type error ");
            }
        }
    }


}
