package cn.edu.hbpu.reggie.mapper;

import cn.edu.hbpu.reggie.entity.Category;
import cn.edu.hbpu.reggie.entity.Dish;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
