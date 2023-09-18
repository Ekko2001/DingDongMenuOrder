package com.yakusa.reggie.dto;

import com.yakusa.reggie.entity.Dish;
import com.yakusa.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {
    //增加了一个flavors属性，用于接收前端传递的口味名称和口味数据list
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
