package com.Lchasi.train.business.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.Lchasi")//自动扫描并注册components，services,controllers等spring管理的bean
@MapperScan("com.Lchasi.train.*.mapper")
@EnableFeignClients("com/Lchasi/train/business/feign")//标识哪个包下为feign
@EnableCaching//开启缓存
public class BusinessApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessApplication.class);
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BusinessApplication.class);
        Environment enx = app.run(args).getEnvironment();//拿到系统环境
        LOG.info("启动成功！！");
        LOG.info("地址：\thttp://127.0.0.1:{}:{}", enx.getProperty("server.port") ,enx.getProperty("server.servlet.context-path"));//获得系统端口
//        SpringApplication.run(MemberApplication.class, args);
        //限流规则
        initFlowRules();
        LOG.info("已定义限流规则");
    }
    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("doConfirm");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(20);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }

}
