package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.User;
import com.xyy.dijia.mapper.UserMapper;
import com.xyy.dijia.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
