package com.xyy.dijia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyy.dijia.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
