package com.Lchasi.train.batch.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 适合单体应用，不适合集群。 同一个任务已经有节点在跑，其他的就不需要跑，因此不选用springboot自带的定时调度
 */
@Component
@EnableScheduling//开启定时任务
public class SpringBootTestJob {

    //定时任务三大要素：1.执行的内容：功能逻辑 2.执行的策略：cron表达式 3.开关：开启定时任务
    @Scheduled(cron = "0/5 * * * * ?")//cron从左到右（用空格隔开）：秒 分 小时 月份中的日期 月份 星期中的日期 年份     0/5秒数处于5余数是0则触发
    private void test(){
        System.out.println("SpringBootTestJob");
    }
}
