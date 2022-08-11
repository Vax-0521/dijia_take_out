package com.xyy.dijia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.dto.SetmealDto;
import com.xyy.dijia.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    //新增套餐同时保存套餐和菜品关联关系
    public void saveWithDish(SetmealDto setmealDto);

    //删除套餐同时删除套餐和菜品的关联数据
    public void removeWithDish(List<Long> ids);

    //修改/批量修改菜品售卖状态
    public boolean updateStatusWithDish(Integer status,List<Long> ids);

    //修改菜品同时修改菜品对应的口味数据
    public void updateWithDish(SetmealDto dto);

    //根据id查询套餐
    public SetmealDto getByIdWithDish(Long id);
}
