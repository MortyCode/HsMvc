package com.api.rcode.mvc;

import com.api.rcode.enums.Interceptor;
import com.api.rcode.interceptor.AbstractInterceptor;

/**
 * @author ：河神
 * @date ：Created in 2022/2/14 2:15 下午
 */
public class InterceptorDP implements AbstractInterceptor{

    private AbstractInterceptor interceptor;
    private String[] path;
    private int order;

    public InterceptorDP(Class<?> objClazz) throws InstantiationException, IllegalAccessException {
        Class<Interceptor> interceptorClass = Interceptor.class;
        Interceptor interceptor = objClazz.getAnnotation(interceptorClass);
        this.path = interceptor.value();
        this.order = interceptor.order();
        this.interceptor = (AbstractInterceptor) objClazz.newInstance();
    }

    public boolean isMatching(String url){
        return true;
    }

    public String[] getPath() {
        return path;
    }

    public int getOrder() {
        return order;
    }


    @Override
    public void preHandle() {
         interceptor.preHandle();
    }

    @Override
    public void postHandle() {
        interceptor.postHandle();
    }

    @Override
    public void afterCompletion() {
        interceptor.afterCompletion();
    }
}
