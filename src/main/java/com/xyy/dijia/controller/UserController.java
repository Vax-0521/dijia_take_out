package com.xyy.dijia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xyy.dijia.common.R;
import com.xyy.dijia.entity.User;
import com.xyy.dijia.service.UserService;
import com.xyy.dijia.utils.SMSUtils;
import com.xyy.dijia.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);

            //调用aliyun短信服务api发送短信
            //SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
            //将生成的验证码保存到session中
            //session.setAttribute(phone,code);

            //将生成的验证码缓存到redis，设置有效期五分钟
            redisTemplate.opsForValue().set(phone,code,5l, TimeUnit.MINUTES);

            return R.success("发送成功");

        }
        return R.error("发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);


        //进行验证码比对（页面传过来的验证码和session中保存的验证码）
        if (codeInSession != null && codeInSession.equals(code)){
            //比对成功，登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
                //判断当前手机号用户是否为新用户，是就自动完成注册，添加进数据库
                user = new User();
                user.setPhone(phone);

                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果登陆成功，就删除缓存的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登录失败");
    }




}
