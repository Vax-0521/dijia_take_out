package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.common.CustomException;
import com.xyy.dijia.entity.Category;
import com.xyy.dijia.entity.Dish;
import com.xyy.dijia.entity.Setmeal;
import com.xyy.dijia.mapper.CategoryMapper;
import com.xyy.dijia.service.CategoryService;
import com.xyy.dijia.service.DishService;
import com.xyy.dijia.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategoryService {


    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删除前需要判断
     * @param id
     */
    @Override
    public void removeById1(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        //查询当前分类是否关联了菜品，如果关联抛出异常
        if (count > 0){
            //关联，抛出业务异常
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> queryWrapper2 = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        queryWrapper2.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(queryWrapper2);
        //查询当前分类是否关联了套餐，如果关联抛出异常
        if (count > 0){
            //关联，抛出业务异常
            throw new CustomException("当前分类关联了套餐，不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
