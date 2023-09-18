package com.yakusa.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.dto.DishDto;
import com.yakusa.reggie.entity.Category;
import com.yakusa.reggie.entity.Dish;
import com.yakusa.reggie.entity.DishFlavor;
import com.yakusa.reggie.service.CategoryService;
import com.yakusa.reggie.service.DishFlavorService;
import com.yakusa.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/dish")

public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;


    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("添加菜品：{}", dishDto);
        //dishService.save(dishDto);
        dishService.saveWithFlavor(dishDto);
        //删除redis中的缓存
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("添加成功");
    }

    //分页查询
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
       //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);//目的是重新单独设置这个page对象的Records,加入菜品名称来显示
        //构造条件构造器对象
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name!=null ,Dish::getName, name);
        wrapper.orderByDesc(Dish::getUpdateTime);

        //分页查询
        dishService.page(pageInfo, wrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();//把Records单独拿处理处理

        List<DishDto> list = records.stream().map((item) -> {
        DishDto dishDto =new DishDto();//创建DishDto实体类对象目的是设置categoryName的值

       BeanUtils.copyProperties(item,dishDto);//拷贝Dish属性到dishDto中，防止Dish属性全为null
       Long categoryId = item.getCategoryId();//获取分类Id,item就是Dish

        Category category = categoryService.getById(categoryId);   //根据id查询分类对象,目的是获取分类的名称

        String categoryName = category.getName(); //获取分类名称

        dishDto.setCategoryName(categoryName);//设置dishDto中的菜品名

        return  dishDto;
        }).collect(Collectors.toList());


        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }
    //回显菜品信息
    @GetMapping("/{id}")
    public  R<DishDto> get(@PathVariable Long id){
        log.info("查询菜品：{}",id);
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }
    //更新菜品信息
    @PutMapping
    public  R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品：{}",dishDto);
        dishService.updateWithFlavor(dishDto);

        //删除redis中的缓存
        String keys = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);


        return R.success("修改成功");
    }

    //删除菜品
    @DeleteMapping
    public  R<String> delete( @RequestParam List<Long> ids){
        log.info("删除菜品：{}",ids);
        dishService.removeWithFlavor(ids);

        return R.success("删除成功");
    }


    //更新菜品为停售状态(单个和批量)
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        log.info("修改菜品状态");

        //根据update dish set status = 0 where id = ids 这条SQL语句来写
//        Dish dish = dishService.getById(ids);//根据id查询获取到dish对象
//        dish.setStatus(0);//设置dish的状态为0
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Dish::getId,ids);
//        dishService.update(dish,wrapper);

           for (Long id : ids){ //遍历菜品id列表,逐个更新状态
               Dish dish = dishService.getById(id);//根据id查询获取到dish对象
               if ( dish!=null){
                   dish.setStatus(0);//设置dish的状态为0
                   LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
                   wrapper.eq(Dish::getId,id);
                   dishService.update(dish,wrapper);
               }
            }
        return R.success("停售状态设置成功");
    }

    //更新菜品为启用状态(单个和批量)
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        log.info("修改菜品状态");
        //根据update dish set status = 1 where id = ids 这条SQL语句来写
//        Dish dish = dishService.getById(ids);//根据id查询获取到dish对象
//        dish.setStatus(1);//设置dish的状态为1
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Dish::getId,ids);
//        dishService.update(dish,wrapper);
        for (Long id :ids){
            Dish dish = dishService.getById(id);
            if (dish!=null){
                dish.setStatus(1);
                LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Dish::getId,id);
                dishService.update(dish,wrapper);
            }
        }
        return R.success("启用状态设置成功");
    }



    //在新增套餐功能查询对应的菜品数据
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info("查询菜品列表");
//        //构造条件构造器对象
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        //根据前端传入的分类id查询
//        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        wrapper.eq(Dish::getStatus,1);//只需要查询到在售状态的菜品
//        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(wrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList =null;

        String key ="dish_"+dish.getCategoryId()+"_"+dish.getStatus();//dish_112343244212_1  动态拼接key
        //从redis中获取菜品列表
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果redis中有菜品列表，就直接返回
        if (dishDtoList!=null){
            log.info("从redis中获取菜品列表");
            return R.success(dishDtoList);
        }


        //构造查询条件,    如果redis中没有菜品列表，就从数据库中查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在,需要查询数据库,将查询到的菜品数据缓存到redis中
        redisTemplate.opsForValue().set(key,dishDtoList,5, TimeUnit.MINUTES);


        return R.success(dishDtoList);
    }



}
