package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.common.CustomException;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.dto.SetmealDto;
import com.xyy.dijia.entity.Dish;
import com.xyy.dijia.entity.DishFlavor;
import com.xyy.dijia.entity.Setmeal;
import com.xyy.dijia.entity.SetmealDish;
import com.xyy.dijia.mapper.SetmealMapper;
import com.xyy.dijia.service.SetmealDishService;
import com.xyy.dijia.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时保存套餐和菜品关联关系
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，setmeal表，insert操作
        this.save(setmealDto);
        List<SetmealDish> list = setmealDto.getSetmealDishes();

        list.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐和菜品关联信息，setmeal_dish表，insert操作
        setmealDishService.saveBatch(list);
    }

    /**
     * 删除套餐同时删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //select count(*) from setmeal where id in(?,?,?) and status = 1
        //查询套餐状态，判断是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        //如果不可以删除，抛出业务异常
        int count = this.count(queryWrapper);
        if (count>0){
            throw new CustomException("套餐正在售卖中，删除失败");
        }

        //如果可以删除，先删除套餐表中的数据,setmeal
        this.removeByIds(ids);

        //再删除关系表中的数据,setmeal_dish
        //delete from setmeal_dish where setmeal_id in (?,?,?)
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    /**
     * 修改/批量修改套餐售卖状态
     * @param status
     * @param ids
     */
    @Override
    public boolean updateStatusWithDish(Integer status, List<Long> ids) {
        //SQL:select * from setmeal where (id IN (?,?))
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //条件查询，找出所有ids对应的套餐
        queryWrapper.in(ids != null, Setmeal::getId,ids);
        List<Setmeal> list = this.list(queryWrapper);
        //判断list是否为空
        if (list != null){
            //不是空就依次遍历，修改套餐售卖状态
            for (Setmeal setmeal : list) {
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
            return true;
        }
        return false;
    }

    /**
     * 修改套餐同时修改菜品对应的口味数据
     * @param dto
     */
    @Override
    public void updateWithDish(SetmealDto dto) {
        //更新setmeal表
        this.updateById(dto);
        //先清除当前套餐的对应菜品数据，setmeal_dish的delete操作
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,dto.getId());

        setmealDishService.remove(queryWrapper);
        //再添加当前提交过来的对应菜品数据，setmeal_dish的insert操作
        List<SetmealDish> dishes = dto.getSetmealDishes();
        //重新设置id
        dishes = dishes.stream().map((item)->{
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        //查询套餐基本信息，setmeal表
        Setmeal setmeal = this.getById(id);

        //对象拷贝
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询套餐对应菜品信息，setmeal_dish表
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }
}
