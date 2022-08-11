package com.xyy.dijia.dto;

import com.xyy.dijia.entity.Dish;
import com.xyy.dijia.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //菜品对应的口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName; //菜品分类

    private Integer copies;
}
