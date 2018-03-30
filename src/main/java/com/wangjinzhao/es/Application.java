package com.wangjinzhao.es;


import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;


/**
 * Created by WANGJINZHAO on 2017/5/2.
 *
 * @SpringBootApplication annotation is equivalent to using
 * @Configuration, @EnableAutoConfiguration and @ComponentScan with their default attributes.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan
//@ServletComponentScan  //自动加载servlet相关的注解@WebServlet @WebFilter等
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static int port = 8888;
    private static int jettyThreadPoolSize = 400;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();
        factory.setPort(8888);
        factory.addServerCustomizers(new JettyServerCustomizer() {
            @Override
            public void customize(Server server) {
                {
                    ((QueuedThreadPool) server.getThreadPool()).setMaxThreads(jettyThreadPoolSize);
                    MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
                    server.addEventListener(mbContainer);
                    server.addBean(mbContainer);
                    for (Connector connector : server.getConnectors()) {
                        if (connector instanceof ServerConnector) {
                            ServerConnector serverConnector = (ServerConnector) connector;
                            //acceptors：接收线程数量，Acceptor的功能是接收客户端连接然后分配个给ThreadPool处理，表示同时在监听read事件的线程数，缺省值为2，对于NIO来说，建议值2*（处理器核数-1）；或者小于等于2*处理器核数
                            try {
                                serverConnector.accept(2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            serverConnector.setIdleTimeout(60 * 1000);
                            serverConnector.setAcceptQueueSize(512);
                            Collection<ConnectionFactory> collection = serverConnector.getConnectionFactories();

                            for (ConnectionFactory connectionfactory : collection) {
                                if (connectionfactory instanceof HttpConnectionFactory) {
                                    // HttpConnectionFactory
                                    // httpConnectionFactory=(HttpConnectionFactory)connectionfactory;
                                    // HttpConfiguration
                                    // httpConfig=httpConnectionFactory.getHttpConfiguration();
                                    // httpConnectionFactory.setInputBufferSize(1024*256);

                                }
                            }

                        }
                    }

                }
            }
        });
        return factory;
    }
}
