package com.yakusa.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.common.CustomException;
import com.yakusa.reggie.entity.Category;
import com.yakusa.reggie.entity.Dish;
import com.yakusa.reggie.entity.Setmeal;
import com.yakusa.reggie.mapper.CategoryMapper;
import com.yakusa.reggie.service.CategoryService;
import com.yakusa.reggie.service.DishService;
import com.yakusa.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {
@Autowired
private DishService dishService;
@Autowired
private SetmealService setmealService;


    @Override
    public void remove(Long ids) {
        //判断该分类下是否关联有菜品
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getCategoryId,ids);
        int count = dishService.count(wrapper);
        if (count>0){
            throw new CustomException("该分类下有菜品，不能删除");
        }

        //判断该分类下是否关联有套餐
        LambdaQueryWrapper<Setmeal> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(Setmeal::getCategoryId,ids);
        int count1 = setmealService.count(wrapper1);
        if (count1>0){
            throw new CustomException("该套餐下有菜品，不能删除");
        }

        //删除分类
        super.removeById(ids);
    }
}
