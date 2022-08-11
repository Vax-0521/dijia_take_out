package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.common.CustomException;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.entity.Dish;
import com.xyy.dijia.entity.DishFlavor;
import com.xyy.dijia.entity.Setmeal;
import com.xyy.dijia.mapper.DishMapper;
import com.xyy.dijia.service.DishFlavorService;
import com.xyy.dijia.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dto) {
        //保存菜品的基本信息dish
        this.save(dto);
        //获取菜品的id，要存到dish_flavor
        Long dishId = dto.getId();
        List<DishFlavor> flavors = dto.getFlavors();
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());
        //保存菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息以及口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，dish表
        Dish dish = this.getById(id);

        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应口味信息，dish_flavor表
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 修改菜品同时修改菜品对应的口味数据
     * @param dto
     */
    @Override
    public void updateWithFlavor(DishDto dto) {
        //更新dish表
        this.updateById(dto);
        //先清除当前菜品的对应口味数据，dish_flavor的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dto.getId());

        dishFlavorService.remove(queryWrapper);
        //再添加当前提交过来的口味数据，dish_flavor的insert操作
        List<DishFlavor> flavors = dto.getFlavors();
        //重新设置id
        flavors = flavors.stream().map((item)->{
            item.setDishId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 删除套餐同时删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    public void removeWithFlavor(List<Long> ids) {
        //查询套餐状态，判断是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);

        //如果不可以删除，抛出业务异常
        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("菜品正在售卖中，删除失败");
        }

        //如果可以，就删除dish表中的dish
        this.removeByIds(ids);

        //再根据菜品id删除dish_flavor表中对应的口味
        LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(queryWrapper1);

    }

    /**
     * 修改/批量修改菜品售卖状态
     * @param status
     * @param ids
     */
    @Override
    public boolean updateStatusWithFlavor(Integer status, List<Long> ids) {
        //SQL:SELECT id,name,category_id,price,code,image,description,status,sort,create_time,update_time,create_user,update_user,is_deleted FROM dish WHERE (id IN (?,?))
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //条件查询，找出所有ids对应的菜品
        queryWrapper.in(ids != null, Dish::getId,ids);
        List<Dish> list = this.list(queryWrapper);
        //判断list是否为空
        if (list != null){
            //不是空就依次遍历，修改售卖状态
            for (Dish dish : list) {
                dish.setStatus(status);
                this.updateById(dish);
            }
            return true;
        }
        return false;
    }
}
