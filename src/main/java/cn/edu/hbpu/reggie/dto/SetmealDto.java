package cn.edu.hbpu.reggie.dto;

import cn.edu.hbpu.reggie.entity.Setmeal;
import cn.edu.hbpu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
