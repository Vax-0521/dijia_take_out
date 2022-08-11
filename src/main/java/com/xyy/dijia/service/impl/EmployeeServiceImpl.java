package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.Employee;
import com.xyy.dijia.mapper.EmployeeMapper;
import com.xyy.dijia.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


}
