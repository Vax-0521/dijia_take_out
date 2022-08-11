package com.xyy.dijia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品同时插入菜品对应的口味数据
    public void saveWithFlavor(DishDto dto);

    //根据id查询菜品以及菜品对应的口味数据
    public DishDto getByIdWithFlavor(Long id);

    //修改菜品同时修改菜品对应的口味数据
    public void updateWithFlavor(DishDto dto);

    //删除菜品同时删除菜品对应的口味数据
    public void removeWithFlavor(List<Long> ids);

    //修改/批量修改菜品售卖状态
    public boolean updateStatusWithFlavor(Integer status,List<Long> ids);



}
