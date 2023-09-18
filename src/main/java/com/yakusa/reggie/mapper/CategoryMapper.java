package com.yakusa.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yakusa.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
