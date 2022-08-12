package com.xyy.test;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

public class uploadFileTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        String fileName = "sdasdasd.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }

    //邮箱验证码
    @Test
    public void test2(){
        //创建HtmlEmail实例对象
        try {
           HtmlEmail email = new HtmlEmail();
        //设置邮箱的SMTP服务器，登录相对应的邮箱官网，去拿就行了,设置发送的字符集类型
           email.setHostName("smtp.qq.com");
           email.setCharset("utf-8");
        //设置收件人和发送人的邮箱和用户名
           email.addTo("2897745784@qq.com");
           email.setFrom("1145275181@qq.com","xyy");
        //设置邮箱地址和授权码
            email.setAuthentication("1145275181@qq.com","zorkxtffluebhceh");
        //设置发送标题和内容就可以了
            email.setMsg("邮箱测试");
            email.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * StringRedisTemplate.
     */
    @Test
    void testStringRedisTemplate(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("k1","v1");
        System.out.println(ops.get("k1"));

    }

}
