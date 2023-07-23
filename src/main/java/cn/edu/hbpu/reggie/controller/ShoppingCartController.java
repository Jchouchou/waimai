package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.BaseContext;
import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.entity.ShoppingCart;
import cn.edu.hbpu.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品或者套餐到购物车中
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //获取线程中的userId
        Long userId = BaseContext.getCurrentId();
//        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);

        //判断shoppingCart里面是否有菜品相同或者套餐相同的
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId())
                .or()
                .eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(wrapper);
        if(shoppingCartOne == null){
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shoppingCartOne=shoppingCart;
        }
        else{
            int number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 修改购物车中的菜品或者套餐的数量
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId())
                .or()
                .eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(wrapper);
        int number = shoppingCartOne.getNumber();

        if(number != 1){
            shoppingCartOne.setNumber(number - 1);
            shoppingCartService.updateById(shoppingCartOne);
        }
        else{
            shoppingCartOne.setNumber(0);
            shoppingCartService.removeById(shoppingCartOne.getId());
        }
        return R.success(shoppingCartOne);
    }

    /**
     * 根据当前用户id来查询购物车所有数据
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId).orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(wrapper);
        return R.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,userId);
        shoppingCartService.remove(wrapper);
        return R.success("清空成功！");
    }
}
