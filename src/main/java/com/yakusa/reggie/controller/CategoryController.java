package com.yakusa.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yakusa.reggie.common.R;
import com.yakusa.reggie.entity.Category;
import com.yakusa.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    //添加分类(包含菜品分类和套餐分类)
    @PostMapping
    public R<String> addCategory(@RequestBody Category category) {
        log.info("添加分类：{}", category);
        categoryService.save(category);
        return R.success("添加成功");
    }
    //设置分页显示分类信息
    @GetMapping("/page")
    public R<Page> Page( int page, int pageSize) {
        log.info("分页查询分类：pageNum={},pageSize={}", page, pageSize);
        //分页构造器
        Page<Category> PageInfo = new Page<>(page, pageSize);

        //条件构造器,  构造排序条件 按照升序排列
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(PageInfo, wrapper);

        return R.success(PageInfo);
    }

    //删除分类
    @DeleteMapping
public R<String> deleteCategory(  Long ids) {
    log.info("删除分类：{}", ids);
    categoryService.remove(ids);//自定义一个删除方法

    return R.success("删除成功");
}


    //更新分类
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        log.info("更新分类：{}", category);
        categoryService.updateById(category);
        return R.success("更新成功");
    }

    //查询下拉框显示分类
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        log.info("查询分类：{}", category);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
    //按类型查询
        wrapper.eq(category.getType()!=null,Category::getType,category.getType());
    //先按排序查再按时间查
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(wrapper);


        return R.success(list);
    }





}
