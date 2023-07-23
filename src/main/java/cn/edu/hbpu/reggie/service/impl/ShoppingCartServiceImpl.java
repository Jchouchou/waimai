package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.entity.ShoppingCart;
import cn.edu.hbpu.reggie.mapper.ShoppingCartMapper;
import cn.edu.hbpu.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
