package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.dto.DishDto;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Dish;
import cn.edu.hbpu.reggie.entity.DishFlavor;
import cn.edu.hbpu.reggie.service.CategoryService;
import cn.edu.hbpu.reggie.service.DishFlavorService;
import cn.edu.hbpu.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        //新增某一个分类下的菜品，就精准删除整这个key和value
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("保存菜品成功！");
    }

    /**
     * 分页查询菜品
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize ,String name){
        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null,Dish::getName,name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,wrapper);

        return R.success(pageInfo);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        //修改某一个分类下的菜品，就精准删除整这个key和value
        String key = "dish_" + dishDto.getCategoryId();
        redisTemplate.delete(key);
        return R.success("修改菜品成功！");
    }

    /**
     * 根据菜品分类来查询菜品
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Dish::getCategoryId,dish.getCategoryId());
//
//        List<Dish> list = dishService.list(wrapper);
//        return R.success(list);
//    }

    /**
     * 根据菜品分类来查询菜品和口味
     * 用dto流的遍历，BeanUtils拷贝菜品信息还有设置菜品名和菜品口味到dishDto中
     * 然后返回一个dishDto
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        //设置缓存的key值，用分类id来拼接
        String key = "dish_" + dish.getCategoryId();

        //首先查询redis，根据分类的key来看是否有菜品数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果有就直接返回这个数据。
        if(dishDtoList != null){
            return R.success(dishDtoList);
        }

        //如果没有，就直接查询数据库，然后存入缓存。

        //构造条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId())
                .eq(Dish::getStatus,1).orderByAsc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(wrapper);

        //mp中只能用流遍历来解决dto拷贝问题
        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if(category != null){
                dishDto.setCategoryName(category.getName());
            }

            //构造flavor构造器
            LambdaQueryWrapper<DishFlavor> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper1);

            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
