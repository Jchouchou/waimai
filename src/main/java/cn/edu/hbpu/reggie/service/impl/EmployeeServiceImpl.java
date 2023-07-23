package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.entity.Employee;
import cn.edu.hbpu.reggie.mapper.EmployeeMapper;
import cn.edu.hbpu.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
