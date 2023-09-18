package com.yakusa.reggie.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.dto.DishDto;
import com.yakusa.reggie.entity.Dish;
import com.yakusa.reggie.entity.DishFlavor;
import com.yakusa.reggie.mapper.DishMapper;
import com.yakusa.reggie.service.DishFlavorService;
import com.yakusa.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional //事务注解
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表dish
        this.save(dishDto);

        //获取菜品id,填充到dish_flavor表中
        Long dishId = dishDto.getId();

        //获取菜品口味数据，填充到dish_flavor表中
        List<DishFlavor> flavors = dishDto.getFlavors();
        //遍历菜品口味数据，填充dishId
        flavors.forEach(flavor -> {
            flavor.setDishId(dishId);
        });

        //保存菜品口味到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品信息
        Dish byId = this.getById(id);

        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, byId.getId());//根据菜品id查询菜品口味信息
        List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);

        //单独查询菜品分类信息和菜品口味信息,然后将两者合并给DishDto对象中统一返回
        DishDto dishDto =new DishDto();//构造一个Dto对象
        dishDto.setFlavors(dishFlavors);//设置DishDto菜品口味信息
        BeanUtils.copyProperties(byId,dishDto);//拷贝对象,将byId的属性拷贝到dishDto中

        return dishDto;
    }
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
    this.updateById(dishDto);//更新dish菜品表基本信息

    Long dishId = dishDto.getId(); //菜品id
    //删除dish_flavor表中的数据
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(wrapper);

    //添加dish_flavor表中的数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(flavor -> {
            flavor.setDishId(dishId);
        });
        dishFlavorService.saveBatch(flavors);
    }



    @Transactional
    @Override //删除菜品信息和对应的口味信息
    public void removeWithFlavor(List<Long> ids) {
   //delete from dish where id = ids
    this.removeByIds(ids);//删除菜品信息 ---dish表

    //删除菜品口味信息 ---dish_flavor表 ,两种方法删除:第一种是遍历, 第二种是查询条件对象用wrapper.in()方法封装ids
    //delete from dish_flavor where dish_id in (1,2,3)
//        for (Long id :ids){
//            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(DishFlavor::getDishId,id);
//            dishFlavorService.remove(wrapper);
//        }

    LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
    wrapper.in(DishFlavor::getDishId,ids);//根据菜品id删除菜品口味信息
    dishFlavorService.remove(wrapper);//删除菜品口味信息
    }


}
