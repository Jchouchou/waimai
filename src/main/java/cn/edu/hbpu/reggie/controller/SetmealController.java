package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.dto.SetmealDto;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Setmeal;
import cn.edu.hbpu.reggie.service.CategoryService;
import cn.edu.hbpu.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @Autowired
    CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null,Setmeal::getName,name);
        wrapper.select().orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,wrapper);

        //对象拷贝，不拷贝records
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if(category != null){
                String name1 = category.getName();
                setmealDto.setCategoryName(name1);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    @PostMapping
    //当调用保存套餐接口时，redis缓存就会删除所有的key和对应的value，然后将新的数据存入到缓存中
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("保存成功！");
    }

    @GetMapping("/{id}")
    @CachePut(value = "setmealCache", key = "#id")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateByIdWithDish(setmealDto);
        return R.success("修改成功！");
    }

    @DeleteMapping
    //当调用删除接口时，redis缓存就会删除所有的key和对应的value，然后将新的数据存入到缓存中
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除成功！");
    }


    @GetMapping("/list")
    //当缓存里面没有这个key对应的value时，就会将接口返回值存入缓存，
    //如果缓存可以查到的话，就不会调用这个接口。
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        wrapper.eq(Setmeal::getStatus,1);

        List<Setmeal> setmealList = setmealService.list(wrapper);
        return R.success(setmealList);
    }
}
