package com.Lchasi.train.batch.config;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan("com.Lchasi")//自动扫描并注册components，services,controllers等spring管理的bean
@MapperScan("com.Lchasi.train.*.mapper")
@EnableFeignClients("com.Lchasi.train.batch.feign")
public class BatchApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BatchApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BatchApplication.class);
        Environment enx = app.run(args).getEnvironment();//拿到系统环境
        LOG.info("启动成功！！");
        LOG.info("地址：\thttp://127.0.0.1:{}:{}", enx.getProperty("server.port") ,enx.getProperty("server.servlet.context-path"));//获得系统端口
//        SpringApplication.run(MemberApplication.class, args);
    }

}
