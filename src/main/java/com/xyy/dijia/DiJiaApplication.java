package com.xyy.dijia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
//日志实现

@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement //开启事务注解
@EnableCaching //开启SpringCache注解方式缓存功能
public class DiJiaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiJiaApplication.class,args);
        log.info("启动成功");  //输出info级别日志
    }
}
