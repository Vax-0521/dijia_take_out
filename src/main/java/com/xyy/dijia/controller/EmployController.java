package com.xyy.dijia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyy.dijia.common.R;
import com.xyy.dijia.entity.Employee;
import com.xyy.dijia.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployController {
    @Resource //@Autowired默认按类型装配，@Resource按名称装配
    private EmployeeService employeeService;

    /**
     *登陆方法
     * @param employee
     * @return
     */
    @PostMapping("/login")
    // HttpServletRequest对象，它需要把employee对象信息存到session里一份
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码password进行md5加密(DigestUtils工具类的MD5加密方法)
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employ = employeeService.getOne(queryWrapper);//用户名为unique，调用getOne
        //3.判断，如果没查到返回登录失败结果
        if(employ == null){
            return R.error("用户不存在，登录失败");
        }
        //4.密码对比，不一致返回失败
        if (!employ.getPassword().equals(password)){
            return R.error("密码错误，登录失败");
        }
        //5.查看员工状态，如果为禁用，返回禁用结果
        if(employ.getStatus() == 0){
            return R.error("账号已被禁用");
        }
        //6.登录成功，将员工id存入session，返回成功
        request.getSession().setAttribute("employee",employ.getId());
        return R.success(employ);
    }

    /**
     * 退出方法
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //1.清理Session中保存的当前员工的id
        request.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    /**
     * 添加员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());

        //设置初始密码123456，md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());  //当前记录创建时间
//        employee.setUpdateTime(LocalDateTime.now());  //当前记录更新时间
//        //获取当前登录用户的id
//        employee.setCreateUser((Long) request.getSession().getAttribute("employee"));
//        //更新的用户同当前登录用户
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("信息：{}",employee.toString());

        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
        employeeService.updateById(employee);

        long id = Thread.currentThread().getId();
        log.info("线程id：{}",id);

        return R.success("员工状态信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息。。。");

        Employee employee = employeeService.getById(id);
        if (employee != null){
        return R.success(employee);
        }
        return R.error("无该员工信息");
    }





}
