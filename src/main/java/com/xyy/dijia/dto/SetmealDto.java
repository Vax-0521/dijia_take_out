package com.xyy.dijia.dto;

import com.xyy.dijia.entity.Setmeal;
import com.xyy.dijia.entity.SetmealDish;
import com.xyy.dijia.entity.Setmeal;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
