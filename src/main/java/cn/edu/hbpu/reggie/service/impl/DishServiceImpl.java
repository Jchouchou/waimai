package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.dto.DishDto;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Dish;
import cn.edu.hbpu.reggie.entity.DishFlavor;
import cn.edu.hbpu.reggie.mapper.CategoryMapper;
import cn.edu.hbpu.reggie.mapper.DishMapper;
import cn.edu.hbpu.reggie.service.CategoryService;
import cn.edu.hbpu.reggie.service.DishFlavorService;
import cn.edu.hbpu.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存菜品口味
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        // 保存菜品到菜品dish表
        this.save(dishDto);

        // 菜品id
        Long dishId = dishDto.getId();

        // 菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        // 保存菜品口味数据到dishFlavor表
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id来查询菜品和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish dish = this.getById(id);

        //将dish属性复制到dishDto里
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(wrapper);

        //再给dishDto设置刚查出来的flavor值
        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    /**
     * 更新dish数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表数据
        this.updateById(dishDto);

        //先清理dishFlavor表flavor数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(wrapper);

        //保存更新的flavor数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
}
