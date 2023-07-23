package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.BaseContext;
import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.entity.Orders;
import cn.edu.hbpu.reggie.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功！");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> userPage(int page, int pageSize){
        //查询userID
        Long userId = BaseContext.getCurrentId();

        //构造分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId,userId).orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }
}
