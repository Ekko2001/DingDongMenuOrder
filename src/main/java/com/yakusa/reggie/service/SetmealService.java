package com.yakusa.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yakusa.reggie.dto.SetmealDto;
import com.yakusa.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public  void saveWithDish(SetmealDto setmealDto);

    public  void removeWithDish(List<Long> ids);

   public SetmealDto getByIdWithDish(Long id);

   public void updateWithDish(SetmealDto setmealDto);
}
