package com.api.rcode.servlet.front;

import com.api.rcode.mvc.HandlerDP;
import com.api.rcode.mvc.HandlerManger;
import org.apache.catalina.connector.RequestFacade;
import org.apache.coyote.Request;
import org.apache.coyote.http2.Http2Error;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author ：河神
 * @date ：Created in 2022/2/11 10:31 上午
 */
public class DispatcherServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        RequestFacade facadeReq = (RequestFacade) req;
        dispatcher(facadeReq,resp);
    }

    private void dispatcher(RequestFacade facadeReq, HttpServletResponse resp) throws IOException {

        String requestURI = facadeReq.getRequestURI();
        HandlerDP handlerDP = HandlerManger.getHandlerDP(requestURI);
        resp.setContentType("text/html;charset=UTF-8");

        if (handlerDP==null){
            resp.setStatus(404);
            resp.getWriter().println("页面不存在");
            return;
        }

        Map<String, String[]> parameterMap = facadeReq.getParameterMap();

        try {

            handlerDP.preHandler();
            Object invoke = handlerDP.invoke(parameterMap);
            handlerDP.postHandler();

            resp.getWriter().println(invoke);
        } catch (Exception e) {
            resp.setStatus(502);
            e.printStackTrace();
            resp.getWriter().println("发送错误"+e.getMessage());
        }
    }

}
