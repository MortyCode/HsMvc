package com.api.rcode.interceptor;

import com.api.rcode.enums.Interceptor;

/**
 * @author ：河神
 * @date ：Created in 2022/2/14 11:17 上午
 */
@Interceptor(value = {"/home/*","/h1"})
public class HomeInterceptor implements AbstractInterceptor{

    @Override
    public void preHandle() {
        System.out.println("preHandle");
    }

    @Override
    public void postHandle() {
        System.out.println("postHandle");
    }

    @Override
    public void afterCompletion() {
        System.out.println("afterCompletion");
    }
}
