package com.api.rcode.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author ：河神
 * @date ：Created in 2021/10/22 9:39 上午
 */
public class BaseFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("BaseFilter---->#init");
    }

    @Override
    public void destroy() {
        System.out.println("BaseFilter#destroy");
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        System.out.println("BaseFilter#doFilter Start");
        filterChain.doFilter(servletRequest,servletResponse);
        System.out.println("BaseFilter#doFilter End、");
    }
}
