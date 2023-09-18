package com.yakusa.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakusa.reggie.dto.DishDto;
import com.yakusa.reggie.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品,同时新增菜品口味，需要操作两张表
    public void saveWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息和对应的口味信息
    public  DishDto getByIdWithFlavor(Long id);

    //更新菜品信息和对应的口味信息
     public void updateWithFlavor(DishDto dishDto);

     //删除菜品信息和对应的口味信息
    public void removeWithFlavor(List<Long> ids);



}
