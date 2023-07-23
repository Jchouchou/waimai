package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.entity.OrderDetail;
import cn.edu.hbpu.reggie.mapper.OrderDetailMapper;
import cn.edu.hbpu.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}