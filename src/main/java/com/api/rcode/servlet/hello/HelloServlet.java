package com.api.rcode.servlet.hello;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ：河神
 * @date ：Created in 2021/1/25 2:35 下午
 */
public class HelloServlet extends HttpServlet {

    ServletConfig servletConfig;

    public int i = 1;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doGet AA");
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("doGet 返回数据-" + i);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        i--;
        System.out.println("doDelete AA");
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("doDelete返回数据-" + i);

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        i++;
        System.out.println("doPut AA");
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("doPut返回数据-" + i);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        i++;
        resp.setContentType("text/html;charset=UTF-8");
        resp.getWriter().println("doPost返回数据-" + i);
    }



    @Override
    public String getServletInfo() {
        return "hello";
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}
