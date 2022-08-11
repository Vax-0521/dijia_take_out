package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.ShoppingCart;
import com.xyy.dijia.mapper.ShoppingCartMapper;
import com.xyy.dijia.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
