package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.entity.Employee;
import cn.edu.hbpu.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.print.Pageable;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        // 1.给密码md5加密
        String password = employee.getPassword();
//        System.out.println(password);
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2.根据页面提供的用户名来查询数据库中的username
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3.如果没有查到则返回失败！
        if(emp==null){
            return R.error("你所输入的用户名不存在！");
        }

        // 4.密码的比对
        if(!emp.getPassword().equals(password)){
            return R.error("你所输入的密码不正确！");
        }

        // 5.查看员工状态，若为禁用状态则返回已禁用。
        if(emp.getStatus() == 0){
            return R.error("该账号已封禁！");
        }

        // 6.登陆成功，将员工的id存入session中并返回结果。
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 新增员工信息
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        // 设置默认密码并md5加密处理
        log.info("新增员工，员工信息:{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("添加成功！");
    }

    /**
     * 分页操作
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {} , pageSize = {} , name = {}",page,pageSize,name);
        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        // 添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改账号状态
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info(employee.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long emp = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(emp);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    /**
     * 根据员工id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据员工id查询员工信息...");
        Employee emp = employeeService.getById(id);
        if(emp != null){
            return R.success(emp);
        }
        return R.error("未查到该员工信息！");
    }

}

















