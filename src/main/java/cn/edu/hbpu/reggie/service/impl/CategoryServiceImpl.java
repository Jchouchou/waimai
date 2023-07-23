package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.common.CustomException;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Dish;
import cn.edu.hbpu.reggie.entity.Setmeal;
import cn.edu.hbpu.reggie.mapper.CategoryMapper;
import cn.edu.hbpu.reggie.service.CategoryService;
import cn.edu.hbpu.reggie.service.DishService;
import cn.edu.hbpu.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id来查询
        dishWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishWrapper);

        //查询当前分类是否关联了菜品，若关联，则抛出一个业务异常。
        if(count1 > 0){
            // 已经关联菜品，抛出一个业务异常
            throw new CustomException("当前分类关联了菜品，不能删除！");
        }

        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id来查询
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealWrapper);

        //查询当前分类是否关联了套餐，若关联，则抛出一个业务异常。
        if(count2 > 0){
            // 已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类关联了套餐，不能删除！");
        }

        //正常删除分类
        super.removeById(id);
    }
}
