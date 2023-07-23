package cn.edu.hbpu.reggie.service.impl;

import cn.edu.hbpu.reggie.common.CustomException;
import cn.edu.hbpu.reggie.common.R;
import cn.edu.hbpu.reggie.dto.SetmealDto;
import cn.edu.hbpu.reggie.entity.Setmeal;
import cn.edu.hbpu.reggie.entity.SetmealDish;
import cn.edu.hbpu.reggie.mapper.SetmealMapper;
import cn.edu.hbpu.reggie.service.SetmealDishService;
import cn.edu.hbpu.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 保存套餐分类和套餐分类的菜品
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存Setmeal里的数据
        this.save(setmealDto);

        //获取setmealId
        Long setmealId = setmealDto.getId();

        //保存SetmealDish里的数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }


    /**
     * 根据套餐id获取需要修改的套餐数据并回显
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        //将setmeal数据复制到setmealDto中
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询setmealDish数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(wrapper);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    @Transactional
    public void updateByIdWithDish(SetmealDto setmealDto) {
        //修改setmeal数据
        this.updateById(setmealDto);

        //先删除setmealDish数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(wrapper);

        //再新增setmealDish数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());


        setmealDishService.saveBatch(setmealDishes);
    }

    //事务操作
    @Transactional
    @Override
    public void removeWithDish(List<Long> ids) {
        //首先判断套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);

        //统计出有多少条符合条件的数据
        int count = this.count(wrapper);
        if(count > 0){
            //售卖中的套餐不能删除
            throw new CustomException("套餐正在售卖中无法删除！");
        }

        //删除setmeal数据
        this.removeByIds(ids);

        //删除setmealdish数据
        LambdaQueryWrapper<SetmealDish> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper1);

    }


}
