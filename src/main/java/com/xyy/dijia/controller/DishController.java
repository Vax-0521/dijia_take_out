package com.xyy.dijia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyy.dijia.common.R;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.entity.Category;
import com.xyy.dijia.entity.Dish;
import com.xyy.dijia.entity.DishFlavor;
import com.xyy.dijia.service.CategoryService;
import com.xyy.dijia.service.DishFlavorService;
import com.xyy.dijia.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品方法
     * @param dto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dto){
        log.info(dto.toString());

        dishService.saveWithFlavor(dto);

        //一、全部清理，使用redis清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //二、精确清理，使用redis精确清理某个菜品分类下的缓存
        String key = "dish_" + dto.getCategoryId() + "_" + dto.getStatus();
        redisTemplate.delete(key);

        return R.success("新增成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> pageInfo2 = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象浅拷贝
        BeanUtils.copyProperties(pageInfo,pageInfo2,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            //对象浅拷贝
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        pageInfo2.setRecords(list);

        return R.success(pageInfo2);
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("id为：",id);

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品方法
     * @param dto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dto){
        log.info(dto.toString());

        dishService.updateWithFlavor(dto);

        //一、全部清理，使用redis清理所有菜品的缓存数据
        //Set keys = redisTemplate.keys("dish_*");
        //redisTemplate.delete(keys);

        //二、精确清理，使用redis精确清理某个菜品分类下的缓存
        String key = "dish_" + dto.getCategoryId() + "_" + dto.getStatus();
        redisTemplate.delete(key);

        return R.success("修改成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        log.info(dish.getCategoryId().toString());
//
//        //构造查询条件对象
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //只查状态为1的，即只查起售状态
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info(dish.getCategoryId().toString());
        //先初始化list集合
        List<DishDto> dishDtoList = null;

        //先拼接一个动态的key      dish_****_****
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null){
            //如果不为空说明缓存中存在，不需要查询数据库，直接返回
            return R.success(dishDtoList);
        }

        //构造查询条件对象
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //只查状态为1的，即只查起售状态
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            //对象浅拷贝
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60l, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

    /**
     * 根据ids删除菜品和菜品对应的关联数据
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam List<Long> ids){
        log.info("ids:{}",ids.toString());

        dishService.removeWithFlavor(ids);

        return R.success("删除成功");
    }

    /**
     * 修改售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updatestatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("ids:{} ,status",ids,status);

        if (dishService.updateStatusWithFlavor(status,ids)) {
            return R.success("菜品的售卖状态更改成功！");
        }else {
            return R.error("售卖状态更改失败！");
        }
    }



}
