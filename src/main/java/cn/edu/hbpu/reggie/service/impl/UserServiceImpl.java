package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.entity.User;
import cn.edu.hbpu.reggie.mapper.UserMapper;
import cn.edu.hbpu.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
