package com.yakusa.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.entity.Employee;
import com.yakusa.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    /**
     *员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //1.获取页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库内用户的信息
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        //封装查询条件
        wrapper.eq(Employee::getUsername, employee.getUsername());
        //根据用户名执行查询.
        Employee emp = employeeService.getOne(wrapper);
       //3.判断用户名是否存在
        if (emp ==null){
        return R.error("登录失败,用户不存在");
        }

        //4.判断密码是否正确
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败，密码错误");
        }

        //5.判断账户是否被禁用
        if (emp.getStatus()==0){
            return R.error("账户已禁用");
        }


        //6.将用户信息保存到session中
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     */
@PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清楚session中保持的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 添加员工
     */
@PostMapping
    public R<String> save(@RequestBody Employee employee,HttpServletRequest request){
        log.info("员工信息:{}",employee);
        //设置默认初始密码
    employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
    //设置创建时间和更新时间
   // employee.setCreateTime(LocalDateTime.now());
  //  employee.setUpdateTime(LocalDateTime.now());

    //获取当前登录用户的id,作为创建人和修改人
   // Long employeeId=(Long)request.getSession().getAttribute("employee");
   // employee.setCreateUser(employeeId);
   // employee.setUpdateUser(employeeId);

    //调用业务层方法保存员工信息
    employeeService.save(employee);

        return R.success("新员工添加成功");
    }

    /**
     * 设置分页显示员工信息
     */
@GetMapping("/page")
public R<Page> page(int page,int pageSize,String name){
    log.info("当前页:{},每页条数:{},查询条件:{}",page,pageSize,name);
    //构造分页构造器
    Page<Employee> pageInfo = new Page<>(page,pageSize);

    //构造条件构造器
    LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
    wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);//添加过滤条件
    wrapper.orderByDesc(Employee::getUpdateTime);//添加排序条件

    //执行查询
    employeeService.page(pageInfo,wrapper);
    return R.success(pageInfo);
}

/**
 * 启动和禁用员工账户以及编辑员工信息,此处update()方法为通用方法
 */
@PutMapping
    public R<String>update(@RequestBody Employee employee){
    log.info("员工信息:{}",employee);
    //    long id = Thread.currentThread().getId();
    //    log.info("当前线程id:{}",id);
    //获取当前登录用户的id
    // Long empId =(Long) request.getSession().getAttribute("employee");
    //设置修改时间和修改人
   // employee.setUpdateUser(empId);
   // employee.setUpdateTime(LocalDateTime.now());

    employeeService.updateById(employee);
    return R.success("启用/禁用成功");
}

/**
 * 回显员工信息
 */
  @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable("id") Long id){
      Employee employee = employeeService.getById(id);
    if (employee==null) {
        return R.error("员工不存在");
    }
    return R.success(employee);
  }














}
