package com.yakusa.reggie.controller;

/*
套餐管理
 */
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.dto.SetmealDto;
import com.yakusa.reggie.entity.Category;
import com.yakusa.reggie.entity.Dish;
import com.yakusa.reggie.entity.Setmeal;
import com.yakusa.reggie.entity.SetmealDish;
import com.yakusa.reggie.service.CategoryService;
import com.yakusa.reggie.service.SetmealDishService;
import com.yakusa.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/setmeal")

public class SetmealController {
@Autowired
    private SetmealService setmealService;
@Autowired
    private SetmealDishService setmealDishService;
@Autowired
private CategoryService categoryService;
@Autowired
private RedisTemplate redisTemplate;

//新增套餐
@PostMapping
@CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody  SetmealDto setmealDto){
    log.info("套餐信息:{}",setmealDto);
   setmealService.saveWithDish(setmealDto);
    return R.success("新增套餐成功");
}

//分页查询显示
    @GetMapping("/page")
public  R<Page> page(int page,int pageSize,String name){
    //分页构造器对象
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //设置查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);//分页查询,但是这里没有查询到套餐分类名称

        //拷贝对象
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");//除了records属性，其他的都拷贝过去
        List<Setmeal> records = pageInfo.getRecords();//获取records属性重新赋值

        //把records由List<Setmeal>转换成List<SetmealDto>
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto(); //创建一个新的dto对象
            BeanUtils.copyProperties(item,setmealDto); //拷贝item属性到setmealDto属性里面

            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                setmealDto.setCategoryName(category.getName());//获取分类名称并赋值给setmealDto对象
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);//重新设置records属性
        return R.success(dtoPage);

    }

//删除套餐(单个和批量)
@DeleteMapping
@CacheEvict(value = "setmealCache",allEntries = true)
    public  R<String> delete(@RequestParam List<Long> ids){
    log.info("删除套餐的id:{}",ids);

    setmealService.removeWithDish(ids);

    return R.success("删除套餐成功");
    }

    //更新套餐为停售状态(单个和批量)
    @PostMapping("/status/0")
    public R<String> statusStop(@RequestParam List<Long> ids){
        log.info("修改菜品状态");

        //update setmeal set status = 0 where id = ids 这条SQL语句来写
        for (Long id : ids){ //遍历菜品id列表,逐个更新状态
            Setmeal setmeal = setmealService.getById(id);
            if (  setmeal!=null){
                setmeal.setStatus(0);//设置dish的状态为0
                LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Setmeal::getId,id);
              setmealService.update( setmeal,wrapper);
            }
        }
        return R.success("停售状态设置成功");
    }


    //更新套餐为启用状态(单个和批量)
    @PostMapping("/status/1")
    public R<String> statusStart(@RequestParam List<Long> ids){
        log.info("修改菜品状态");
        //update setmeal set status = 1 where id = ids 这条SQL语句来写
        for (Long id : ids){ //遍历菜品id列表,逐个更新状态
            Setmeal setmeal = setmealService.getById(id);
            if (  setmeal!=null){
                setmeal.setStatus(1);//设置dish的状态为0
                LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Setmeal::getId,id);
                setmealService.update( setmeal,wrapper);
            }
        }
        return R.success("启用状态设置成功");
    }

   //回显套餐信息
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("回显套餐信息");
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    //更新套餐信息
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("更新套餐信息");
        setmealService.updateWithDish(setmealDto);

        //删除redis中的缓存
        String keys = "setmeal_" + setmealDto.getCategoryId() + "_1";
        redisTemplate.delete(keys);

        return R.success("更新套餐信息成功");
    }



     @GetMapping("/list")
    //@Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        String key = "setmeal_" + setmeal.getCategoryId() + "_" + setmeal.getStatus();//拼接key
        List<Setmeal> list=null;
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);

        if (list!=null){
            log.info("从缓存中获取套餐列表");
            return R.success(list);
        }

        log.info("查询套餐列表");
        //构造条件构造器对象
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        //根据前端传入的分类id查询
        wrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        wrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());//根据前端传入的状态查询
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        list = setmealService.list(wrapper);

        redisTemplate.opsForValue().set(key,list,5, TimeUnit.MINUTES);


        return R.success(list);
    }



}
