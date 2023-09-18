package com.yakusa.reggie.dto;

import com.yakusa.reggie.entity.Setmeal;
import com.yakusa.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;//套餐菜品属性

    private String categoryName;
}
