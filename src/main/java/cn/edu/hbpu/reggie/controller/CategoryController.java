package cn.edu.hbpu.reggie.controller;

import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    /**
     * 添加菜品或者套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功！");
    }

    /**
     * 分页查询菜品和套餐分类
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        // 构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.select().orderByAsc(Category::getSort);

        categoryService.page(pageInfo,wrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类信息
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){
        categoryService.remove(ids);
        return R.success("删除成功！");
    }

    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("该分类id为:{}",category.getId());
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    /**
     * 显示菜品分类信息
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        // 构造条件构造器
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        // 添加条件
        wrapper.eq((category.getType() != null),Category::getType,category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(wrapper);

        return R.success(list);
    }
}
