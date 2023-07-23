package cn.edu.hbpu.reggie.service;

import cn.edu.hbpu.reggie.dto.SetmealDto;
import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public SetmealDto getByIdWithDish(Long id);

    public void updateByIdWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<Long> ids);
}

