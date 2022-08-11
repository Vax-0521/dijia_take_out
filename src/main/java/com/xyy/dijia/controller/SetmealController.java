package com.xyy.dijia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xyy.dijia.common.R;
import com.xyy.dijia.dto.DishDto;
import com.xyy.dijia.dto.SetmealDto;
import com.xyy.dijia.entity.Category;
import com.xyy.dijia.entity.Setmeal;
import com.xyy.dijia.service.CategoryService;
import com.xyy.dijia.service.SetmealDishService;
import com.xyy.dijia.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐方法
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());

        setmealService.saveWithDish(setmealDto);

        return R.success("新增成功");
    }

    /**
     * 分页查询方法
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //对象浅拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage);

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据name进行模糊查询like
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //对象浅拷贝(protected List<T> records;  泛型不一样所以records要忽略掉，不拷贝records)
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        //重新设置records集合
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象浅拷贝
            BeanUtils.copyProperties(item,setmealDto);

            //获得分类id
            Long categoryId = item.getCategoryId();
            //根据id查询name
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //获得分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 根据ids删除套餐
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam List<Long> ids){
        log.info("ids:{}",ids.toString());

        setmealService.removeWithDish(ids);

        return R.success("删除成功");
    }

    /**
     * 根据id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        log.info("id为：",id);

        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        log.info("套餐数据：{}",setmeal.toString());

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 修改套餐售卖状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updatestatus(@PathVariable Integer status, @RequestParam List<Long> ids){
        log.info("ids:{} ,status",ids,status);

        if (setmealService.updateStatusWithDish(status,ids)) {
            return R.success("套餐的售卖状态更改成功！");
        }else {
            return R.error("售卖状态更改失败！");
        }
    }

    /**
     * 修改套餐方法
     * @param dto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto dto){
        log.info(dto.toString());

        setmealService.updateWithDish(dto);
        return R.success("修改成功");
    }




}
