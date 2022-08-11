package com.xyy.dijia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyy.dijia.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
