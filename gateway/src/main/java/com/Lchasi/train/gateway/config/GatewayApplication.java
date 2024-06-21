package com.Lchasi.train.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.Lchasi")//自动扫描并注册components，services,controllers等spring管理的bean
public class GatewayApplication {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatewayApplication.class);
        Environment enx = app.run(args).getEnvironment();//拿到系统环境
        LOG.info("启动成功！！");
        LOG.info("网关地址：\thttp://127.0.0.1:{}", enx.getProperty("server.port"));//获得系统端口

    }

}
