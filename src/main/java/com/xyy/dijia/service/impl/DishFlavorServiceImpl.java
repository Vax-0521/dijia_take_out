package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.DishFlavor;
import com.xyy.dijia.mapper.DishFlavorMapper;
import com.xyy.dijia.service.DishFlavorService;
import com.xyy.dijia.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
