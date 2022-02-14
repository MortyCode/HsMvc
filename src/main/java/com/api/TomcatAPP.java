package com.api;

import com.api.rcode.filter.BaseFilter;
import com.api.rcode.mvc.HandlerManger;
import com.api.rcode.servlet.front.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.JreMemoryLeakPreventionListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.core.ThreadLocalLeakPreventionListener;
import org.apache.catalina.mbeans.GlobalResourcesLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.VersionLoggerListener;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import java.io.File;

/**
 * @author ：河神
 * @date ：Created in 2021/1/24 10:11 下午
 */
public class TomcatAPP {

    public static void main(String[] args) throws LifecycleException {
        // 设置端口
        int port = 8080;
        Tomcat tomcat = new Tomcat();

        tomcat.setBaseDir("heshen");
        tomcat.setPort(port);

        // 设置Host
        Host host = tomcat.getHost();
        host.setAppBase("webapps");

        // 添加listener
        StandardServer server = (StandardServer) tomcat.getServer();
        server.addLifecycleListener(new AprLifecycleListener());
        server.addLifecycleListener(new JreMemoryLeakPreventionListener());
        server.addLifecycleListener(new VersionLoggerListener());
        server.addLifecycleListener(new GlobalResourcesLifecycleListener());
        server.addLifecycleListener(new ThreadLocalLeakPreventionListener());

        // 设置contextPath和路径
        String contextPath = "";
        String docBase = new File("./").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(8080);
        tomcat.getService().addConnector(connector);

        //Wrapper
//        tomcat.addServlet(contextPath, "hello", new HelloServlet()).addMapping("/hello");
//        tomcat.addServlet(contextPath, "hello2", new Hello2Servlet()).addMapping("/hello2");

        tomcat.addServlet(contextPath, "DispatcherServlet", new DispatcherServlet()).addMapping("/*");

        try {
            HandlerManger.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //增加filter
        FilterDef filterDef = new FilterDef();
        filterDef.setFilter(new BaseFilter());
        filterDef.setFilterName(BaseFilter.class.getName());
        filterDef.setFilterClass(BaseFilter.class.getName());
        context.addFilterDef(filterDef);

        //增加filter映射
        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName(BaseFilter.class.getName());
        filterMap.addURLPattern("/*");
        context.addFilterMap(filterMap);

        // 启动tomcat
        tomcat.start();

        // 进入监听状态,如果不进入监听状态,启动tomcat后就会关闭tomcat
        tomcat.getServer().await();
    }

}
