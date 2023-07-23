package cn.edu.hbpu.reggie.service;

import cn.edu.hbpu.reggie.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
