package cn.edu.hbpu.reggie.service;

import cn.edu.hbpu.reggie.dto.DishDto;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    public void saveWithFlavor(DishDto dishDto);

    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
