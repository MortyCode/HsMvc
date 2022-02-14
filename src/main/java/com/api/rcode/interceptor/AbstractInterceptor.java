package com.api.rcode.interceptor;

/**
 * @author ：河神
 * @date ：Created in 2022/2/14 1:52 下午
 */
public interface AbstractInterceptor {

    void preHandle();
    void postHandle();
    void afterCompletion();

}
