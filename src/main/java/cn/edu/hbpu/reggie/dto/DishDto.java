package cn.edu.hbpu.reggie.dto;

import cn.edu.hbpu.reggie.entity.Dish;
import cn.edu.hbpu.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
