package com.yakusa.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.dto.SetmealDto;
import com.yakusa.reggie.entity.Setmeal;
import com.yakusa.reggie.entity.SetmealDish;
import com.yakusa.reggie.mapper.SetmealMapper;
import com.yakusa.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishServiceImpl setmealDishService;



    //新增套餐,同时新增套餐和菜品的关联关系
    @Transactional //对于数据库的操作，要么全部成功，要么全部失败
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
    //保存套餐基本信息 ,操作setmeal表
        this.save(setmealDto);

    //保存套餐和菜品的关联关系,操作setmeal_dish表
        setmealDto.getSetmealDishes().forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealDto.getId());
        });
        //批量保存
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }



/*
删除套餐,同时需要删除套餐和菜品的关联关系
 */
@Transactional //对于数据库的操作，要么全部成功，要么全部失败
@Override
public void removeWithDish(List<Long> ids) {
    //删除套餐基本信息 ---setmeal表
    this.removeByIds(ids);

    //删除套餐和菜品的关联关系 --- setmael_dish表
    //delete from setmeal_dish where setmeal_id in (1,2,3)
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.in(SetmealDish::getSetmealId,ids);

    setmealDishService.remove(queryWrapper);
}

    @Override
    public SetmealDto getByIdWithDish(Long id) {
    //查询套餐基本信息 select * from setmeal where id = id
    Setmeal setmeal = this.getById(id);

    //查询套餐和菜品的关联关系 select * from setmeal_dish where setmeal_id = id
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SetmealDish::getSetmealId,id);
    List<SetmealDish> list = setmealDishService.list(queryWrapper);

        //封装数据
        SetmealDto setmealDto = new SetmealDto();
        setmealDto.setSetmealDishes(list);
        BeanUtils.copyProperties(setmeal,setmealDto);

        return setmealDto;
    }

    //修改套餐,同时修改套餐和菜品的关联关系
    @Transactional //对于数据库的操作，要么全部成功，要么全部失败
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
    //修改套餐setmeal基本信息
    this.updateById(setmealDto);

    //修改套餐和菜品的关联关系
    //先删除原来的关联关系 delete from setmeal_dish where setmeal_id = id
    LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
    setmealDishService.remove(queryWrapper);

    //再新增新的关联关系  insert into setmeal_dish values(1,1),(1,2),(1,3)
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();//获取套餐菜品属性
        Long id = setmealDto.getId();//获取套餐id
        setmealDishes.forEach(setmealDish -> {//遍历套餐菜品属性
        setmealDish.setSetmealId(id);//设置套餐id
    });
    setmealDishService.saveBatch(setmealDishes );//批量保存
    }

}

