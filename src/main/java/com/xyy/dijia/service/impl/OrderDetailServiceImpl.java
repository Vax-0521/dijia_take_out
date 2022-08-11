package com.xyy.dijia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyy.dijia.entity.OrderDetail;
import com.xyy.dijia.mapper.OrderDetailMapper;
import com.xyy.dijia.service.OrderDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
